package com.FullStack.WalletBanking.dao.repository;

import com.FullStack.WalletBanking.model.domain.User;

import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepo extends MongoRepository<User,Integer> {

    Optional<User> findByEmail(String email);
}
