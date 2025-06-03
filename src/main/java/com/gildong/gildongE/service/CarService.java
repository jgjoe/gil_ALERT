package com.gildong.gildongE.service;

import com.gildong.gildongE.dto.CarRequest;
import com.gildong.gildongE.dto.CarResponse;
import com.gildong.gildongE.model.Car;
import com.gildong.gildongE.repository.CarRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarService {
    private final CarRepository carRepository;
    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public CarResponse registerCar(CarRequest req) {
        // DTO → Entity
        Car entity = new Car();
        entity.setUserId(req.getUserId());
        entity.setModelName(req.getModelName());
        entity.setCarNumber(req.getCarNumber());
        entity.setCreatedAt(LocalDateTime.now());

        Car saved = carRepository.save(entity);

        // Entity → DTO
        CarResponse resp = new CarResponse();
        resp.setId(saved.getId());
        resp.setUserId(saved.getUserId());
        resp.setModelName(saved.getModelName());
        resp.setCarNumber(saved.getCarNumber());
        resp.setCreatedAt(saved.getCreatedAt());
        return resp;
    }

    public List<CarResponse> getCarsByUserId(String userId) {
        return carRepository.findByUserId(userId).stream()
                .map(saved -> {
                    CarResponse dto = new CarResponse();
                    dto.setId(saved.getId());
                    dto.setUserId(saved.getUserId());
                    dto.setModelName(saved.getModelName());
                    dto.setCarNumber(saved.getCarNumber());
                    dto.setCreatedAt(saved.getCreatedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
