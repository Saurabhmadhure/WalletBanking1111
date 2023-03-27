package com.FullStack.WalletBanking.dao.repository;
import com.FullStack.WalletBanking.model.AccountDetails;

import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
 public interface AccountDetailsRepo extends MongoRepository<AccountDetails,Integer> {

 Optional<AccountDetails> findByDetails_Email(String email);



}
