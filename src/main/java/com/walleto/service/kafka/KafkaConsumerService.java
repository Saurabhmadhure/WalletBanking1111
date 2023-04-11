package com.walleto.service.kafka;

import com.walleto.service.email.EmailService;
import com.walleto.model.controller.EmailMessage;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
@Service
public class KafkaConsumerService {



        @Autowired
        private EmailService emailService;

    @KafkaListener(topics = "email-topic", groupId = "email-group")
    public void listenMessage(String message) {
        try {
            String[] parts = message.split(", ");
            String to = parts[0].substring(4);
            Integer otp = Integer.parseInt(parts[1].substring(5));
            EmailMessage emailMessage = new EmailMessage(to, otp);
            emailService.sendEmail(emailMessage);
        } catch (MessagingException ex) {
             ex.printStackTrace();
        }
    }


}

