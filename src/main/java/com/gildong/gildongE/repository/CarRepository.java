package com.gildong.gildongE.repository;

import com.gildong.gildongE.model.Car;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface CarRepository extends MongoRepository<Car, String> {
    List<Car> findByUserId(String userId);
}