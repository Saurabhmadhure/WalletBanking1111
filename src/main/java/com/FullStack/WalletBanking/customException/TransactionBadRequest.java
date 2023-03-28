package com.FullStack.WalletBanking.customException;


public class TransactionBadRequest extends RuntimeException {

        public TransactionBadRequest(   String message) {
            super(message);
        }


}
