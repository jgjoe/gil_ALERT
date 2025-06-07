package com.gildong.gildongE.controller;

import com.gildong.gildongE.dto.*;
import com.gildong.gildongE.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService srv;
    public NotificationController(NotificationService srv) {
        this.srv = srv;
    }

    // 새 알림 저장 (앱에서 이벤트 발생 시마다 POST)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationResponse create(@RequestBody NotificationRequest req) {
        return srv.create(req);
    }

    // 유저별 전체 알림 조회 (앱 알림 페이지 들어갈 때 GET)
    @GetMapping("/user/{userId}")
    public List<NotificationResponse> list(@PathVariable String userId) {
        return srv.listForUser(userId);
    }
}
