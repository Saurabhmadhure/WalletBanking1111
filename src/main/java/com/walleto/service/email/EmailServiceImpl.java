package com.walleto.service.email;

import com.walleto.model.controller.EmailMessage;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

//@Service
//public class EmailServiceImpl implements EmailService {
//
//    @Autowired
//    private JavaMailSender javaMailSender;
//
//
//
//    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
//
//    public String sendEmail(String to, Integer otp) throws MessagingException {
//
//          MimeMessage message=javaMailSender.createMimeMessage();
//       logger.info("Generated OTP : " + otp);
//       logger.info("sending to :"+to);
//
//        MimeMessageHelper helper=new MimeMessageHelper(message,true);
//        helper.setFrom("Walleto <saurabhmadhure364@gmail.com>");
//        helper.setTo(to);
//        helper.setSubject("Walleto Verification Otp");
//        helper.setText("Your OTP is  :- "+ otp);
//
//        if (javaMailSender != null && helper != null) {
//            javaMailSender.send(message);
//            return "Mail Sent Successfully...";
//        } else {
//            return "Error while Sending Mail";
//        }
//
//    }
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Override
    public String sendEmail(EmailMessage message) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom("Walleto <saurabhmadhure364@gmail.com>");
            helper.setTo(message.getTo());
            helper.setSubject("Walleto Verification Otp");
            helper.setText("Your OTP is  :- "+ message.getOtp());
            javaMailSender.send(mimeMessage);
            logger.info("Mail sent to " + message.getTo());
            return "Mail sent successfully";
        } catch (MessagingException e) {
            logger.error("Error sending email: " + e.getMessage());
            throw new RuntimeException("Error sending email: " + e.getMessage());
        } catch (jakarta.mail.MessagingException e) {
            logger.error("Error sending email: " + e.getMessage());
            throw new RuntimeException("Error sending email: " + e.getMessage());
        }
    }



}


