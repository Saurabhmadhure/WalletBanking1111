package com.FullStack.WalletBanking.controller;

import com.FullStack.WalletBanking.EmailService.EmailServiceImpl;
import com.FullStack.WalletBanking.request_response_Helper.BalanceResponse;
import com.FullStack.WalletBanking.request_response_Helper.DepositRequest;
import com.FullStack.WalletBanking.request_response_Helper.DepositResponse;
import com.FullStack.WalletBanking.customException.TransactionBadRequest;
import com.FullStack.WalletBanking.dao.repository.TransactionRepository;
import com.FullStack.WalletBanking.model.AccountDetails;
import com.FullStack.WalletBanking.model.Transaction;
import com.FullStack.WalletBanking.request_response_Helper.SendMoneyResponse;
import com.FullStack.WalletBanking.service.WalletOperations;
import com.FullStack.WalletBanking.webConfig.Config.LogoutService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@CrossOrigin(origins = "*")
public class AccountController {

    @Autowired
    private WalletOperations walletOperations;
    @Autowired
    private LogoutService logoutService;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private EmailServiceImpl emailService;
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);


    @GetMapping("/info/{accNumber}")
    public AccountDetails showUserInfo(@PathVariable int accNumber) {
        return walletOperations.showInfo(accNumber);
    }

    @GetMapping("/{acc}")
    public BalanceResponse showBal(@PathVariable int acc) {
        return walletOperations.bal(acc);
    }

    @PostMapping("/logout")
    public String log_Out(HttpServletRequest request,
                          HttpServletResponse response,
                          Authentication authentication) {
        logoutService.logout(request, response, authentication);
        return "Logout SuccessFully";

    }

//    @DeleteMapping(path = "/del/{id}")
//    public String delAccount(@PathVariable int id){
//        return walletOperations.deleteAccount(id);
//    }

    @PostMapping(path = "/deposit")
    public DepositResponse amountDeposit(@RequestBody DepositRequest depositRequest) {
        return walletOperations.deposit(depositRequest);

    }

    @PostMapping("/send")
    public ResponseEntity<SendMoneyResponse> transferMoney(@RequestBody Transaction transaction) throws TransactionBadRequest {
        return walletOperations.sendMoney(transaction);

    }

    @GetMapping("/transaction/{accNo}")
    public List<Transaction> printEntities(@PathVariable int accNo) throws JsonProcessingException {
        return walletOperations.userTransaction(accNo);

    }


    @GetMapping("/cashback/{accountNumber}")
    public ResponseEntity<?> accountCashback(@PathVariable int accountNumber) throws JsonProcessingException {
        return walletOperations.totalCashbackEarned(accountNumber);
    }


}
