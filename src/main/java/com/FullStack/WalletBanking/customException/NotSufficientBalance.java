package com.FullStack.WalletBanking.customException;

public class NotSufficientBalance extends RuntimeException {

    public NotSufficientBalance() {
        super("TransactionBadRequest");

    }
}
