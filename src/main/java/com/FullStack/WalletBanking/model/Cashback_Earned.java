package com.FullStack.WalletBanking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cashback_Earned {
    @Id
    private int _id;

    private int Cashback;

}
