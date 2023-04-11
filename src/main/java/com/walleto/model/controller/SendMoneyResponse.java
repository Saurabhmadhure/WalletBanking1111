package com.walleto.model.controller;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
public class SendMoneyResponse {
    @Id
    private String transaction_id;

    private int sendAmount;



    private int senderAvailable_balance;

    private int senderId;
    private int receiverId;
    private Date date;
    private String Message;
    private String status;
    private String cashback;
}
