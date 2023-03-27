package com.FullStack.WalletBanking.utility;

import com.FullStack.WalletBanking.entityUtility.DbSequence;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;


@Service
public class SequenceGeneratorService {

    @Autowired
    private MongoOperations mongoOperations;

    public int generateSequenceNumber(String sequence){
        //get sequence no
        Query query=new Query(Criteria.where("id").is(sequence)) ;

        //Update the seq no.
        Update update=new Update().inc("seq",1);
        //modifiying in Doucments
        DbSequence counter=mongoOperations.findAndModify(query,update,
                options().returnNew(true).upsert(true),DbSequence.class);

        return !Objects.isNull(counter)?counter.getSeq():1;

    }
    public int generateSequenceNumber2(String sequence){
        //get sequence no
        Query query=new Query(Criteria.where("id").is(sequence)) ;

        //Update the seq no.
        Update update=new Update().inc("seq",1);
        //modifiying in Doucments
        DbSequence counter=mongoOperations.findAndModify(query,update,
                options().returnNew(true).upsert(true),DbSequence.class);

        return !Objects.isNull(counter)?counter.getSeq():1;

    }
    public int generateSequenceNumberToken(String sequence){
        //get sequence no
        Query query=new Query(Criteria.where("id").is(sequence)) ;

        //Update the seq no.
        Update update=new Update().inc("seq",1);
        //modifiying in Doucments
        DbSequence counter=mongoOperations.findAndModify(query,update,
                options().returnNew(true).upsert(true),DbSequence.class);

        return !Objects.isNull(counter)?counter.getSeq():1;

    }

}
