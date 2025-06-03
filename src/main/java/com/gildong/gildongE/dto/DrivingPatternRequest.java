package com.gildong.gildongE.dto;

public class DrivingPatternRequest {
    private String userId;
    private float drivingScore;

    public String getUserId() {return userId;}
    public void setUserId(String userId) {this.userId = userId;}

    public float getDrivingScore() {return drivingScore;}
    public void setDrivingScore(float drivingScore) {this.drivingScore = drivingScore;}
}
