package com.FullStack.WalletBanking.kafkaServices;

import com.FullStack.WalletBanking.utility.GenerateOTP;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
@Service
public class EmailKafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic = "email-topic";

    public EmailKafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEmail(String to) {
        GenerateOTP generateOTP = new GenerateOTP();
        Integer otp = generateOTP.otp_generate();
        String message = "To: " + to + ", OTP: " + otp.toString();
        kafkaTemplate.send(topic, message);
    }
}


