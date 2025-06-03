package com.gildong.gildongE.repository;

import com.gildong.gildongE.model.DrivingPattern;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DrivingPatternRepository extends MongoRepository<DrivingPattern, String> {
    List<DrivingPattern> findByUserId(String userId);
    // 기록일 기준 오름차순 조회
    List<DrivingPattern> findByUserIdOrderByRecordedAtAsc(String userId);
}
