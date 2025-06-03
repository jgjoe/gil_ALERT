package com.gildong.gildongE.service;

import com.gildong.gildongE.model.CarModel;
import com.gildong.gildongE.repository.CarModelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarModelService {

    private final CarModelRepository carModelRepository;

    public CarModelService(CarModelRepository carModelRepository) {
        this.carModelRepository = carModelRepository;
    }

    public List<CarModel> getAllCarModels() {
        return carModelRepository.findAll();
    }

    public List<CarModel> getCarModelsByManufacturer(String manufacturer) {
        return carModelRepository.findByManufacturer(manufacturer);
    }

    public CarModel createCarModel(CarModel carModel) {
        return carModelRepository.save(carModel);
    }

    public List<CarModel> getCarModelsByModelName(String modelName) {
        return carModelRepository.findByModelName(modelName);
    }

}
