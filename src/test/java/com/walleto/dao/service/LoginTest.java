package com.walleto.dao.service;

import com.walleto.exception.SendMessageException;
import com.walleto.model.controller.AuthenticationResponse;
import com.walleto.model.entity.AccountDetails;
import com.walleto.model.entity.RegisterRequest;
import com.walleto.model.entity.User;
import com.walleto.repository.AccountDetailsRepo;
import com.walleto.repository.SequenceGeneratorService;
import com.walleto.repository.UserRepo;
import com.walleto.service.AuthenticationService;
import com.walleto.service.JwtService;
import com.walleto.service.kafka.KafkaProducer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepo repository;

//    @Mock
//    private TokenRepo tokenRepository;

    @Mock
    private SequenceGeneratorService sequenceGeneratorService;

//    @Mock
//    private OtpClassRepo otpClassRepository;


//    @Mock
//    private UserRepo userRepo;

    @Mock
    private AccountDetailsRepo accountDetailsRepo;

    @Mock
    private KafkaProducer kafkaProducerService;

//    @Mock
//    private AuthenticationManager authenticationManager;

    @InjectMocks
    public AuthenticationService authenticationService;

    @Test
    void registerShouldReturnAuthenticationResponseWhenEmailIsNotPresentInAccountDetails() throws SendMessageException {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("saurabh@gmail.com");
        registerRequest.setPassword("Passsword123");
        registerRequest.setName("Saurabh");

        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setAccNumber(1);
        accountDetails.setBalance(0);
        accountDetails.setDetails(new User());

        when(accountDetailsRepo.findByDetails_Email(registerRequest.getEmail())).thenReturn(Optional.empty());
        when(sequenceGeneratorService.generateSequenceNumber(RegisterRequest.SEQUENCE_NAME)).thenReturn(1);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(repository.save(any())).thenReturn(new User());

        when(accountDetailsRepo.save(any())).thenReturn(accountDetails);
        when(jwtService.generateToken(any())).thenReturn("jwtToken");

        // Act
        AuthenticationResponse authenticationResponse;
        authenticationResponse = authenticationService.register(registerRequest);

        // Assert
        verify(accountDetailsRepo, times(1)).findByDetails_Email(registerRequest.getEmail());
        verify(sequenceGeneratorService, times(1)).generateSequenceNumber(RegisterRequest.SEQUENCE_NAME);
        verify(passwordEncoder, times(1)).encode(registerRequest.getPassword());
        verify(repository, times(1)).save(any());

        verify(accountDetailsRepo, times(1)).save(any());
        verify(jwtService, times(1)).generateToken(any());
        verify(kafkaProducerService, times(1)).sendMessage(registerRequest.getEmail());

        Assertions.assertEquals("saurabh@gmail.com", authenticationResponse.getEmail());
        Assertions.assertEquals("Saurabh", authenticationResponse.getName());
        Assertions.assertEquals("jwtToken", authenticationResponse.getToken());
        Assertions.assertEquals(1, authenticationResponse.getAccNo());
        Assertions.assertEquals(0, authenticationResponse.getBalance());
    }

/*
    @Test
    void registerShouldReturnNullWhenEmailIsPresentInAccountDetails() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password");
        registerRequest.setName("John");

        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setDetails(new User());

        when(accountDetailsRepo.findByDetails_Email(registerRequest.getEmail())).thenReturn(Optional.of(accountDetails));

        // Act
        AuthenticationResponse authenticationResponse = AuthenticationService.register(registerRequest);

        // Assert
        verify(accountDetailsRepo, times(1)).findByDetails_Email(registerRequest.getEmail());
        verifyNoMoreInteractions(sequenceGeneratorService, passwordEncoder, repository, GenAccountNumber,
*/

}