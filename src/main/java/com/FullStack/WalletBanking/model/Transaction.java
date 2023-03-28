package com.FullStack.WalletBanking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

//import javax.mail.Message;
import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection ="Transactions")
public class Transaction {
 @Id
 private String id;

 private int sendAmount;
 private int receiveAmount;
 private int deposited;
    public Transaction(int sendAmount, int deposited , int senderId, int receiverId, Date date, String message, String status, String cashback,int senderAvailable_balance) {
        this.sendAmount = sendAmount;
        this.senderAvailable_balance=senderAvailable_balance;
        this.deposited=deposited;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.date = date;
        this.Message = message;
        this.status = status;
        this.cashback = cashback;
     }
     private int senderAvailable_balance;

    private int senderId;
    private int receiverId;
    private Date date;
    private String Message;
    private String status;
    private String cashback;

}
