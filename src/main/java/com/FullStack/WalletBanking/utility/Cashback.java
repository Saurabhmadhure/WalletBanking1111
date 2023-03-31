package com.FullStack.WalletBanking.utility;

import java.util.Random;

public class Cashback {
    public static int generateCashback(int amount){

        Random random=new Random();
        System.out.println(  (random.nextInt(amount)*0.010));

        return (int) (random.nextInt(amount)*0.020)+10;

    }
}
