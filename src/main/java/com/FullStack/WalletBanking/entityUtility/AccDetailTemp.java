package com.FullStack.WalletBanking.entityUtility;

import com.FullStack.WalletBanking.model.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccDetailTemp {

    private int _id;

    private int accNumber;
    private double balance;
    private User details;







}
