package com.walleto.dao.service;

import com.walleto.exception.TransactionBadRequest;
import com.walleto.model.controller.SendMoneyResponse;
import com.walleto.model.entity.AccountDetails;
import com.walleto.model.entity.Transaction;
import com.walleto.repository.AccountDetailsRepo;
import com.walleto.repository.CashbackEarnedRepo;
import com.walleto.repository.TransactionRepo;
import com.walleto.service.WalletOperations;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

//@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceTest {
    @InjectMocks
    private WalletOperations walletOperations;
    @Mock
    private CashbackEarnedRepo cashbackEarnedRepo;
    @Mock
    private TransactionRepo transactionRepo;
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
    @Test
    public void testSendMoney() throws TransactionBadRequest {
        int senderAccNumber = 123;
        int receiverAccNumber = 456;
        int senderBalance = 100;
        int transferringAmount = 50;

        AccountDetails sender = new AccountDetails();
        sender.setAccNumber(senderAccNumber);
        sender.setBalance(senderBalance);

        AccountDetails receiver = new AccountDetails();
        receiver.setAccNumber(receiverAccNumber);

        Transaction transaction = new Transaction();
        transaction.setSenderId(senderAccNumber);
        transaction.setReceiverId(receiverAccNumber);
        transaction.setSendAmount(transferringAmount);

        // creating a mock WalletOperations instance
        WalletOperations walletOperationsMock = Mockito.mock(WalletOperations.class);

        // setting up the mock to use the accountDetailsRepo instance
//        Mockito when(walletOperations.AccountDetailsRepo()).thenReturn(accountDetailsRepo);

        // mock the behavior of the accountDetailsRepo instance
        Mockito.when(accountDetailsRepo.findById(senderAccNumber)).thenReturn(Optional.of(sender));
        Mockito.when(accountDetailsRepo.findById(receiverAccNumber)).thenReturn(Optional.of(receiver));

        // calling the sendMoney() method on the mock WalletOperations instance
        ResponseEntity<SendMoneyResponse> responseEntity = walletOperationsMock.sendMoney(transaction);

        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        SendMoneyResponse response = responseEntity.getBody();
        Assert.assertNotNull(response);

        Assert.assertEquals(senderAccNumber, response.getSenderId());
        Assert.assertEquals(receiverAccNumber, response.getReceiverId());
        Assert.assertEquals(transferringAmount, response.getSendAmount());
        Assert.assertEquals("Transferred to Acc. No: [" + receiverAccNumber + "]", response.getMessage());
        Assert.assertEquals("Success", response.getStatus());
        Assert.assertNotNull(response.getTransaction_id());
    }


}

