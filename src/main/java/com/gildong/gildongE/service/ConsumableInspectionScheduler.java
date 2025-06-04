package com.gildong.gildongE.service;

import com.gildong.gildongE.dto.AlertDto;
import com.gildong.gildongE.dto.ConsumablesOverviewResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ConsumableInspectionScheduler {

    private final ConsumableService consumableService;
    private final UserService userService;
    private final AlertService alertService;

    // YYYYMMDD 파싱/포맷용
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd");

    public ConsumableInspectionScheduler(ConsumableService consumableService,
                                         UserService userService,
                                         AlertService alertService) {
        this.consumableService = consumableService;
        this.userService       = userService;
        this.alertService      = alertService;
    }

    /**
     * 매일 자정(KST)에 실행하여,
     * 각 사용자별로 “가장 가까운 교체 예정일(nextDueDate)”가
     * 오늘부터 7일 이내라면 알림을 발송한다.
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void pushUpcomingConsumableAlerts() {
        LocalDate today = LocalDate.now();
        LocalDate inSevenDays = today.plusDays(7);

        // 1) 모든 사용자 ID를 가져옴
        List<String> userIds = userService.getAllUserIds();

        for (String userId : userIds) {
            // 2) 해당 사용자의 ConsumablesOverviewResponse 얻기
            ConsumablesOverviewResponse overview =
                    consumableService.getConsumablesOverview(userId);

            String nextDueDateStr = overview.getNextDueDate();
            if (nextDueDateStr == null) {
                // 미래 예정일이 하나도 없으면 건너뛰기
                continue;
            }

            // 3) “YYYYMMDD” → LocalDate 로 파싱
            LocalDate nextDueDate;
            try {
                nextDueDate = LocalDate.parse(nextDueDateStr, fmt);
            } catch (Exception ex) {
                // 파싱 불가(형식 에러 등)이면 무시하고 넘어감
                continue;
            }

            // 4) 오늘 ~ 7일 이내라면 알림 생성
            if (!nextDueDate.isBefore(today) && !nextDueDate.isAfter(inSevenDays)) {
                // 예) today = 2025-05-20, nextDueDate = 2025-05-25 (5일 후) → 알림
                AlertDto alert = new AlertDto();
                alert.setId(UUID.randomUUID().toString());
                alert.setUserId(userId);
                alert.setType("CONSUMABLE_DUE_SOON");
                alert.setTitle("차량점검");
                alert.setMessage(
                        "가장 가까운 교체 예정일: " +
                                // “20250525” → “05월 25일”
                                (nextDueDate.getMonthValue() < 10 ? "0" + nextDueDate.getMonthValue() : nextDueDate.getMonthValue())
                                + "월 " +
                                (nextDueDate.getDayOfMonth() < 10 ? "0" + nextDueDate.getDayOfMonth() : nextDueDate.getDayOfMonth())
                                + "일"
                );
                alert.setCreatedAt(Instant.now());
                alertService.pushAlert(alert);
            }
        }
    }
}
