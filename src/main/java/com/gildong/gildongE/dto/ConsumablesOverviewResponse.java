package com.gildong.gildongE.dto;

import java.util.List;

public class ConsumablesOverviewResponse {
    private List<ConsumableResponse> all;   // 전체 소모품 리스트
    private String nextDueDate;             // YYYYMMDD 형식, 가장 가까운 교체 예정일

    public ConsumablesOverviewResponse() {}

    public List<ConsumableResponse> getAll() {
        return all;
    }
    public void setAll(List<ConsumableResponse> all) {
        this.all = all;
    }

    public String getNextDueDate() {
        return nextDueDate;
    }
    public void setNextDueDate(String nextDueDate) {
        this.nextDueDate = nextDueDate;
    }
}
