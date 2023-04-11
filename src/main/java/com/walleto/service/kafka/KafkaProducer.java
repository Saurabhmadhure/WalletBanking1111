package com.walleto.service.kafka;

import com.walleto.exception.SendMessageException;
import com.walleto.model.entity.OtpClass;
import com.walleto.repository.OtpClassRepo;
import com.walleto.service.email.EmailService;
import com.walleto.utility.GenerateOTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
@Service
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic = "email-topic";

    @Autowired
    private EmailService emailService;
    @Autowired
    private OtpClassRepo otpClassRepo;
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String to) throws SendMessageException {
        GenerateOTP generateOTP = new GenerateOTP();
        Integer otp = generateOTP.otp_generate();
        OtpClass otpClass = new OtpClass(otp, to);
        otpClassRepo.save(otpClass);
        logger.info("Mail sent to " + to);
        String message = "To: " + to + ", OTP: " + otp.toString();
        try {
            kafkaTemplate.send(topic, message);
        } catch (Exception e) {
            throw new SendMessageException("Error sending message: " + e.getMessage());
        }
    }
}
