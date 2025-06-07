package com.gildong.gildongE.service;

import com.gildong.gildongE.dto.*;
import com.gildong.gildongE.model.Notification;
import com.gildong.gildongE.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository repo;
    private final UserService userService;

    public NotificationServiceImpl(NotificationRepository repo, UserService userService) {
        this.repo = repo;
        this.userService = userService;
    }

    @Override
    public NotificationResponse create(NotificationRequest req) {
        // 사용자 존재 검증
        userService.getUserById(req.getUserId());
        Notification n = new Notification();
        n.setUserId(req.getUserId());
        n.setTitle(req.getTitle());
        n.setMessage(req.getMessage());
        n.setCreatedAt(Instant.now());
        Notification saved = repo.save(n);
        return toDto(saved);
    }

    @Override
    public List<NotificationResponse> listForUser(String userId) {
        userService.getUserById(userId);
        return repo.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private NotificationResponse toDto(Notification n) {
        NotificationResponse r = new NotificationResponse();
        r.setId(n.getId());
        r.setUserId(n.getUserId());
        r.setTitle(n.getTitle());
        r.setMessage(n.getMessage());
        r.setCreatedAt(n.getCreatedAt());
        return r;
    }
}
