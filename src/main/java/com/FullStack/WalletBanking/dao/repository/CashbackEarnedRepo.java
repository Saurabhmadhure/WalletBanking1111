package com.FullStack.WalletBanking.dao.repository;

import com.FullStack.WalletBanking.model.Cashback_Earned;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashbackEarnedRepo extends MongoRepository<Cashback_Earned,Integer> {

}
