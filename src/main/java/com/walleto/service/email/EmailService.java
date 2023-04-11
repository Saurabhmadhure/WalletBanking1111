package com.walleto.service.email;


import com.walleto.model.controller.EmailMessage;
import jakarta.mail.MessagingException;

public interface EmailService {
    String sendEmail(EmailMessage message) throws MessagingException;
}
