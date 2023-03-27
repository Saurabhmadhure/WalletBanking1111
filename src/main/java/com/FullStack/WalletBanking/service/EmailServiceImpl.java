package com.FullStack.WalletBanking.service;

import com.FullStack.WalletBanking.dao.repository.EmailService;
import com.FullStack.WalletBanking.entityUtility.AccDetailTemp;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;



    private static final Logger logger = LoggerFactory.getLogger(AccDetailTemp.class);

    public String sendEmail(String to, Integer otp) throws MessagingException {

          MimeMessage message=javaMailSender.createMimeMessage();
       logger.info("Generated OTP : " + otp);
       logger.info("sending to :"+to);

        MimeMessageHelper helper=new MimeMessageHelper(message,true);
        helper.setFrom("Walleto <saurabhmadhure364@gmail.com>");
        helper.setTo(to);
        helper.setSubject("Walleto Verification Otp");
        helper.setText("Your OTP is  :- "+ otp);

        if (javaMailSender != null && helper != null) {
            javaMailSender.send(message);
            return "Mail Sent Successfully...";
        } else {
            return "Error while Sending Mail";
        }

    }}


