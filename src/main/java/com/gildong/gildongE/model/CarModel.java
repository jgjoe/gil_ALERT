package com.gildong.gildongE.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "car_models")
public class CarModel {
    @Id
    private String id;
    private String manufacturer;  // 제조사: ex) Hyundai
    private String modelName;     // 모델명: ex) Avante
    private String modelYear;     // 연식: ex) 2022
    private String fuelType;      // 연료: ex) Gasoline
    private String bodyType;      // 차종: ex) Sedan
    private String manualUrl;
}
