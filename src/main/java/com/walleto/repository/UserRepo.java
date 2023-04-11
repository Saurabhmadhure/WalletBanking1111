package com.walleto.repository;

import com.walleto.model.entity.User;

import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepo extends MongoRepository<User,Integer> {

    Optional<User> findByEmail(String email);
}
