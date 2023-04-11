package com.walleto.repository;
import com.walleto.model.entity.AccountDetails;

import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
 public interface AccountDetailsRepo extends MongoRepository<AccountDetails,Integer> {

 Optional<AccountDetails> findByDetails_Email(String email);



}
