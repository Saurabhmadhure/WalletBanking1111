package com.FullStack.WalletBanking.dao.service;

import com.FullStack.WalletBanking.EmailService.EmailKafkaService;
import com.FullStack.WalletBanking.dao.repository.AccountDetailsRepo;
import com.FullStack.WalletBanking.dao.repository.OtpClassRepository;
import com.FullStack.WalletBanking.dao.repository.UserRepo;
import com.FullStack.WalletBanking.model.AccountDetails;
import com.FullStack.WalletBanking.model.OtpClass;
import com.FullStack.WalletBanking.model.User;
import com.FullStack.WalletBanking.request_response_Helper.AuthenticationRequest;
import com.FullStack.WalletBanking.request_response_Helper.AuthenticationResponse;
import com.FullStack.WalletBanking.service.AuthenticationService;
import com.FullStack.WalletBanking.utility.SequenceGeneratorService;
import com.FullStack.WalletBanking.webConfig.Config.JwtService;
import com.FullStack.WalletBanking.webConfig.TokenRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepo repository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private SequenceGeneratorService sequenceGenerator;

    @Mock
    private User us;

    @Mock
    private OtpClassRepository otpClassRepository;

    @Mock
    private UserRepo userRepo;

    @Mock
    private AccountDetailsRepo accountDetailsRepo;

    @Mock
    private AccountDetails accountDetails;
    private String jwtSecret;

    @Mock
    private EmailKafkaService emailKafkaService;
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }





//    @Test
//    void register() throws RuntimeException{
//
//
//        User savedUser = new User();
//        savedUser.setUserId(1);
//        savedUser.setEmail("saurabh@gmail.com");
//        savedUser.setName("Saurabh");
//        savedUser.setUserRole(User_Role.USER);
//        savedUser.setPassword("Passsword123");
//
//        AccountDetails accountDetails = new AccountDetails();
//        accountDetails.setDetails(savedUser);
//        accountDetails.setBalance(0);
//        accountDetails.setAccNumber(123456);
//        UserDetails userDetails = new User(1,"saurabh@gmail.com", "Saurabh","Passsword123" );
//
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
//        when(accountDetailsRepo.findByDetails_Email(accountDetails.getDetails().getEmail())).thenReturn(Optional.of(accountDetails.getAccNumber()));
//        when(userRepo.findByEmail(accountDetails.getDetails().getEmail())).thenReturn(Optional.of(user));
//         when(jwtService.generateToken(any())).thenReturn("dummy-token");
//
//
//        AuthenticationResponse expectedResponse = new AuthenticationResponse();
//        expectedResponse.setAccNo(accountDetails.getAccNumber());
//        expectedResponse.setBalance(accountDetails.getBalance());
////        expectedResponse.setToken("jwt_token");
//        expectedResponse.setName(registerRequest.getName());
//        expectedResponse.setEmail(registerRequest.getEmail());
//
//         AuthenticationResponse response = authenticationService.register(registerRequest);
//
//        // verify that the returned AuthenticationResponse object has the expected properties
//        assertNotNull(response);
//        assertEquals("Saurabh", response.getName());
//        assertEquals("saurabh@gmail.com", response.getEmail());
//        assertEquals(0, response.getBalance());
//        assertEquals("dummy-token", response.getToken());
//
//        verify(accountDetailsRepo).findByDetails_Email(registerRequest.getEmail());
//        verify(passwordEncoder).encode(registerRequest.getPassword());
//        verify(sequenceGenerator).generateSequenceNumber(RegisterRequest.SEQUENCE_NAME);
//        verify(repository).save(any(User.class));
//        verify(jwtService).generateToken(userDetails);
//
//
//    }
//    @Test
//    public void testRegisterSuccess() throws Exception {
//        // Given
//        RegisterRequest request = new RegisterRequest();
//        request.setEmail("saurabh@gmail.com");
//        request.setName("Saurabh");
//        request.setPassword("Password123");
//
//        User us = new User();
//
//        us.setEmail("saurabh@gmail.com");
//        us.setUserRole(User_Role.USER);
//        us.setName("Saurabh");
//        us.setPassword("Password123");
//
//        when(accountDetailsRepo.findByDetails_Email(anyString())).thenReturn(Optional.empty());
//        when(passwordEncoder.encode(anyString())).thenReturn("Password123");
//        when(userRepo.save(any(User.class))).thenReturn(us);
//        when(accountDetailsRepo.save(any(AccountDetails.class))).thenReturn(new AccountDetails());
//        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("generatedtoken");
//        when(authenticationService.authenticate(any(AuthenticationRequest.class))).thenReturn(new AuthenticationResponse());
//        AuthenticationResponse response = authenticationService.register(request);
//
//
//        assertNotNull(response);
//        assertEquals("generatedtoken", response.getToken());
//        assertEquals("saurabh@gmail.com", response.getName());
//
//
//    }
//    @Test
//    public void testAuthenticateSuccess() throws Exception {
//        // Arrange
//        AuthenticationRequest request = new AuthenticationRequest("saurabhmadhure@gmail.com", "Password123");
//        User user = new User();
//        user.setEmail("saurabhmadhure@gmail.com");
//        user.setPassword("$2a$10$2LJMQXvjd8oE5K5u5ZQrqe3Hv1LLJ19.TyRXjK32tYUK9XvP8EkOW");
//        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
//        AccountDetails accountDetails = new AccountDetails();
//        accountDetails.setDetails(user);
//        accountDetails.setBalance(0);
//        accountDetails.setAccNumber( 12345);
//        when(accountDetailsRepo.findByDetails_Email(request.getEmail())).thenReturn(Optional.of(accountDetails));
//        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
//
//        when(jwtService.generateToken(any(UserDetails.class))).thenReturn(token);
//        Mockito.verify(accountDetailsRepo).findByDetails_Email(request.getEmail());
//
//
//        // Act
//        AuthenticationResponse response = authenticationService.authenticate(request);
//
//        // Assert
//        assertEquals("Saurabh Madhure", response.getName());
//        assertEquals(0, response.getBalance());
//        assertEquals("saurabhmadhure@gmail.com", response.getEmail());
//        assertNotNull(response.getAccNo());
//        assertEquals(token, response.getToken());
//    }

    @Test
    public void testAuthenticate() throws Exception {
        User savedUser = new User();
        savedUser.setUserId(1);
        savedUser.setEmail("saurabh@gmail.com");
        savedUser.setName("Saurabh");
//        savedUser.setUserRole(User_Role.USER);
        savedUser.setPassword("Passsword123");


        // Mock dependencies
        AuthenticationRequest request = new AuthenticationRequest("saurabh@gmail.com", "Passsword123");




        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setDetails(savedUser);
        accountDetails.setBalance(0);
        accountDetails.setAccNumber(123456);
        String jwtToken = "jwtToken";
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepo.findByEmail("saurabh@gmail.com")).thenReturn(Optional.of(savedUser));
        when(accountDetailsRepo.findByDetails_Email("saurabh@gmail.com")).thenReturn(Optional.of(accountDetails));
        when(jwtService.generateToken(savedUser)).thenReturn(jwtToken);

        // Call the method
        AuthenticationResponse response = authenticationService.authenticate(request);

        // Verify the result
        assertNotNull(response);
        assertEquals("Saurabh", response.getName());
        assertEquals(123456, response.getAccNo());
        assertEquals("saurabh@gmail.com", response.getEmail());
        assertEquals(0, response.getBalance());
        assertEquals(jwtToken, response.getToken());
    }



    @Test(expected = Exception.class)
    public void testAuthenticateWithInvalidCredentials() throws Exception {
        String email = "test@example.com";
        String password = "wrong-password";

        // Mock the AuthenticationManager
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException(""));

        // Call the authenticate method
        AuthenticationRequest request = new AuthenticationRequest(email, password);
        authenticationService.authenticate(request);
    }

    @Test
    public void testAuthenticateInvalidCredentials() {

        AuthenticationRequest request = new AuthenticationRequest("saurabhmadhure@gmail.com", "Password");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(BadCredentialsException.class);


        assertThrows(Exception.class, () -> {
            authenticationService.authenticate(request);
        });
    }


    @Test
    public void testVerifyOTPWhenOtpClassNotFound() {

        OtpClass otpClass = new OtpClass();
        otpClass.setEmail("saurabhmadhure@gmail.com");
//        Mockito.when(otpClassRepository.findByEmail(otpClass.getEmail())).thenReturn(null);


        when(otpClassRepository.findByEmail("saurabhmadhure@gmail.com")).thenReturn(null);


        boolean result = authenticationService.verifyOTP(otpClass);

        // assert the result
        assertFalse(result);
    }

    @Test
    public void testVerifyOTPWhenGeneratedOtpIsNull() {

        OtpClass otpClass = new OtpClass();
        otpClass.setEmail("saurabhmadhure@gmail.com");

        when(otpClassRepository.findByEmail("saurabhmadhure@gmail.com")).thenReturn(null);

        boolean result = authenticationService.verifyOTP(otpClass);

        assertFalse(result);
    }

    @Test
    public void testVerifyOTPWhenOtpNotMatched() {

        OtpClass otpClass = new OtpClass();
        otpClass.setEmail("saurabhmadhure@gmail.com");
        otpClass.setUserEnteredOTP(1234);
        OtpClass savedOtpClass = new OtpClass();
        savedOtpClass.setGeneratedOTP(5678);
        Mockito.when(otpClassRepository.findByEmail(otpClass.getEmail())).thenReturn(savedOtpClass);

        boolean result = authenticationService.verifyOTP(otpClass);

        assertFalse(result);
    }

    @Test
    public void testVerifyOTPWhenOtpMatched() {

        OtpClass otpClass = new OtpClass();
        otpClass.setEmail("saurabhmadhure@gmail.com");
        otpClass.setUserEnteredOTP(5678);
        OtpClass savedOtpClass = new OtpClass();
        savedOtpClass.setGeneratedOTP(5678);
        Mockito.when(otpClassRepository.findByEmail(otpClass.getEmail())).thenReturn(savedOtpClass);
        Mockito.when(otpClassRepository.save(savedOtpClass)).thenReturn(savedOtpClass);

        boolean result = authenticationService.verifyOTP(otpClass);


        assertTrue(result);
    }

}