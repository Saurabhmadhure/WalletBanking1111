package com.FullStack.WalletBanking.service;

import com.FullStack.WalletBanking.customException.TransactionBadRequest;
import com.FullStack.WalletBanking.dao.repository.AccountDetailsRepo;
import com.FullStack.WalletBanking.dao.repository.CashbackEarnedRepo;
import com.FullStack.WalletBanking.dao.repository.TransactionRepository;
import com.FullStack.WalletBanking.model.AccountDetails;
import com.FullStack.WalletBanking.model.Cashback_Earned;
import com.FullStack.WalletBanking.model.Transaction;
import com.FullStack.WalletBanking.request_response_Helper.*;
import com.FullStack.WalletBanking.utility.Cashback;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    private TransactionRepository transactionRepository;
    @Autowired
    private CashbackEarnedRepo cashbackEarnedRepo;


    private static final Logger logger = LoggerFactory.getLogger(WalletOperations.class);

    int balance;

    public static Map<Integer, AccountDetails> map1 = new HashMap<>();
    public static List<String> transactions = new ArrayList<>();


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
//        String deposit = " THE AMOUNT " + v + "IS DEPOSITED";

        balTransaction.setDeposited(depositRequest.getAmount());
        balTransaction.setSenderId(temp.getAccNumber());
        balTransaction.setStatus("SUCCESS");
        balTransaction.setId(UUID.randomUUID().toString());
        balTransaction.setDate(new Date(Calendar.getInstance().getTime().getTime()));
        balTransaction.setMessage("₹" + depositRequest.getAmount() + " DEPOSITED");
        transactionRepository.save(balTransaction);
        accountDetailsRepo.save(temp);

        DepositResponse response = new DepositResponse();
        response.setDeposited_Amount(depositRequest.getAmount());
        response.setAvailable_Balance(v);

        return response;
    }

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

        int senderBalance= sender.getBalance();
        int transferringAmount=transaction.getSendAmount();
        int deductedbalance=senderBalance-transferringAmount;

        validateAmount(transaction.getSendAmount(), senderBalance);

        int cashback = generateCashback(transaction.getSendAmount());

        updateSender(sender, cashback,deductedbalance);
        updateReceiver(receiver,transferringAmount);
        updateSenderCashback(sender,cashback);
        updateTransferTransactions(id,sender, receiver, transaction, cashback);

        SendMoneyResponse response = createSendMoneyResponse(transaction, sender, receiver, cashback, deductedbalance);
        response.setTransaction_id(id);
        logger.info(String.format("$$ -> Transaction Completed --> %s", transaction));
        return ResponseEntity.ok(response);
    }

    private void updateReceiver(AccountDetails receiver, int transferringAmount) {
       receiver.setBalance(receiver.getBalance()+transferringAmount);
       accountDetailsRepo.save(receiver);

    }

    private SendMoneyResponse createSendMoneyResponse(Transaction transaction, AccountDetails sender, AccountDetails receiver, int cashback, int senderAvailableBal) {
        SendMoneyResponse sendMoneyResponse = new SendMoneyResponse();

        sendMoneyResponse.setSendAmount(transaction.getSendAmount());
        sendMoneyResponse.setSenderAvailable_balance(senderAvailableBal);
        sendMoneyResponse.setSenderId(sender.getAccNumber());
        sendMoneyResponse.setReceiverId(receiver.getAccNumber());
        sendMoneyResponse.setMessage("Transferred to Acc. No: [" + receiver.getAccNumber() + "]");
        sendMoneyResponse.setDate(new Date(Calendar.getInstance().getTime().getTime()));
        sendMoneyResponse.setCashback("₹ " + cashback);
        sendMoneyResponse.setStatus("Success");
        return sendMoneyResponse;
    }



    private AccountDetails findAccountDetailsById(int accountId) throws TransactionBadRequest {
        Optional<AccountDetails> optionalAccount = accountDetailsRepo.findById(accountId);
        if (!optionalAccount.isPresent()) {
            throw new TransactionBadRequest("No wallet found for account: " + accountId);
        }
        return optionalAccount.get();
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

//    private void updateSender(AccountDetails sender, int cashback,int amount) {
//        logger.info(String.valueOf(amount));
////        int amount = sender.getBalance();
//         sender.setBalance(amount+cashback);
//        sender.setIsRedeemed(cashback + " is redeemed");
//    }
    private void updateSender(AccountDetails sender, int cashback, int deductedbalance) {
        sender.setBalance(deductedbalance + cashback);
        sender.setIsRedeemed(cashback + " is redeemed");
        accountDetailsRepo.save(sender);
    }

    private void updateSenderCashback(AccountDetails sender,int cashback) {
        int amount = sender.getBalance();
        Optional<Cashback_Earned> optionalSenderCashback = cashbackEarnedRepo.findById(sender.getAccNumber());
        Cashback_Earned senderCashback = optionalSenderCashback.orElse(new Cashback_Earned(sender.getAccNumber(), 0));
        int previousCashback = senderCashback.getCashback();
        int totalCashback = previousCashback + cashback;
        senderCashback.setCashback(totalCashback);
        cashbackEarnedRepo.save(senderCashback);
    }

    private void updateTransferTransactions(String id,AccountDetails sender, AccountDetails receiver, Transaction transaction, int cashback) {


        Transaction senderTransaction = new Transaction();
        senderTransaction.setId(id);
        senderTransaction.setSenderId(sender.getAccNumber());
        senderTransaction.setReceiverId(receiver.getAccNumber());
        senderTransaction.setSendAmount(transaction.getSendAmount());
        senderTransaction.setCashback("₹ " + cashback);
        senderTransaction.setStatus("SUCCESS");
        senderTransaction.setDate(new Date(Calendar.getInstance().getTime().getTime()));
        senderTransaction.setMessage("Transferred to Acc. No: [" + receiver.getAccNumber() + "]");
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
//        allTransactions.add(senderTransaction);
//        allTransactions.add((receiverTransaction));

                accountDetailsRepo.save(sender);
                accountDetailsRepo.save(receiver);

    }


        public BalanceResponse showbalance(int acc_number) {

        for (Integer key : map1.keySet()) {

            accountDetails = map1.get(key);
            if (accountDetails.getAccNumber() == acc_number) {
                balance = accountDetails.getBalance();
                logger.info("YOUR BALANCE IS => Rs" + accountDetails.getBalance());

                BalanceResponse balanceResponse = new BalanceResponse();
                balanceResponse.setAvailableBalance(accountDetails.getBalance());
                return balanceResponse;
            }

        }
        return null;
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


    public ResponseEntity<?> totalCashbackEarned(int accountNumber) throws JsonProcessingException {
//        Optional<Cashback_Earned> cashbackAccount = cashbackEarnedRepo.findById(accountNumber);
//        int cashback=cashbackAccount.get().getCashback();
//        CashbackResponse cashbackResponse=new CashbackResponse();
//        cashbackResponse.setTotal_Cashback_Earned(cashback);
//
//        return new ResponseEntity<>(cashbackResponse, HttpStatus.OK);
        Optional<Cashback_Earned> cashbackAccount = cashbackEarnedRepo.findById(accountNumber);

        return new ResponseEntity<>(cashbackAccount.get().getCashback(), HttpStatus.OK);


    }
}
