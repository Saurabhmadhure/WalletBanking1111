package com.walleto.service;

import com.walleto.exception.AccountCreationException;
import com.walleto.exception.SendMessageException;
import com.walleto.model.controller.AuthenticationRequest;
import com.walleto.model.controller.AuthenticationResponse;
import com.walleto.model.entity.AccountDetails;
import com.walleto.model.entity.OtpClass;
import com.walleto.model.entity.RegisterRequest;
import com.walleto.model.entity.User;
import com.walleto.repository.*;
import com.walleto.service.kafka.KafkaProducer;
import com.walleto.utility.GenAccountNumber;
import com.walleto.model.entity.Token;
import com.walleto.webconfig.TokenType;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Component
public class AuthenticationService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepo repository;
    @Autowired
    private TokenRepo tokenRepo;
    private final AuthenticationManager authenticationManager;
    @Autowired
    private SequenceGeneratorService service;
    @Autowired
    private User us;
    @Autowired
    private OtpClassRepo otpClassRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private AccountDetailsRepo accountDetailsRepo;
    @Autowired
    private AccountDetails accountDetails;

    private String jwtSecret;
    @Autowired
    private KafkaProducer kafkaProducerService;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);


    public AuthenticationResponse register(RegisterRequest request) {
        try {
            String email = request.getEmail();
            Optional<AccountDetails> byEmail = accountDetailsRepo.findByDetails_Email(email);
            if (byEmail.isEmpty()) {
                User user = createUser(request);
                AccountDetails accountDetails = createAccountDetails(user);
                String jwtToken = generateToken(user);
                saveUserToken(user, jwtToken);
                sendActivationEmail(request.getEmail());
                return createAuthenticationResponse(user, accountDetails, jwtToken);
            } else {
                logger.info("Email Id " + email + " is already present. Please use another.");
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private User createUser(RegisterRequest request) throws AccountCreationException {
        User user = new User();
        user.setUserId(service.generateSequenceNumber(RegisterRequest.SEQUENCE_NAME));
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        try {
            return userRepo.save(user);
        } catch (DataAccessException e) {
            logger.error("Failed to create user: " + e.getMessage());
            throw new AccountCreationException("Failed to create user");
        }
    }


    private AccountDetails createAccountDetails(User user) throws AccountCreationException {
        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setDetails(user);
        accountDetails.setBalance(0);
        accountDetails.setAccNumber(GenAccountNumber.generateAccountNumber());
        try {
            return accountDetailsRepo.save(accountDetails);
        } catch (DataAccessException e) {
            logger.error("Failed to create account details: " + e.getMessage());
            throw new AccountCreationException("Failed to create account details");
        }
    }


    private String generateToken(User user) {
        return jwtService.generateToken((UserDetails) user);
    }



    private void sendActivationEmail(String email) throws SendMessageException {
        kafkaProducerService.sendMessage(email);
        logger.info("Activation email has been sent to " + email);
    }

    private AuthenticationResponse createAuthenticationResponse(User user, AccountDetails accountDetails, String jwtToken) {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setAccNo(accountDetails.getAccNumber());
        authenticationResponse.setBalance(accountDetails.getBalance());
        authenticationResponse.setName(user.getName());
        authenticationResponse.setEmail(user.getEmail());
        authenticationResponse.setToken(jwtToken);
        logger.info("Authentication response created");
        return authenticationResponse;
    }


    public boolean verifyOTP(OtpClass otpClass) {
        OtpClass savedOtpClass = otpClassRepo.findByEmail(otpClass.getEmail());
        logger.info("Info Of otp Repo" + savedOtpClass.getGeneratedOTP());
        logger.info(String.valueOf(savedOtpClass));
        if (savedOtpClass.getGeneratedOTP() == null) {
            logger.info("Generated OTP is null");
            return false;
        }

        Integer generatedOTP = savedOtpClass.getGeneratedOTP();


        logger.info("Generated OTP: " + generatedOTP);
        logger.info("User-entered OTP: " + otpClass.getUserEnteredOTP());

        if (generatedOTP.equals(otpClass.getUserEnteredOTP())) {
            logger.info("OTP matched");
            savedOtpClass.setUserEnteredOTP(otpClass.getUserEnteredOTP());
            savedOtpClass.setVerified(true);
            otpClassRepo.save(savedOtpClass);
            return true;
        } else {
            logger.info("OTP not matched");
            return false;
        }
    }


    public AuthenticationResponse authenticate(AuthenticationRequest request) throws Exception {
        try {
            this.authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            var acount = accountDetailsRepo.findByDetails_Email(request.getEmail()).orElseThrow();
            var user = repository.findByEmail(request.getEmail()).orElseThrow();

            var jwtToken = jwtService.generateToken((UserDetails) user);

            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);

            AuthenticationResponse response = new AuthenticationResponse();
            response.setName(acount.getDetails().getName());
            response.setBalance(acount.getBalance());
            response.setEmail(acount.getDetails().getEmail());
            response.setAccNo(acount.getAccNumber());
            response.setToken(jwtToken);
            logger.info(String.valueOf(response));
            logger.info(String.valueOf(response));
            return response;


        } catch (BadCredentialsException e) {
            logger.info("Invalid Details !!");
            throw new Exception("Invalid userName or password");

        }
    }
    private void saveUserToken(User user, String jwtToken) {
        Token token = new Token();
        token.setId(service.generateSequenceNumberToken(Token.SEQUENCE_TOKEN));
        token.setUser(user);
        token.setToken(jwtToken);
        token.setTokenType(TokenType.BEARER);
        token.setRevoked(false);
        token.setExpired(false);
        tokenRepo.save(token);
    }


    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepo.findAllValidTokenByUser(user.getUserId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepo.saveAll(validUserTokens);
    }

    public boolean validateToken(String token) {
        try {
//            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(token);

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public ResponseEntity<String> acceptingToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");

        if (validateToken(token)) {
            return ResponseEntity.ok("Success!");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    public AuthenticationService(final AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
}
