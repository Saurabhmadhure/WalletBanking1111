package com.FullStack.WalletBanking.utility;

import java.util.Random;

public class GenerateOTP {
    public Integer otp_generate(){
        Random temp=new Random();
        Integer min=100000;
        Integer max=900000;

        Integer randomNum=temp.nextInt(max)+min;
        return randomNum;
    }

}
