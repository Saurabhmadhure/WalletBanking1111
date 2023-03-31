package com.FullStack.WalletBanking.webConfig;

import com.FullStack.WalletBanking.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@Document(collection = "Tokens")
public class Token {
 @Transient
 public static final String SEQUENCE_TOKEN="user_sequence";

   @Id
    public Integer id;


    public String token;


    public  TokenType tokenType = TokenType.BEARER;

    public boolean revoked;

    public boolean expired;


    public User user;
}