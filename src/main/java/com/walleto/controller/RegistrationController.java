package com.walleto.controller;

import com.walleto.model.entity.OtpClass;
import com.walleto.repository.AccountDetailsRepo;
import com.walleto.service.AuthenticationService;
import com.walleto.service.email.EmailServiceImpl;
import com.walleto.model.controller.AuthenticationRequest;
import com.walleto.model.controller.AuthenticationResponse;
import com.walleto.model.entity.RegisterRequest;
import com.walleto.model.entity.User;
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

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    @PostMapping(path = "/register")
    public AuthenticationResponse registerData(@RequestBody RegisterRequest request ){

        return  service.register(request );


    }
    @PostMapping(path="/verify")
    public boolean verifyUsingOtp(@RequestBody OtpClass otpClassP){
        return service.verifyOTP(otpClassP);
    }


    @PostMapping(path = "/login")
    public AuthenticationResponse login(@RequestBody AuthenticationRequest request) throws Exception {

        logger.debug("Login request received for email: {}", request.getEmail());


        return service.authenticate(request);
    }
    @GetMapping("/acc/info")
    public   String  showUserInfo( @RequestBody User auth){
        return  auth.getName();
    }






}
