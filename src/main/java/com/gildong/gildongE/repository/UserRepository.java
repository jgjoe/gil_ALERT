package com.gildong.gildongE.repository;

import com.gildong.gildongE.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    @Query(value = "{ 'loginId' : { $regex: ?0, $options: 'i' } }")
    Optional<User> findByLoginId(String loginId);
    Optional<User> findByUserName(String userName);
}