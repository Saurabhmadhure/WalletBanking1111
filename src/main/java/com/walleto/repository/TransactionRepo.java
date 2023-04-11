package com.walleto.repository;

import com.walleto.model.entity.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionRepo extends MongoRepository<Transaction,String> {
//    List<Transaction> findByUserId(int userId);
}
