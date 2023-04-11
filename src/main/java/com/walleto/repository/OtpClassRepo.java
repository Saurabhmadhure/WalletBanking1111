package com.walleto.repository;

import com.walleto.model.entity.OtpClass;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpClassRepo extends MongoRepository<OtpClass,Integer > {
 OtpClass findByEmail(String email);


 }

