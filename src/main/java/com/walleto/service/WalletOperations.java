package com.walleto.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.walleto.exception.TransactionBadRequest;
import com.walleto.model.controller.*;
import com.walleto.model.entity.AccountDetails;
import com.walleto.model.entity.CashbackEarned;
import com.walleto.model.entity.Transaction;
import com.walleto.repository.AccountDetailsRepo;
import com.walleto.repository.CashbackEarnedRepo;
import com.walleto.repository.TransactionRepo;
import com.walleto.utility.Cashback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.*;

@Service
public class WalletOperations {
    @Autowired
    private AccountDetailsRepo accountDetailsRepo;
    @Autowired
    private AccountDetails accountDetails;


    @Autowired
    private TransactionRepo transactionRepo;
    @Autowired
    private CashbackEarnedRepo cashbackEarnedRepo;


    private static final Logger logger = LoggerFactory.getLogger(WalletOperations.class);

    int balance;

    public static Map<Integer, AccountDetails> map1 = new HashMap<>();
    public static List<String> transactions = new ArrayList<>();




    //    @ExceptionHandler(Exception.class)
    @ExceptionHandler(value = TransactionBadRequest.class)
    public ResponseEntity<TransactionBadRequest> customException(TransactionBadRequest str) {
        return new ResponseEntity<>(str, HttpStatus.NOT_FOUND);

    }

    @Transactional
    public ResponseEntity<SendMoneyResponse> sendMoney(Transaction transaction) throws TransactionBadRequest {

        AccountDetails sender = accountDetailsRepo.findById(transaction.getSenderId()).get();
        AccountDetails receiver = accountDetailsRepo.findById(transaction.getReceiverId()).get();

        validateAccounts(sender, receiver);

        transaction.setDate(new Date(Calendar.getInstance().getTime().getTime()));
        String id = UUID.randomUUID().toString();
        transaction.setId(id);

        int senderBalance = sender.getBalance();
        int transferringAmount = transaction.getSendAmount();
        int deductedbalance = senderBalance - transferringAmount;

        validateAmount(transaction.getSendAmount(), senderBalance);


        int cashback = generateCashback(transaction.getSendAmount());

        updateSender(sender, cashback, deductedbalance);
        updateReceiver(receiver, transferringAmount);
        updateSenderCashback(sender, cashback);
        updateTransferTransactions(id, sender, receiver, transaction, cashback,senderBalance);

        SendMoneyResponse response = createSendMoneyResponse(transaction, sender, receiver, cashback, deductedbalance);
        response.setTransaction_id(id);

        logger.info(String.format("$$ -> Transaction Completed --> %s", transaction));
        return ResponseEntity.ok(response);
    }

    private void updateReceiver(AccountDetails receiver, int transferringAmount) {
        receiver.setBalance(receiver.getBalance() + transferringAmount);
        accountDetailsRepo.save(receiver);

    }

    private void updateSender(AccountDetails sender, int cashback, int deductedbalance) {
        logger.info(String.valueOf(cashback));

        sender.setBalance(deductedbalance + cashback);
        sender.setIsRedeemed(cashback + " is redeemed");
        logger.info(String.valueOf(sender));
        accountDetailsRepo.save(sender);
    }

    private SendMoneyResponse createSendMoneyResponse(Transaction transaction, AccountDetails sender, AccountDetails receiver, int cashback, int senderAvailableBal) {
        SendMoneyResponse sendMoneyResponse = new SendMoneyResponse();

        sendMoneyResponse.setSendAmount(transaction.getSendAmount());
        sendMoneyResponse.setSenderAvailable_balance(senderAvailableBal+cashback);
        sendMoneyResponse.setSenderId(sender.getAccNumber());
        sendMoneyResponse.setReceiverId(receiver.getAccNumber());
        sendMoneyResponse.setMessage("Transferred to Acc. No: [" + receiver.getAccNumber() + "]");
        sendMoneyResponse.setDate(new Date(Calendar.getInstance().getTime().getTime()));
        sendMoneyResponse.setCashback("₹ " + cashback);
        sendMoneyResponse.setStatus("Success");
        return sendMoneyResponse;
    }



    private void validateAccounts(AccountDetails sender, AccountDetails receiver) throws TransactionBadRequest {
        if (sender == null || receiver == null || sender.getAccNumber() == receiver.getAccNumber()) {
            logger.info("No wallet for sender or receiver");
            throw new TransactionBadRequest("Sender and receiver accounts cannot be the same");
        }
    }

    private void validateAmount(int amount, int senderBalance) throws TransactionBadRequest {
        if (amount <= 0) {
            throw new TransactionBadRequest("Amount should be more than 0");
        }
        if (senderBalance < amount) {
            throw new TransactionBadRequest("Not having sufficient balance");
        }
    }

    private int generateCashback(int amount) {
        return Cashback.generateCashback(amount);
    }


    private void updateSenderCashback(AccountDetails sender, int cashback) {
        int amount = sender.getBalance();
        Optional<CashbackEarned> optionalSenderCashback = cashbackEarnedRepo.findById(sender.getAccNumber());
        CashbackEarned senderCashback = optionalSenderCashback.orElse(new CashbackEarned(sender.getAccNumber(), 0));
        int previousCashback = senderCashback.getCashback();
        int totalCashback = previousCashback + cashback;
        senderCashback.setCashback(totalCashback);
        cashbackEarnedRepo.save(senderCashback);
    }

    private void updateTransferTransactions(String id, AccountDetails sender, AccountDetails receiver, Transaction transaction, int cashback,int senderBalance) {


        Transaction senderTransaction = new Transaction();
        senderTransaction.setId(id);
        senderTransaction.setSenderId(sender.getAccNumber());
        senderTransaction.setReceiverId(receiver.getAccNumber());
        senderTransaction.setSendAmount(transaction.getSendAmount());
        senderTransaction.setCashback("₹ " + cashback);
        senderTransaction.setStatus("SUCCESS");
        senderTransaction.setDate(new Date(Calendar.getInstance().getTime().getTime()));
        senderTransaction.setMessage("Transferred to Acc. No: [" + receiver.getAccNumber() + "]");
        senderTransaction.setSenderAvailable_balance(senderBalance);
        sender.getTransactions().add(senderTransaction);

        Transaction receiverTransaction = new Transaction();
        receiverTransaction.setId(UUID.randomUUID().toString());
        receiverTransaction.setSenderId(sender.getAccNumber());
        receiverTransaction.setReceiverId(receiver.getAccNumber());
        receiverTransaction.setReceiveAmount(transaction.getSendAmount());
        receiverTransaction.setCashback("");
        receiverTransaction.setStatus("SUCCESS");
        receiverTransaction.setMessage("Received from Acc. No: [" + sender.getAccNumber() + "]");
        receiverTransaction.setDate(new Date(Calendar.getInstance().getTime().getTime()));
        receiver.getTransactions().add(receiverTransaction);
        receiver.setBalance(receiver.getBalance() + transaction.getSendAmount());

        accountDetailsRepo.save(sender);
        accountDetailsRepo.save(receiver);

    }




    public BalanceResponse bal(int acc) {
        AccountDetails temp = accountDetailsRepo.findById(acc).get();
        BalanceResponse balanceResponse = new BalanceResponse();
        balanceResponse.setAvailableBalance(temp.getBalance());
        return balanceResponse;
    }

//    public List<Transaction> allList(){
//        return allTransactions;
//    }

    public List<Transaction> userTransaction(int acc) {
        AccountDetails temp = accountDetailsRepo.findById(acc).get();
        return temp.getTransactions();

    }
    public String deleteAccount(int id) {
        Optional<AccountDetails> wallet = accountDetailsRepo.findById(id);
        if (accountDetailsRepo.findById(id).isPresent()) {
            accountDetailsRepo.delete(wallet.get());
            return "Account Deleted Succesfully";
        } else {
            return "No account Found" + id;
        }
    }

    public AccountDetails showInfo(int accNumber) {
        AccountDetails temp = accountDetailsRepo.findById(accNumber).get();
        return temp;
    }

    public DepositResponse deposit(DepositRequest depositRequest) {
        AccountDetails temp = accountDetailsRepo.findById(depositRequest.getAccountNo()).get();
        Transaction balTransaction = new Transaction();
        int v = temp.getBalance() + depositRequest.getAmount();

        temp.setAccNumber(depositRequest.getAccountNo());
        temp.setBalance(v);
        temp.getTransactions().add(balTransaction);
        logger.info(String.valueOf(temp.getBalance()));

        balTransaction.setDeposited(depositRequest.getAmount());
        balTransaction.setSenderId(temp.getAccNumber());
        balTransaction.setStatus("SUCCESS");
        balTransaction.setId(UUID.randomUUID().toString());
        balTransaction.setDate(new Date(Calendar.getInstance().getTime().getTime()));
        balTransaction.setMessage("₹" + depositRequest.getAmount() + " DEPOSITED");
        balTransaction.setSenderAvailable_balance(v);
        transactionRepo.save(balTransaction);
        accountDetailsRepo.save(temp);

        DepositResponse response = new DepositResponse();
        response.setDeposited_Amount(depositRequest.getAmount());
        response.setAvailable_Balance(v);
        logger.info(String.valueOf(v));

        return response;
    }


    public ResponseEntity<?> totalCashbackEarned(int accountNumber) throws JsonProcessingException {
        try {
            Optional<CashbackEarned> cashbackAccount = cashbackEarnedRepo.findById(accountNumber);
            int cashback = cashbackAccount.get().getCashback();
            CashbackResponse cashbackResponse = new CashbackResponse();
            cashbackResponse.setTotal_Cashback_Earned(cashback);
            return new ResponseEntity<>(cashbackResponse, HttpStatus.OK);
        } catch (Exception e) {
            String message = "No Cashback ";
            logger.error(message, e);
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }}
}
