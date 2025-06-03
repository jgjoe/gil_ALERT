package com.gildong.gildongE.controller;

import com.gildong.gildongE.dto.CarModelRequest;
import com.gildong.gildongE.dto.CarModelResponse;
import com.gildong.gildongE.model.CarModel;
import com.gildong.gildongE.service.CarModelService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/car-models")
@CrossOrigin(origins = "*")
public class CarModelController {

    private final CarModelService carModelService;

    public CarModelController(CarModelService carModelService) {
        this.carModelService = carModelService;
    }

    @GetMapping
    public List<CarModelResponse> getAllCarModels() {
        return carModelService.getAllCarModels()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @PostMapping
    public CarModelResponse createCarModel(@RequestBody CarModelRequest request) {
        CarModel saved = carModelService.createCarModel(convertToEntity(request));
        return convertToResponse(saved);
    }

    // --- 변환 메서드 ---
    private CarModelResponse convertToResponse(CarModel carModel) {
        CarModelResponse res = new CarModelResponse();
        res.setId(carModel.getId());
        res.setManufacturer(carModel.getManufacturer());
        res.setModelName(carModel.getModelName());
        res.setModelYear(carModel.getModelYear());
        res.setFuelType(carModel.getFuelType());
        res.setBodyType(carModel.getBodyType());
        res.setManualUrl(carModel.getManualUrl());
        return res;
    }

    private CarModel convertToEntity(CarModelRequest req) {
        CarModel car = new CarModel();
        car.setManufacturer(req.getManufacturer());
        car.setModelName(req.getModelName());
        car.setModelYear(req.getModelYear());
        car.setFuelType(req.getFuelType());
        car.setBodyType(req.getBodyType());
        car.setManualUrl(req.getManualUrl());
        return car;
    }

    @GetMapping("/manufacturer/{manufacturer}")
    public List<CarModelResponse> getByManufacturer(@PathVariable String manufacturer) {
        return carModelService.getCarModelsByManufacturer(manufacturer)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @GetMapping("/model/{modelName}")
    public List<CarModelResponse> getByModelName(@PathVariable String modelName) {
        return carModelService.getCarModelsByModelName(modelName)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

}
