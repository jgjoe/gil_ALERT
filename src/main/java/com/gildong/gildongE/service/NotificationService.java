package com.gildong.gildongE.service;

import com.gildong.gildongE.dto.NotificationRequest;
import com.gildong.gildongE.dto.NotificationResponse;
import java.util.List;

public interface NotificationService {
    NotificationResponse create(NotificationRequest req);
    List<NotificationResponse> listForUser(String userId);
}
