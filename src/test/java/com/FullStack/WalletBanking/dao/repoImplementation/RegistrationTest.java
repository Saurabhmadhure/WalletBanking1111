package com.FullStack.WalletBanking.dao.repoImplementation;

import com.FullStack.WalletBanking.dao.repository.AccountDetailsRepo;
import com.FullStack.WalletBanking.dao.repository.OtpClassRepository;
import com.FullStack.WalletBanking.dao.repository.UserRepo;
import com.FullStack.WalletBanking.model.AccountDetails;
import com.FullStack.WalletBanking.model.domain.Role;
import com.FullStack.WalletBanking.model.domain.User;
import com.FullStack.WalletBanking.model.RegisterRequest;
import com.FullStack.WalletBanking.service.EmailKafkaService;
import com.FullStack.WalletBanking.utility.SequenceGeneratorService;
import com.FullStack.WalletBanking.webConfig.Config.JwtService;
import com.FullStack.WalletBanking.webConfig.TokenRepository;
import com.FullStack.WalletBanking.api.AuthenticationResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.data.mongodb.core.aggregation.ConditionalOperators.Switch.CaseOperator.when;

@ExtendWith(MockitoExtension.class)
class RegistrationTest {
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepo userRepo;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private SequenceGeneratorService sequenceGeneratorService;

    @Mock
    private OtpClassRepository otpClassRepository;

    @Mock
    private AccountDetailsRepo accountDetailsRepo;

    @Mock
    private EmailKafkaService emailKafkaService;

    @InjectMocks
    private AuthenticationService authenticationService;

//    @Test
//    public void registerTest() {
//        RegisterRequest request = new RegisterRequest();
//        request.setEmail("saurabhmadhure@gmail.com");
//        request.setName("Saurabh");
//        request.setPassword("Password123");
//
//        User us = new User();
//        us.setEmail("saurabhmadhure@gmail.com");
//        us.setRole(Role.USER);
//        us.setName("Saurabh");
//        us.setPassword("Password123");
//
//        AccountDetails accountDetails = new AccountDetails();
//        accountDetails.setDetails(us);
//        accountDetails.setBalance(0);
//        accountDetails.setAccNumber(123456);
//        int generatedAcc = 123456;
//        AccountDetailsRepo accountDetailsRepo = mock(AccountDetailsRepo.class);
//
//        when(accountDetailsRepo.findByDetails_Email("saurabhmadhure@gmail.com"))
//                .thenReturn(Optional.empty());
//        when(sequenceGeneratorService.generateSequenceNumber(RegisterRequest.SEQUENCE_NAME)).thenReturn(1);
////        when(userRepo.save(any(User.class))) ;
//        when(userRepo.findByEmail(us.getEmail())).thenReturn(Optional.of(us));
//        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
////        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwtToken");
//        when(accountDetailsRepo.save(any(AccountDetails.class)))
//                .thenReturn(new AccountDetails());        doNothing().when(emailKafkaService).sendEmail(request.getEmail());
//        AuthenticationResponse response = authenticationService.register(request);
//
//        assertNotNull(response);
//        assertEquals(request.getName(), response.getName());
//        assertEquals(request.getEmail(), response.getEmail());
//        assertEquals("jwtToken", response.getToken());
//        assertEquals(generatedAcc, response.getAccNo());
//        assertEquals(0, response.getBalance());
//
//        verify(accountDetailsRepo, times(1)).findByDetails_Email(request.getEmail());
//        verify(sequenceGeneratorService, times(1)).generateSequenceNumber(RegisterRequest.SEQUENCE_NAME);
//        verify(passwordEncoder, times(1)).encode(request.getPassword());
////        verify(userRepo, times(1)).save(any(User.class));
//        verify(userRepo, times(1)).findByEmail(us.getEmail());
////        verify(jwtService, times(1)).generateToken(any(UserDetails.class));
////        verify(accountDetailsRepo, times(1)).save(any(AccountDetails.class));
//        verify(emailKafkaService, times(1)).sendEmail(request.getEmail());
//    }
@Test
public void registerTest() {
    RegisterRequest request = new RegisterRequest();
    request.setEmail("saurabhmadhure@gmail.com");
    request.setName("Saurabh");
    request.setPassword("Password123");

    User us = new User();
    us.setEmail("saurabhmadhure@gmail.com");
    us.setUserId(1);
    us.setRole(Role.USER);
    us.setName("Saurabh");
    us.setPassword("Password123");

    AccountDetails accountDetails = new AccountDetails();
    accountDetails.setDetails(us);
    accountDetails.setBalance(0);
    accountDetails.setAccNumber(123456);
    int generatedAcc = 123456;

//    when(accountDetailsRepo.findByDetails_Email("saurabhmadhure@gmail.com"))
//            .thenReturn(Optional.empty());
//    when(sequenceGeneratorService.generateSequenceNumber(RegisterRequest.SEQUENCE_NAME)).thenReturn(1);
//    when(userRepo.findByEmail(us.getEmail())).thenReturn(Optional.of(us));
//    when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
//    when(accountDetailsRepo.save(any(AccountDetails.class)))
//            .thenReturn(new AccountDetails());
//
//    doNothing().when(emailKafkaService).sendEmail(request.getEmail());
    when(userRepo.findByEmail(request.getEmail())).thenReturn(Optional.empty());
    when(accountDetailsRepo.findByDetails_Email(request.getEmail())).thenReturn(Optional.empty());
    when(sequenceGeneratorService.generateSequenceNumber(RegisterRequest.SEQUENCE_NAME)).thenReturn(1);
    when(userRepo.save(any(User.class))).thenReturn(us);
    when(accountDetailsRepo.save(any(AccountDetails.class))).thenReturn(accountDetails);
    when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwtToken");
    doNothing().when(emailKafkaService).sendEmail(request.getEmail());
    AuthenticationResponse response = authenticationService.register(request);

    assertNotNull(response);
    assertEquals(request.getName(), response.getName());
    assertEquals(request.getEmail(), response.getEmail());
    assertEquals("jwtToken", response.getToken());
    assertEquals(generatedAcc, response.getAccNo());
    assertEquals(0, response.getBalance());

    verify(accountDetailsRepo, times(1)).findByDetails_Email(request.getEmail());
    verify(sequenceGeneratorService, times(1)).generateSequenceNumber(RegisterRequest.SEQUENCE_NAME);
    verify(passwordEncoder, times(1)).encode(request.getPassword());
    verify(userRepo, times(1)).findByEmail(us.getEmail());
    verify(accountDetailsRepo, times(1)).save(any(AccountDetails.class));
    verify(emailKafkaService, times(1)).sendEmail(request.getEmail());
}


    @Test
    void register() {
    }

    @Test
    void verifyOTP() {
    }

    @Test
    void validateToken() {
    }

    @Test
    void acceptingToken() {
    }
}