package com.FullStack.WalletBanking.dao.repository;

import com.FullStack.WalletBanking.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionRepository extends MongoRepository<Transaction,String> {
//    List<Transaction> findByUserId(int userId);
}
