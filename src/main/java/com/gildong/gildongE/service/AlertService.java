package com.gildong.gildongE.service;

import com.gildong.gildongE.dto.AlertDto;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * AlertService는 SSE(SERVER-SENT EVENTS) 기반으로
 * 특정 사용자(userId)에게 실시간 알림을 전송하기 위한 인터페이스입니다.
 *
 * - subscribe(userId): 클라이언트가 SSE 연결을 맺을 때 호출합니다.
 * - pushAlert(alert): 서버 로직(스케줄러, Change Stream 등)에서 알림이 발생하면 호출합니다.
 */
public interface AlertService {

    /**
     * 클라이언트(프론트엔드, 모바일 앱 등)에서 SSE 연결을 요청할 때 호출합니다.
     * @param userId 알림을 받을 사용자 ID
     * @return SseEmitter 인스턴스. 이 객체를 통해 서버가 클라이언트로 이벤트를 전송합니다.
     */
    SseEmitter subscribe(String userId);

    /**
     * 알림을 생성하고 특정 사용자에게 전송합니다.
     * @param alert AlertDto 객체 (id, userId, type, title, message, createdAt 등 포함)
     */
    void pushAlert(AlertDto alert);

    /**
     * (선택) 운영 중 특정 사용자의 연결을 강제로 종료할 때 호출할 수 있습니다.
     * 예: 로그아웃 시, 또는 타임아웃 후 수동으로 제거할 때 등
     * @param userId 연결을 끊을 사용자 ID
     */
    void unsubscribe(String userId);
}
