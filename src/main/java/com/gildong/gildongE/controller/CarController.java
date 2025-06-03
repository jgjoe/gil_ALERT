package com.gildong.gildongE.controller;

import com.gildong.gildongE.dto.CarRequest;
import com.gildong.gildongE.dto.CarResponse;
import com.gildong.gildongE.service.CarService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {
    private final CarService carService;
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @PostMapping
    public ResponseEntity<CarResponse> registerCar(@RequestBody CarRequest req) {
        CarResponse resp = carService.registerCar(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CarResponse>> getCarsByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(carService.getCarsByUserId(userId));
    }
}
