package com.FullStack.WalletBanking.controller;

import com.FullStack.WalletBanking.dao.repository.AccountDetailsRepo;
import com.FullStack.WalletBanking.dao.repository.TransactionRepository;
import com.FullStack.WalletBanking.model.AccountDetails;
import com.FullStack.WalletBanking.model.Transaction;
import com.FullStack.WalletBanking.model.User;
import com.FullStack.WalletBanking.request_response_Helper.DepositRequest;
import com.FullStack.WalletBanking.request_response_Helper.DepositResponse;
import com.FullStack.WalletBanking.service.WalletOperations;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
 @RunWith(MockitoJUnitRunner.class)
public class AccountTestCases {
    private MockMvc mockMvc;

    @Mock
    private AccountDetailsRepo accountDetailsRepository;


    @InjectMocks
    private AccountController accountController;
    @InjectMocks
    private WalletOperations walletOperations;

    @Mock
    private AccountDetailsRepo accountDetailsRepo;

    @Mock
    private TransactionRepository transactionRepository;

    @Test
    public void deposit_createsTransactionObject() {
        AccountDetails account = new AccountDetails(1234, 500, new User("Saurabh Madhure"), new ArrayList<Transaction>());
        accountDetailsRepo.save(account);

        DepositRequest depositRequest = new DepositRequest(1234, 100); // account number 1234, deposit amount 100
        walletOperations.deposit(depositRequest);

        List<Transaction> transactions = transactionRepository.findAll();
        assertEquals(1, transactions.size());

        Transaction transaction = transactions.get(0);
        assertEquals("SUCCESS", transaction.getStatus());
        assertEquals(1234, transaction.getSenderId());
        assertEquals(100, transaction.getDeposited());
        assertNotNull(transaction.getDate());
        assertEquals("100 DEPOSITED", transaction.getMessage());
    }

    @Test
    public void deposit_updatesAccountBalance() {
        AccountDetails account = new AccountDetails(1234, 500, new User("Saurabh Madhure"), new ArrayList<Transaction>());
        accountDetailsRepo.save(account);

        DepositRequest depositRequest = new DepositRequest(1234, 100); // account number 1234, deposit amount 100
        walletOperations.deposit(depositRequest);

        AccountDetails updatedAccount = accountDetailsRepo.findById(1234).get();
        assertEquals(600, updatedAccount.getBalance());
    }

    @Test
    public void deposit_returnsDepositResponse() {
        AccountDetails account = new AccountDetails(1234, 500, new User("Saurabh Madhure"), new ArrayList<Transaction>());
        accountDetailsRepo.save(account);

        DepositRequest depositRequest = new DepositRequest(1234, 100); // account number 1234, deposit amount 100
        DepositResponse response = walletOperations.deposit(depositRequest);

        assertNotNull(response);
        assertEquals(100, response.getDeposited_Amount());
    }





    @Test
    public void testShowUserInfo() throws Exception {
        // Mock account details data
        int accNumber = 123456;
        User user = User.builder().userId(1).email("saurabh@gmail.com").name("Saurabh").password("Password123").build();
        AccountDetails accountDetails = AccountDetails.builder().accNumber(accNumber).balance(1000).details(user).build();
        Mockito.when(accountDetailsRepository.findById(accNumber)).thenReturn(Optional.ofNullable(accountDetails));

        mockMvc.perform(get("/user/showUserInfo/{accNumber}", accNumber))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) content().contentType(MediaType.APPLICATION_JSON))
                .andExpect((ResultMatcher) jsonPath("$.accountNumber").value(accNumber))
                .andExpect((ResultMatcher) jsonPath("$.balance").value(1000))
                .andExpect((ResultMatcher) jsonPath("$.details.userId").value(1))
                .andExpect((ResultMatcher) jsonPath("$.details.email").value("saurabh@gmail.com"))
                .andExpect((ResultMatcher) jsonPath("$.details.name").value("Saurabh"))
                .andExpect((ResultMatcher) jsonPath("$.details.password").value("Password123"))
                .andExpect((ResultMatcher) jsonPath("$.details.userRole").value("USER"));

    }







}
