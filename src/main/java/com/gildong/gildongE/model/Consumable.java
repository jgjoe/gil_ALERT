package com.gildong.gildongE.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "consumables")
public class Consumable {
    @Id
    private String id;
    private String userId;
    private String carModel;
    private String carNumber;

    // 교체 예정일
    private String engineOilDate;
    private String batteryDate;
    private String coolantDate;
    private String transmissionOilDate;
    private String brakeOilDate;
    private String airconFilterDate;

    // 교체한 날짜
    private String engineOilChangedDate;
    private String batteryChangedDate;
    private String coolantChangedDate;
    private String transmissionOilChangedDate;
    private String brakeOilChangedDate;
    private String airconFilterChangedDate;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
