package com.FullStack.WalletBanking.dao.repoImplementation;

import com.FullStack.WalletBanking.api.BalanceResponse;
import com.FullStack.WalletBanking.api.DepositResponse;
import com.FullStack.WalletBanking.customException.NotSufficientBalance;
import com.FullStack.WalletBanking.customException.TransactionBadRequest;
import com.FullStack.WalletBanking.dao.repository.AccountDetailsRepo;
import com.FullStack.WalletBanking.dao.repository.CashbackEarned;
import com.FullStack.WalletBanking.dao.repository.TransactionRepository;
import com.FullStack.WalletBanking.model.AccountDetails;
import com.FullStack.WalletBanking.model.Balance;
import com.FullStack.WalletBanking.model.Cashback_Earned;
import com.FullStack.WalletBanking.model.Transaction;
import com.FullStack.WalletBanking.utility.Cashback;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

@Service
public class WalletOperations {
    @Autowired
    private AccountDetailsRepo accountDetailsRepo;
    @Autowired
    private AccountDetails acdetails;



    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private CashbackEarned cashbackrepo;



    private static final Logger logger = LoggerFactory.getLogger(WalletOperations.class);

    int balance;

    public static Map<Integer, AccountDetails> map1 = new HashMap<>();
    public static List<String> transactions= new ArrayList<>();


 public String deleteAccount(int id){
        Optional<AccountDetails> wallet=accountDetailsRepo.findById(id);
        if(accountDetailsRepo.findById(id).isPresent()){
            accountDetailsRepo.delete(wallet.get());
            return "Account Deleted Succesfully";
        }
        else{
            return "No account Found"+id;
        }
 }
    public AccountDetails showInfo(int accNumber){
        AccountDetails temp = accountDetailsRepo.findById(accNumber).get();
        return temp;
    }

    public  DepositResponse deposit(Balance balance) {
        AccountDetails temp = accountDetailsRepo.findById(balance.getAccountNo()).get();
        Transaction balTransaction = new Transaction();
        int v = temp.getBalance() + balance.getAmount();
        balTransaction.setDeposited(balance.getAmount());
        balTransaction.setSenderId(temp.getAccNumber());
        balTransaction.setStatus("SUCCESS");
        balTransaction.setDate(new Date(Calendar.getInstance().getTime().getTime()));
        balTransaction.setMessage("₹"+balance.getAmount() + " DEPOSITED");

        temp.setAccNumber(balance.getAccountNo());
         temp.setBalance(v);
        temp.getTransactions().add(balTransaction);
        logger.info(String.valueOf(temp.getBalance()));
        String deposit = " THE AMOUNT " + v + "IS DEPOSITED";
        transactionRepository.save(balTransaction);
        accountDetailsRepo.save(temp);
        DepositResponse response = new DepositResponse();
        response.setDeposited_Amount(balance.getAmount());
        response.setAvailable_Balance(v);

        return  response;
    }

//    @ExceptionHandler(Exception.class)
    @ExceptionHandler(value=TransactionBadRequest.class)
    public ResponseEntity<TransactionBadRequest> customException(TransactionBadRequest str){
     return new ResponseEntity<>(str,HttpStatus.NOT_FOUND);

    }


    public ResponseEntity<Transaction> sendMoney(@RequestBody Transaction transaction) throws TransactionBadRequest {

        transaction.setDate(new Date(Calendar.getInstance().getTime().getTime()));


        AccountDetails sender = accountDetailsRepo.findById(transaction.getSenderId()).get();
         AccountDetails receiver =  accountDetailsRepo.findById(transaction.getReceiverId()).get();

        if (sender == null || receiver == null||sender.getAccNumber()==receiver.getAccNumber()) {
            logger.info("No wallet for sender or receiver");
            throw new TransactionBadRequest(  "No wallet found for sender or receiver");

        }
        AccountDetails senderWallet = accountDetailsRepo.findById(sender.getAccNumber()).get();
        AccountDetails receiverWallet = accountDetailsRepo.findById(receiver.getAccNumber()).get();


        Transaction senderTransaction = new Transaction();
         senderTransaction.setSenderId(senderWallet.getAccNumber());
        senderTransaction.setReceiverId(receiverWallet.getAccNumber());
        senderTransaction.setId(UUID.randomUUID().toString());

        int amount = transaction.getSendAmount();
        if(amount==0){
            throw new TransactionBadRequest(  "Amount Should be more than 0");
        }
        else {

            if (senderWallet.getBalance() < amount) {
                throw new TransactionBadRequest(  "Not Having Sufficient Balance");
            }
            try {
                int cashb = Cashback.generateCashback(amount);
                logger.info(String.valueOf(cashb));
                logger.info("Congrulations You got Cashback of " + cashb + " rupees");
                int senderAvailableBal=(senderWallet.getBalance() - amount) + cashb;
                senderWallet.setBalance(senderAvailableBal);
                senderWallet.setIsRedeemed(cashb + " is redemmed");
                Optional<Cashback_Earned> optionalSenderCashback = cashbackrepo.findById(senderWallet.getAccNumber());
                Cashback_Earned senderCashback;
                if (optionalSenderCashback.isPresent()) {
                    senderCashback = optionalSenderCashback.get();
                } else {
                    senderCashback = new Cashback_Earned(senderWallet.getAccNumber(), 0);
                }                int previousCashback = senderCashback.getCashback();
                logger.info(String.valueOf(previousCashback));

                logger.info(String.valueOf(cashb));
                int totalCashback = previousCashback + cashb;
                logger.info(String.valueOf(totalCashback));
                senderCashback.setCashback(totalCashback);


                senderTransaction.setSendAmount(amount);
                senderTransaction.setSenderId(sender.getAccNumber());
                senderTransaction.setReceiverId(receiver.getAccNumber());
                senderTransaction.setCashback("₹ "+ cashb );
                senderTransaction.setStatus("SUCCESS");
                senderTransaction.setDate(new Date(Calendar.getInstance().getTime().getTime()));
                senderTransaction.setMessage("Transferred to Acc. No :[" + receiver.getAccNumber() + "]");
                transaction.setSenderAvailable_balance(senderAvailableBal);
                senderWallet.getTransactions().add(senderTransaction);
                transaction.setMessage("Transferred to Acc. No :[" + receiver.getAccNumber() + "]");
                transaction.setStatus("Success");
                transaction.setCashback("Cashback Earned of " + cashb + " rupees");

                Transaction receiverTransaction = new Transaction();
                receiverTransaction.setId(UUID.randomUUID().toString());
                receiverWallet.setBalance(receiverWallet.getBalance() + amount);
                receiverTransaction.setReceiveAmount(amount);
                receiverTransaction.setCashback("");
                receiverTransaction.setStatus("SUCCESS");
                receiverTransaction.setMessage("Received from Acc. No :[" + sender.getAccNumber() + "]");
                receiverTransaction.setDate(new Date(Calendar.getInstance().getTime().getTime()));

                receiverWallet.getTransactions().add(receiverTransaction);
                logger.info(String.format("$$ ->  Transaction  Completed --> %s", transaction));
                sender.getTransactions().add(senderTransaction);
                receiver.getTransactions().add( receiverTransaction);
//                allTransactions.add(senderTransaction);
//                allTransactions.add((receiverTransaction));

                accountDetailsRepo.save(receiverWallet);
                accountDetailsRepo.save(senderWallet);
                cashbackrepo.save(senderCashback);



                return ResponseEntity.ok(transactionRepository.save(transaction));
            } catch (NotSufficientBalance e) {
                Transaction failedTransaction = new Transaction();
                failedTransaction.setId(UUID.randomUUID().toString());
                failedTransaction.setSenderId(sender.getAccNumber());
                failedTransaction.setReceiverId(receiver.getAccNumber());
                failedTransaction.setSendAmount(amount);
                failedTransaction.setStatus("FAILED");
                failedTransaction.setMessage(e.getMessage());
                failedTransaction.setDate(new Date(Calendar.getInstance().getTime().getTime()));

                senderWallet.getTransactions().add(failedTransaction);
                accountDetailsRepo.save(senderWallet);

                throw new TransactionBadRequest( "Not Having Sufficient Balance");
            } finally {
            }
        }
    }
    public BalanceResponse showbalance(int acc_number) {

        for (Integer key : map1.keySet()) {

            acdetails = map1.get(key);
            if (acdetails.getAccNumber() == acc_number) {
                balance = acdetails.getBalance();
                logger.info("YOUR BALANCE IS => Rs" + acdetails.getBalance());

                BalanceResponse balanceResponse = new BalanceResponse();
                balanceResponse.setAvailableBalance(acdetails.getBalance());
                return  balanceResponse;
            }

        }
        return null;
    }




    public BalanceResponse bal(int acc){
         AccountDetails temp= accountDetailsRepo.findById(acc).get() ;
        BalanceResponse balanceResponse = new BalanceResponse();
        balanceResponse.setAvailableBalance(temp.getBalance());
        return  balanceResponse;
}

//    public List<Transaction> allList(){
//        return allTransactions;
//    }

    public List<Transaction> userTransaction(int acc){
        AccountDetails temp= accountDetailsRepo.findById(acc).get();
        return temp.getTransactions();

    }
    public ResponseEntity<?> totalCashbackEarned(int accountNumber) throws JsonProcessingException {
        Optional<Cashback_Earned> cashbackAccount = cashbackrepo.findById(accountNumber);

        return new ResponseEntity<>(cashbackAccount.get().getCashback(), HttpStatus.OK);
    }
}
