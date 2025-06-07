package com.gildong.gildongE.service;

import com.gildong.gildongE.dto.AlertDto;
import com.gildong.gildongE.dto.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * AlertServiceImpl은 AlertService 인터페이스를 구현한 클래스입니다.
 * 내부적으로 ConcurrentHashMap을 사용해 사용자별(Single or Multiple) SseEmitter를 관리합니다.
 *
 *  - subscribe(userId): 새로운 SseEmitter를 생성한 뒤 userId 키로 맵에 저장
 *  - pushAlert(alert): userId에 매핑된 emitter 리스트를 찾아 event를 전송
 *  - unsubscribe(userId): 해당 userId의 모든 emitter를 제거(선택)
 */
@Service
public class AlertServiceImpl implements AlertService {

    private static final Logger logger = LoggerFactory.getLogger(AlertServiceImpl.class);

    /**
     * key: userId
     * value: 해당 사용자에게 이벤트를 보낼 SseEmitter 객체들의 리스트
     *
     * 여러 디바이스(웹, 모바일 등)에서 동시에 로그인한 경우,
     * userId별로 복수의 emitter가 생길 수 있으므로 List 로 관리합니다.
     */
    private final Map<String, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();
    private final NotificationService notificationService;

    public AlertServiceImpl(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * 클라이언트가 SSE 연결을 요청하면 호출됩니다.
     * - 새로운 SseEmitter 객체 생성
     * - 타임아웃 설정 (여기서는 6시간)
     * - onCompletion, onTimeout, onError 핸들러 등록
     * - emitters 맵에 사용자 ID(userId) 아래로 추가
     *
     * @param userId 알림을 받을 사용자 ID
     * @return 생성된 SseEmitter
     */
    @Override
    public SseEmitter subscribe(String userId) {
        // 타임아웃을 원하는 만큼 설정 가능합니다. (예: Long.MAX_VALUE → 사실상 timeout 없음)
        // 아래 예시는 6시간(= 6 * 60 * 60 * 1000 ms)으로 설정한 것입니다.
        long timeoutMillis = 6L * 60L * 60L * 1000L;

        SseEmitter emitter = new SseEmitter(timeoutMillis);

        // 사용자별로 복수의 emitter를 관리하기 위해 List로 묶어서 관리
        emitters.computeIfAbsent(userId, key -> new CopyOnWriteArrayList<>()).add(emitter);

        // 연결이 완료되거나, 에러나 타임아웃 발생 시 맵에서 제거하도록 한다.
        emitter.onCompletion(() -> {
            removeEmitter(userId, emitter);
            logger.debug("SSE connection completed: userId={}, emitter={}", userId, emitter);
        });
        emitter.onTimeout(() -> {
            removeEmitter(userId, emitter);
            logger.debug("SSE connection timed out: userId={}, emitter={}", userId, emitter);
        });
        emitter.onError((ex) -> {
            removeEmitter(userId, emitter);
            logger.warn("SSE connection error: userId={}, emitter={}, exception={}", userId, emitter, ex.getMessage());
        });

        // (선택) 연결 직후 “연결 성공” 이벤트를 던져 줘서,
        // 클라이언트 쪽에서 구독이 제대로 되었음을 확인하게 할 수도 있습니다.
        try {
            SseEmitter.SseEventBuilder initEvent = SseEmitter.event()
                    .id("INIT-" + Instant.now().toEpochMilli())
                    .name("INIT")
                    .data("SSE connection established for userId=" + userId);
            emitter.send(initEvent);
        } catch (IOException ioex) {
            // 이미 연결에 문제가 있다면 제거
            removeEmitter(userId, emitter);
            logger.warn("Failed to send INIT event to userId={} -> {}", userId, ioex.getMessage());
        }

        return emitter;
    }

    /**
     * 특정 사용자(userId)에게 알림을 보낸다.
     * 1) emitters 맵에서 userId 키로 등록된 모든 SseEmitter를 꺼낸다.
     * 2) 각 emitter로 event 전송을 시도한다.
     * 3) 전송 중 IOException 발생 시 해당 emitter를 맵에서 제거한다.
     *
     * @param alert AlertDto 객체 (id, userId, type, title, message, createdAt 등)
     */
    @Override
    public void pushAlert(AlertDto alert) {
        logger.info("AlertService: 알림 전송 시도 -> {}", alert);

        String userId = alert.getUserId();
        List<SseEmitter> userEmitters = emitters.get(userId);

        if (userEmitters == null || userEmitters.isEmpty()) {
            logger.debug("No active emitters for userId={}, skip sending alert", userId);
            return;
        }

        // 사용자별로 여러 SseEmitter가 존재할 수 있으므로, 매번 새로운 이벤트 빌더를 생성한다.
        for (SseEmitter emitter : userEmitters) {
            try {
                SseEmitter.SseEventBuilder event = SseEmitter.event()
                        .id(alert.getId())
                        .name("ALERT")
                        .data(alert);
                emitter.send(event);
            } catch (IOException e) {
                // IO 오류가 나면 해당 emitter를 제거한다.
                removeEmitter(userId, emitter);
                logger.warn("Failed to send alert to userId={}, emitter removed. alertId={}, error={}",
                        userId, alert.getId(), e.getMessage());
            }
        }

        // 서버 로그에도 남겨둔다.
        logger.info("[ALERT][PUSHED] userId={} type={} title={} message={}",
                userId, alert.getType(), alert.getTitle(), alert.getMessage());

        // DB 저장 호출
        NotificationRequest req = new NotificationRequest();
        req.setUserId(alert.getUserId());
        req.setTitle(categoryFromType(alert.getType()));
        req.setMessage(alert.getMessage());
        notificationService.create(req);
    }

    /**
     * 사용자가 로그아웃하거나, 수동으로 SSE 연결을 끊을 때 호출할 수 있습니다.
     * 특정 userId 하에 있는 모든 emitter를 제거합니다.
     *
     * @param userId 연결을 끊을 사용자 ID
     */
    @Override
    public void unsubscribe(String userId) {
        List<SseEmitter> userEmitters = emitters.remove(userId);
        if (userEmitters != null) {
            for (SseEmitter emitter : userEmitters) {
                emitter.complete();
            }
        }
        logger.debug("All SSE emitters removed for userId={}", userId);
    }

    /**
     * 내부 헬퍼: 하나의 emitter만 제거하고 싶을 때 사용합니다.
     * @param userId 해당 사용자 ID
     * @param emitter 제거할 SseEmitter
     */
    private void removeEmitter(String userId, SseEmitter emitter) {
        List<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters != null) {
            userEmitters.remove(emitter);
            if (userEmitters.isEmpty()) {
                emitters.remove(userId);
            }
        }
    }

    // 알림 type 기반으로 카테고리 문자열 반환
    private String categoryFromType(String type) {
        return switch (type) {
            case "DRIVING_SCORE_LOW", "DRIVING_SCORE_WEEKLY_AVG" -> "안전";
            case "CONSUMABLE_REPLACED" -> "차량 소모품";
            case "CONSUMABLE_DUE_SOON" -> "차량 점검";
            default -> "기타";
        };
    }
}
