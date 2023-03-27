package com.FullStack.WalletBanking.customException;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException( String s) {
        super("User id not found : "  );
    }
}
