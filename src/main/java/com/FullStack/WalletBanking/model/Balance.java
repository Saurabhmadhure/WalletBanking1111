package com.FullStack.WalletBanking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class Balance {
@Id
    private int accountNo;
    private int amount;

}

