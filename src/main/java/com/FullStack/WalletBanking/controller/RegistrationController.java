package com.FullStack.WalletBanking.controller;

import com.FullStack.WalletBanking.dao.repository.AccountDetailsRepo;
import com.FullStack.WalletBanking.entityUtility.AccDetailTemp;
import com.FullStack.WalletBanking.entityUtility.OtpClass;
 import com.FullStack.WalletBanking.model.domain.User;

import com.FullStack.WalletBanking.model.RegisterRequest;

import com.FullStack.WalletBanking.service.EmailServiceImpl;
import com.FullStack.WalletBanking.dao.repoImplementation.AuthenticationService;
 import com.FullStack.WalletBanking.api.AuthenticationRequest;
import com.FullStack.WalletBanking.api.AuthenticationResponse;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class RegistrationController {
    @Autowired
    private AuthenticationService service;

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EmailServiceImpl emailService;
    @Autowired

    private AccountDetailsRepo accountDetailsRepo;






    private static final Logger logger = LoggerFactory.getLogger(AccDetailTemp.class);
    @PostMapping(path = "/register")
    public AuthenticationResponse registerData(@RequestBody RegisterRequest request ){

        return  service.register(request );


    }
    @PostMapping(path="/verify")
    public boolean verifyUsingOtp(@RequestBody  OtpClass otpClassP){
        return service.verifyOTP(otpClassP);
    }


    @PostMapping(path = "/login")
    public AuthenticationResponse login(@RequestBody AuthenticationRequest request) throws Exception {

        logger.debug("Login request received for email: {}", request.getEmail());


        return service.authenticate(request);

//
//        return ResponseEntity.ok(service.authenticate(request));

    }

    @GetMapping("/acc/info")
    public   String  showUserInfo( @RequestBody User auth){
        return  auth.getName();
    }

//    @PostMapping("/sendmail")
//    public String sendingMail(@RequestBody String to) throws MessagingException {
//
//        System.out.println("Sending email to: " + to);
//
//        return  emailService.sendEmail(to);
//
//    }





}
