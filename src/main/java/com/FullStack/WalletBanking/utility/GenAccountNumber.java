package com.FullStack.WalletBanking.utility;

import java.util.Random;

public class GenAccountNumber {
    public static int generateAccountNumber(){
        int accNumber;
        Random random=new Random();
        int bound =1000;

        accNumber=bound*random.nextInt(bound);
        return accNumber;
    }
}
