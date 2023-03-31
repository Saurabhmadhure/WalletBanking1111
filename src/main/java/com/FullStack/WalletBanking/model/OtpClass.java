package com.FullStack.WalletBanking.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data

@NoArgsConstructor
@Document(collection ="Otp-Class")

public class OtpClass {
    private Integer generatedOTP;

    @Id
    private ObjectId id;

    private String email;
    private Integer userEnteredOTP;

    public OtpClass(Integer generatedOTP, String email ) {
        this.generatedOTP = generatedOTP;
        this.email = email;

    }
    public  Boolean verified;

}
