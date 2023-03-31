package com.FullStack.WalletBanking.kafkaServices;

import com.FullStack.WalletBanking.dao.repository.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
@Service
public class EmailKafkaConsumerService {

    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = "email-topic", groupId = "email-group")
    public void listenEmail(String message) throws MessagingException {
        String[] parts = message.split(", ");
        String to = parts[0].substring(4);
        Integer otp = Integer.parseInt(parts[1].substring(5));
        emailService.sendEmail(to, otp);
    }
}

