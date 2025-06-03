package com.gildong.gildongE.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "cars")
public class Car {
    @Id
    private String id;
    private String carNumber;
    private String modelName;
    private String userId;
    private LocalDateTime createdAt;
}