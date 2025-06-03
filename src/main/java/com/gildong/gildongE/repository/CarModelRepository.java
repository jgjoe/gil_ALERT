package com.gildong.gildongE.repository;

import com.gildong.gildongE.model.CarModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarModelRepository extends MongoRepository<CarModel, String> {
    List<CarModel> findByManufacturer(String manufacturer);
    List<CarModel> findByModelName(String modelName);
}
