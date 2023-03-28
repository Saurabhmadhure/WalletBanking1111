package com.FullStack.WalletBanking.dao.repoImplementation;

import com.FullStack.WalletBanking.api.AuthenticationRequest;
import com.FullStack.WalletBanking.api.AuthenticationResponse;
import com.FullStack.WalletBanking.dao.repository.AccountDetailsRepo;
import com.FullStack.WalletBanking.dao.repository.OtpClassRepository;
import com.FullStack.WalletBanking.dao.repository.UserRepo;
import com.FullStack.WalletBanking.entityUtility.OtpClass;
import com.FullStack.WalletBanking.model.AccountDetails;
import com.FullStack.WalletBanking.model.RegisterRequest;
import com.FullStack.WalletBanking.model.domain.Role;
import com.FullStack.WalletBanking.model.domain.User;
import com.FullStack.WalletBanking.service.EmailKafkaService;
import com.FullStack.WalletBanking.utility.GenAccountNumber;
import com.FullStack.WalletBanking.utility.SequenceGeneratorService;
import com.FullStack.WalletBanking.webConfig.Config.JwtService;
import com.FullStack.WalletBanking.webConfig.Token;
import com.FullStack.WalletBanking.webConfig.TokenRepository;
import com.FullStack.WalletBanking.webConfig.TokenType;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private   PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepo repository;
    @Autowired
    private  TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;
    @Autowired
    private SequenceGeneratorService service;
    @Autowired
    private User us;
    @Autowired
    private OtpClassRepository otpClassRepository;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private AccountDetailsRepo accountDetailsRepo;
    @Autowired
    private AccountDetails accountDetails;

    private String jwtSecret;
    @Autowired
    private EmailKafkaService emailKafkaService;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);




    public AuthenticationResponse register(RegisterRequest request) {
        try {

            String email = request.getEmail();

            Optional<AccountDetails> byEmail = accountDetailsRepo.findByDetails_Email(email);
            if (!byEmail.isPresent()) {

                us.setUserId(service.generateSequenceNumber(RegisterRequest.SEQUENCE_NAME));
                us.setEmail(request.getEmail());
                us.setRole(Role.USER);
                us.setName(request.getName());
                us.setPassword(passwordEncoder.encode(request.getPassword()));
                var savedUser = repository.save(us);
                int generatedAcc = GenAccountNumber.generateAccountNumber();
                accountDetails.setDetails(us);
                accountDetails.setBalance(0);
                accountDetails.setAccNumber(generatedAcc);



                accountDetailsRepo.save(accountDetails);

                var jwtToken = jwtService.generateToken((UserDetails) us );
                var authenticationRequest= new AuthenticationRequest();


                authenticationRequest.setEmail(request.getEmail());
                authenticationRequest.setPassword(request.getPassword());
                String mess="account Number generated is "+generatedAcc;
//                var authResponse=this.authenticate(authenticationRequest);
//                logger.info(String.valueOf(authResponse));

                saveUserToken(savedUser, jwtToken);

                logger.info("Activation email has been sent to " + request.getEmail());
                emailKafkaService.sendEmail(email);
                AuthenticationResponse authenticationResponse=new AuthenticationResponse();

                authenticationResponse.setAccNo(generatedAcc);
                authenticationResponse.setBalance(0);

                authenticationResponse.setName(request.getName());
                authenticationResponse.setEmail(request.getEmail());
                authenticationResponse.setToken(jwtToken);
                logger.info("created");

                return authenticationResponse;
            }
            else{
                logger.info("Email Id is already present please Use another");
                return  null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    public boolean verifyOTP(OtpClass otpClass) {
        OtpClass savedOtpClass = otpClassRepository.findByEmail(otpClass.getEmail());
        if (savedOtpClass == null) {
            logger.info("OtpClass not found for email: " + otpClass.getEmail());
            return false;
        }
        if (savedOtpClass.getGeneratedOTP() == null) {
            logger.info("Generated OTP is null");
            return false;
        }

        Integer generatedOTP = savedOtpClass.getGeneratedOTP();


        logger.info("Generated OTP: " + generatedOTP);
        logger.info("User-entered OTP: " + otpClass.getUserEnteredOTP());

        if(generatedOTP.equals(otpClass.getUserEnteredOTP())){
            logger.info("OTP matched");
            savedOtpClass.setUserEnteredOTP(otpClass.getUserEnteredOTP());
            savedOtpClass.setVerified(true);
            otpClassRepository.save(savedOtpClass);
            return true;
        }
        else{
            logger.info("OTP not matched");
            return false;
        }
    }



    public AuthenticationResponse authenticate(AuthenticationRequest request) throws Exception {
      try {
          this.authenticationManager.authenticate(
                  new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword())
          );

//          OtpClass savedOtpClass = otpClassRepository.findByEmail(request.getEmail());
//          if(savedOtpClass.getVerified().equals(false)){
//              return null ;
//          }
          var acount=accountDetailsRepo.findByDetails_Email(request.getEmail()).orElseThrow();
          var user=repository.findByEmail(request.getEmail()).orElseThrow();

          var jwtToken=jwtService.generateToken((UserDetails) user);

          revokeAllUserTokens(user);
          saveUserToken(user, jwtToken);

          AuthenticationResponse response =  new AuthenticationResponse();
          response.setName(acount.getDetails().getName());
          response.setBalance(acount.getBalance());
          response.setEmail(acount.getDetails().getEmail());
          response.setAccNo(acount.getAccNumber());
          response.setToken(jwtToken);
          logger.info(String.valueOf(response));
          logger.info(String.valueOf(response));
          return response;


      }
      catch (BadCredentialsException e){
          logger.info("Invalid Details !!");
          throw  new Exception("Invalid userName or password");

      }
    }
    private void saveUserToken(User user, String jwtToken) {
        Token token=new Token();
        token.setId(service.generateSequenceNumberToken(Token.SEQUENCE_TOKEN));
        token.setUser(user);
        token.setToken(jwtToken);
        token.setTokenType(TokenType.BEARER);
        token.setRevoked(false);
        token.setExpired(false);
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getUserId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
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
