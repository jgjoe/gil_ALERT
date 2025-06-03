package com.gildong.gildongE.dto;

import lombok.Data;

@Data
public class CarModelRequest {
    private String manufacturer;
    private String modelName;
    private String modelYear;
    private String fuelType;
    private String bodyType;
    private String manualUrl;
}
