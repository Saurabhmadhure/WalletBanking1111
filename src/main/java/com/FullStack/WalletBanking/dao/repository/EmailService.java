package com.FullStack.WalletBanking.dao.repository;


import jakarta.mail.MessagingException;

public interface EmailService {
    String  sendEmail(String to, Integer otp) throws MessagingException;
}
