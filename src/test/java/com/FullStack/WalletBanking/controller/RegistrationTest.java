package com.FullStack.WalletBanking.controller;

import com.FullStack.WalletBanking.dao.repoImplementation.AuthenticationService;
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
import com.FullStack.WalletBanking.api.AuthenticationRequest;
import com.FullStack.WalletBanking.api.AuthenticationResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class RegistrationTest {
    @Autowired
    private AuthenticationService authenticationService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserRepo repository;

    @MockBean
    private TokenRepository tokenRepository;

    @MockBean
    private SequenceGeneratorService sequenceGenerator;

    @MockBean
    private User us;

    @MockBean
    private OtpClassRepository otpClassRepository;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private AccountDetailsRepo accountDetailsRepo;

    @MockBean
    private AccountDetails accountDetails;
    private String jwtSecret;

    @MockBean
    private EmailKafkaService emailKafkaService;

    @Before
    public void setUp() {

    }


    @Test
    void testRegister() throws RuntimeException {
        // given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("saurabh@gmail.com");
        registerRequest.setPassword("Passsword123");
        registerRequest.setName("Saurabh");
        Optional<AccountDetails> optionalAccountDetails = Optional.empty();
        when(accountDetailsRepo.findByDetails_Email(registerRequest.getEmail())).thenReturn(optionalAccountDetails);

        when(sequenceGenerator.generateSequenceNumber(RegisterRequest.SEQUENCE_NAME)).thenReturn(1);

        when(jwtService.generateToken((User) registerRequest.toUser())).thenReturn("jwt_token");


        User savedUser = new User();
        savedUser.setUserId(1);
        savedUser.setEmail(registerRequest.getEmail());
        savedUser.setName(registerRequest.getName());
        savedUser.setRole(Role.USER);
        savedUser.setPassword(registerRequest.getPassword());

        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setDetails(savedUser);
        accountDetails.setBalance(0);
        accountDetails.setAccNumber(123456);

        AuthenticationResponse expectedResponse = new AuthenticationResponse();
        expectedResponse.setAccNo(accountDetails.getAccNumber());
        expectedResponse.setBalance(accountDetails.getBalance());
        expectedResponse.setToken("jwt_token");
        expectedResponse.setName(registerRequest.getName());
        expectedResponse.setEmail(registerRequest.getEmail());

        AuthenticationResponse actualAuthResponse = authenticationService.register(registerRequest);

        verify(userRepo, times(1)).save((User) registerRequest.toUser());
        verify(accountDetailsRepo, times(1)).save(registerRequest.toAccountDetails());

        // verify that the returned AuthenticationResponse object has the expected properties
        assertEquals(expectedResponse.getToken(), actualAuthResponse.getToken());
        assertEquals(expectedResponse.getName(), actualAuthResponse.getName());
        assertEquals(expectedResponse.getEmail(), actualAuthResponse.getEmail());


    }

    @Test
    public void registerShouldReturnNullIfEmailAlreadyExists() {
        // given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@test.com");
        registerRequest.setPassword("password");
        registerRequest.setName("Test User");

        User existingUser = new User();
        existingUser.setEmail(registerRequest.getEmail());

        when(userRepo.findByEmail(registerRequest.getEmail())).thenReturn(Optional.of(existingUser));

        // when
        AuthenticationResponse actualResponse = authenticationService.register(registerRequest);


    }

    @Test
    public void testRegisterSuccess() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("saurabhmadhure@gmail.com");
        request.setName("Saurabh");
        request.setPassword("Password123");

        User us = new User();

        us.setEmail("saurabhmadhure@gmail.com");
        us.setRole(Role.USER);
        us.setName("Saurabh");
        us.setPassword("Password123");

        when(accountDetailsRepo.findByDetails_Email(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("Password123");
        when(userRepo.save(any(User.class))).thenReturn(us);
        when(accountDetailsRepo.save(any(AccountDetails.class))).thenReturn(new AccountDetails());
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("generatedtoken");
        when(authenticationService.authenticate(any(AuthenticationRequest.class))).thenReturn(new AuthenticationResponse());
        AuthenticationResponse response = authenticationService.register(request);


        assertNotNull(response);
        assertEquals("generatedtoken", response.getToken());
        assertEquals("saurabhmadhure@gmail.com", response.getName());


    }







}

