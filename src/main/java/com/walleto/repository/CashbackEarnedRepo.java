package com.walleto.repository;

import com.walleto.model.entity.CashbackEarned;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashbackEarnedRepo extends MongoRepository<CashbackEarned,Integer> {

}
