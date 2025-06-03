package com.gildong.gildongE.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.gildong.gildongE.model.AuthProvider;
import java.time.LocalDateTime;

@Data
@Document(collection = "driving_pattern")
public class DrivingPattern {
    @Id
    private String id;
    private String userId;
    private float drivingScore;
    private LocalDateTime recordedAt;
}