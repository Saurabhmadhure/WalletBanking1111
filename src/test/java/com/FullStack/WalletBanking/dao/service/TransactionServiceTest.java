package com.FullStack.WalletBanking.dao.service;

import com.FullStack.WalletBanking.customException.TransactionBadRequest;
import com.FullStack.WalletBanking.dao.repository.AccountDetailsRepo;
import com.FullStack.WalletBanking.model.Transaction;
import com.FullStack.WalletBanking.request_response_Helper.SendMoneyResponse;
import com.FullStack.WalletBanking.service.WalletOperations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    @Autowired
    private WalletOperations walletOperations ;
@Mock
private AccountDetailsRepo accountDetailsRepo;

    @Test
    public void sendMoney_Success() throws TransactionBadRequest {

             Transaction transaction = new Transaction();
            transaction.setSenderId(1);
            transaction.setReceiverId(2);
            transaction.setSendAmount(500);

        WalletOperations mockedWalletOperations = Mockito.mock(WalletOperations.class);
        Mockito.when(mockedWalletOperations.sendMoney(Mockito.any(Transaction.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));

         ResponseEntity<SendMoneyResponse> response = mockedWalletOperations.sendMoney(transaction);

             assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void sendMoney_SenderNotFound() throws NullPointerException{
        Transaction transaction = new Transaction();
        transaction.setSenderId(1);
        transaction.setReceiverId(2);
        transaction.setSendAmount(500);

        assertThrows(NullPointerException.class, () -> {
            walletOperations.sendMoney(transaction);
        });
    }

    @Test
    public void sendMoney_ReceiverNotFound() throws TransactionBadRequest {
        Transaction transaction = new Transaction();
        transaction.setSenderId(1);
        transaction.setReceiverId(0);
        transaction.setSendAmount(500);

        assertThrows(NullPointerException.class, () -> walletOperations.sendMoney(transaction));
    }

    @Test
    public void sendMoney_SenderSameAsReceiver() throws TransactionBadRequest {

        Transaction transaction = new Transaction();
        transaction.setSenderId(1);
        transaction.setReceiverId(1);
        transaction.setSendAmount(500);


        assertThrows(NullPointerException.class, () -> walletOperations.sendMoney(transaction));
    }

    @Test
    public void sendMoney_ZeroAmount()throws TransactionBadRequest {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setSenderId(1);
        transaction.setReceiverId(2);
        transaction.setSendAmount(0);


        assertThrows(NullPointerException.class, () -> walletOperations.sendMoney(transaction));
    }

    @Test
    public void sendMoney_NotEnoughBalance()  throws NullPointerException{

        Transaction transaction = new Transaction();
        transaction.setSenderId(1);
        transaction.setReceiverId(2);
        transaction.setSendAmount(10000);

        assertThrows(NullPointerException.class, () -> walletOperations.sendMoney(transaction));
    }
}

