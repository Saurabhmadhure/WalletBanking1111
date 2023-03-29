package com.FullStack.WalletBanking.controller;

import com.FullStack.WalletBanking.api.BalanceResponse;
import com.FullStack.WalletBanking.api.DepositResponse;
import com.FullStack.WalletBanking.customException.TransactionBadRequest;
import com.FullStack.WalletBanking.dao.repoImplementation.WalletOperations;
import com.FullStack.WalletBanking.dao.repository.TransactionRepository;
import com.FullStack.WalletBanking.entityUtility.AccDetailTemp;
import com.FullStack.WalletBanking.model.AccountDetails;
import com.FullStack.WalletBanking.model.Balance;
import com.FullStack.WalletBanking.model.Transaction;
import com.FullStack.WalletBanking.service.EmailServiceImpl;
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
public class Controller {

    @Autowired
    private WalletOperations walletOperations;
    @Autowired
    private LogoutService logoutService;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private EmailServiceImpl emailService;
    private static final Logger logger = LoggerFactory.getLogger(AccDetailTemp.class);


    @GetMapping("/info/{accNumber}")
    public   AccountDetails  showUserInfo( @PathVariable int accNumber){
        return  walletOperations.showInfo(accNumber);
    }

    @GetMapping("/{acc}")
    public BalanceResponse showBal(@PathVariable int acc){

//        logger.info("Balance availavle:-");
        return walletOperations.bal(acc);
    }


    @PostMapping("/logout")
    public String log_Out(HttpServletRequest request,
                            HttpServletResponse response,
                            Authentication authentication){
        logoutService.logout(request, response,authentication);
        return "Logout SuccessFully";

    }

//    @DeleteMapping(path = "/del/{id}")
//    public String delAccount(@PathVariable int id){
//        return walletOperations.deleteAccount(id);
//    }

    @PostMapping(path="/deposit")
    public DepositResponse amountDeposit(@ RequestBody Balance balance) {
        return walletOperations.deposit(balance);

    }
//    @GetMapping(path = "/transactions")
//    public List<Transaction> allAvailavleTransaction(){
//        logger.info("Transaction is been printed");
//        return transactionRepository.findAll();
//    }
////
    @PostMapping("/send")
    public ResponseEntity<Transaction> transferMoney(@RequestBody Transaction transaction) throws TransactionBadRequest {
        return walletOperations.sendMoney(transaction);

    }
    @GetMapping("/transaction/{accNo}")
    public List<Transaction> printEntities(@PathVariable int accNo) throws JsonProcessingException {
        return walletOperations.userTransaction(accNo);

    }




//    @PostMapping("/Demo")


    @GetMapping("/cashback/{accountNumber}")
    public ResponseEntity<?> accountCashback(@PathVariable int accountNumber) throws JsonProcessingException {
        return walletOperations.totalCashbackEarned(accountNumber);
    }


}
