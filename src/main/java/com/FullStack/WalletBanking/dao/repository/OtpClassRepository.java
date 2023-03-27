package com.FullStack.WalletBanking.dao.repository;

import com.FullStack.WalletBanking.entityUtility.OtpClass;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpClassRepository extends MongoRepository<OtpClass,Integer > {
 OtpClass findByEmail(String email);


 }

