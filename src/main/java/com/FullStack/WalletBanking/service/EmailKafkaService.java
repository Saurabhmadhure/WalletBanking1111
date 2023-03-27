package com.FullStack.WalletBanking.service;

import com.FullStack.WalletBanking.dao.repository.EmailService;
import com.FullStack.WalletBanking.dao.repository.OtpClassRepository;
import com.FullStack.WalletBanking.entityUtility.AccDetailTemp;
import com.FullStack.WalletBanking.entityUtility.OtpClass;
import com.FullStack.WalletBanking.utility.GenerateOTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
@Service
public class EmailKafkaService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic = "email-topic";

    @Autowired
    private EmailService emailService;
    @Autowired
    private OtpClassRepository otpClassRepository;
    private static final Logger logger = LoggerFactory.getLogger(AccDetailTemp.class);

    public EmailKafkaService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEmail(String to) {
        GenerateOTP generateOTP = new GenerateOTP();
        Integer otp = generateOTP.otp_generate();
        OtpClass otpClass = new OtpClass(otp, to);
        otpClassRepository.save(otpClass);
        logger.info("Mail sent to " + to);
         String message = "To: " + to + ", OTP: " + otp.toString();
        kafkaTemplate.send(topic, message);}







    @KafkaListener(topics = "email-topic", groupId = "email-group")
    public void listenEmail(String message) {
        try {
            String[] parts = message.split(", ");
            String to = parts[0].substring(4);
            Integer otp = Integer.parseInt(parts[1].substring(5));
            emailService.sendEmail(to, otp);
            System.out.println("Sending email to: " + to + ", OTP: " + otp.toString());
        } catch (MessagingException | jakarta.mail.MessagingException e) {
            // handle exception
        }
    }
}
