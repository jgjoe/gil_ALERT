package com.gildong.gildongE.controller;

import com.gildong.gildongE.dto.AlertDto;
import com.gildong.gildongE.service.AlertService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Instant;
import java.util.UUID;

/**
 * AlertController: 클라이언트가 SSE를 구독할 수 있는 엔드포인트를 제공합니다.
 *   GET /alerts/subscribe/{userId}  → SseEmitter 반환
 */
@RestController
@RequestMapping("/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    /**
     * SSE 구독 엔드포인트
     * 클라이언트는 아래 URL을 호출해 SseEmitter 객체를 얻고,
     * 서버가 보낼 알림을 실시간으로 수신할 수 있습니다.
     *
     * 가능 예시:
     *   new EventSource("http://서버주소:8080/alerts/subscribe/USER123");
     *
     * @param userId 알림을 받을 사용자 ID
     * @return SseEmitter
     */
    @GetMapping(value = "/subscribe/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable String userId) {
        return alertService.subscribe(userId);
    }

    @PostMapping("test/{userId}")
    public ResponseEntity<String> sendTestAlert(@PathVariable String userId) {
        AlertDto alert = new AlertDto();
        alert.setId(UUID.randomUUID().toString());
        alert.setUserId(userId);
        alert.setType("TEST_ALERT");
        alert.setTitle("테스트 알림");
        alert.setMessage("테스트 알림입니다.");
        alert.setCreatedAt(Instant.now());

        alertService.pushAlert(alert);
        return ResponseEntity.ok("Test alert sent to userId=" + userId);
    }
}
