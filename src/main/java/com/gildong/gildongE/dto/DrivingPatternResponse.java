package com.gildong.gildongE.dto;

import java.time.LocalDateTime;

public class DrivingPatternResponse {
    private String id;
    private String userId;
    private float drivingScore;
    private LocalDateTime recordedAt;

    public String getId() {return id;}
    public void setId(String id) {this.id = id;}

    public String getUserId() {return userId;}
    public void setUserId(String userId) {this.userId = userId;}

    public float getDrivingScore() {return drivingScore;}
    public void setDrivingScore(float drivingScore) {this.drivingScore = drivingScore;}

    public LocalDateTime getRecordedAt() {return recordedAt;}
    public void setRecordedAt(LocalDateTime recordedAt) {this.recordedAt = recordedAt;}
}
