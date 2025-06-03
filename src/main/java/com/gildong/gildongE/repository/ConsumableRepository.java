package com.gildong.gildongE.repository;

import com.gildong.gildongE.model.Consumable;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ConsumableRepository extends MongoRepository<Consumable, String> {
    List<Consumable> findByUserId(String userId);
    List<Consumable> findByUserIdOrderByCreatedAtDesc(String userId);
}