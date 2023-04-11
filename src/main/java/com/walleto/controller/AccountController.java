package com.walleto.controller;

import com.walleto.exception.TransactionBadRequest;
import com.walleto.model.entity.AccountDetails;
import com.walleto.repository.TransactionRepo;
import com.walleto.service.LogoutService;
import com.walleto.service.WalletOperations;
import com.walleto.service.email.EmailServiceImpl;
import com.walleto.model.controller.BalanceResponse;
import com.walleto.model.controller.DepositRequest;
import com.walleto.model.controller.DepositResponse;
import com.walleto.model.entity.Transaction;
import com.walleto.model.controller.SendMoneyResponse;
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
    private TransactionRepo transactionRepo;
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
