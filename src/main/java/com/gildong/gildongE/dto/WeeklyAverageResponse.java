package com.gildong.gildongE.dto;

import java.time.LocalDate;

public class WeeklyAverageResponse {
    private LocalDate weekStart;
    private double averageScore;

    public WeeklyAverageResponse() {}

    public WeeklyAverageResponse(LocalDate weekStart, double averageScore) {
        this.weekStart = weekStart;
        this.averageScore = averageScore;
    }

    public LocalDate getWeekStart() {
        return weekStart;
    }
    public void setWeekStart(LocalDate weekStart) {
        this.weekStart = weekStart;
    }

    public double getAverageScore() {
        return averageScore;
    }
    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }
}
