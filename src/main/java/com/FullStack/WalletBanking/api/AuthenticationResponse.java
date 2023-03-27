package com.FullStack.WalletBanking.api;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String token;


    private String email;
    private String name;

    private int accNo;
    private double balance;


}
