package com.walleto.controller;

import com.walleto.exception.AccountCreationException;
import com.walleto.exception.SendMessageException;
import com.walleto.model.controller.AuthenticationResponse;
import com.walleto.model.entity.AccountDetails;
import com.walleto.model.entity.RegisterRequest;
import com.walleto.model.entity.User;
import com.walleto.repository.AccountDetailsRepo;
import com.walleto.repository.SequenceGeneratorService;
import com.walleto.repository.TokenRepo;
import com.walleto.repository.UserRepo;
import com.walleto.service.AuthenticationService;
import com.walleto.service.JwtService;
import com.walleto.service.kafka.KafkaProducer;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


//@ExtendWith(MockitoExtension.class)
//public class RegistrationTest {
//    @Autowired
//    private AuthenticationService AuthenticationService;
//
//    @MockBean
//    private AuthenticationManager authenticationManager;
//
//    @MockBean
//    private PasswordEncoder passwordEncoder;
//
//    @MockBean
//    private JwtService jwtService;
//
//    @MockBean
//    private UserRepo repository;
//
//    @MockBean
//    private TokenRepo tokenRepo;
//
//    @MockBean
//    private SequenceGeneratorService sequenceGenerator;
//
//    @MockBean
//    private User us;
//
//    @MockBean
//    private OtpClassRepo otpClassRepo;
//
//    @MockBean
//    private UserRepo userRepo;
//
//    @MockBean
//    private AccountDetailsRepo accountDetailsRepo;
//
//    @MockBean
//    private AccountDetails accountDetails;
//    private String jwtSecret;
//
//    @MockBean
//    private KafkaProducer kafkaProducerService;
//
//    @Before
//    public void setUp() {
//
//    }
//
//
//    @Test
//    void testRegister() throws RuntimeException {
//        // given
//        RegisterRequest registerRequest = new RegisterRequest();
//        registerRequest.setEmail("saurabh@gmail.com");
//        registerRequest.setPassword("Passsword123");
//        registerRequest.setName("Saurabh");
//        Optional<AccountDetails> optionalAccountDetails = Optional.empty();
//        when(accountDetailsRepo.findByDetails_Email(registerRequest.getEmail())).thenReturn(optionalAccountDetails);
//
//        when(sequenceGenerator.generateSequenceNumber(RegisterRequest.SEQUENCE_NAME)).thenReturn(1);
//
//        when(jwtService.generateToken((User) registerRequest.toUser())).thenReturn("jwt_token");
//
//
//        User savedUser = new User();
//        savedUser.setUserId(1);
//        savedUser.setEmail(registerRequest.getEmail());
//        savedUser.setName(registerRequest.getName());
////        savedUser.setUserRole(User_Role.USER);
//        savedUser.setPassword(registerRequest.getPassword());
//
//        AccountDetails accountDetails = new AccountDetails();
//        accountDetails.setDetails(savedUser);
//        accountDetails.setBalance(0);
//        accountDetails.setAccNumber(123456);
//
//        AuthenticationResponse expectedResponse = new AuthenticationResponse();
//        expectedResponse.setAccNo(accountDetails.getAccNumber());
//        expectedResponse.setBalance(accountDetails.getBalance());
//        expectedResponse.setToken("jwt_token");
//        expectedResponse.setName(registerRequest.getName());
//        expectedResponse.setEmail(registerRequest.getEmail());
//
//        AuthenticationResponse actualAuthResponse = AuthenticationService.register(registerRequest);
//
//        verify(userRepo, times(1)).save((User) registerRequest.toUser());
//        verify(accountDetailsRepo, times(1)).save(registerRequest.toAccountDetails());
//
//        // verify that the returned AuthenticationResponse object has the expected properties
//        assertEquals(expectedResponse.getToken(), actualAuthResponse.getToken());
//        assertEquals(expectedResponse.getName(), actualAuthResponse.getName());
//        assertEquals(expectedResponse.getEmail(), actualAuthResponse.getEmail());
//
//
//    }
//
//    @Test
//    public void registerShouldReturnNullIfEmailAlreadyExists() {
//        // given
//        RegisterRequest registerRequest = new RegisterRequest();
//        registerRequest.setEmail("test@test.com");
//        registerRequest.setPassword("password");
//        registerRequest.setName("Test User");
//
//        User existingUser = new User();
//        existingUser.setEmail(registerRequest.getEmail());
//
//        when(userRepo.findByEmail(registerRequest.getEmail())).thenReturn(Optional.of(existingUser));
//
//        // when
//        AuthenticationResponse actualResponse = AuthenticationService.register(registerRequest);
//
//
//    }
//
//    @Test
//    public void testRegisterSuccess() throws Exception {
//        // Given
//        RegisterRequest request = new RegisterRequest();
//        request.setEmail("saurabhmadhure@gmail.com");
//        request.setName("Saurabh");
//        request.setPassword("Password123");
//
//        User us = new User();
//
//        us.setEmail("saurabhmadhure@gmail.com");
////        us.setUserRole(User_Role.USER);
//        us.setName("Saurabh");
//        us.setPassword("Password123");
//
//        when(accountDetailsRepo.findByDetails_Email(anyString())).thenReturn(Optional.empty());
//        when(passwordEncoder.encode(anyString())).thenReturn("Password123");
//        when(userRepo.save(any(User.class))).thenReturn(us);
//        when(accountDetailsRepo.save(any(AccountDetails.class))).thenReturn(new AccountDetails());
//        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("generatedtoken");
//        when(AuthenticationService.authenticate(any(AuthenticationRequest.class))).thenReturn(new AuthenticationResponse());
//        AuthenticationResponse response = AuthenticationService.register(request);
//
//
//        assertNotNull(response);
//        assertEquals("generatedtoken", response.getToken());
//        assertEquals("saurabhmadhure@gmail.com", response.getName());
//
//
//    }
//
//
//
//
//
//
//
//}
//

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("deprecation")
public class RegistrationTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepo userRepo;

    @Mock
    private TokenRepo tokenRepo;

    @Mock
    private AccountDetailsRepo accountDetailsRepo;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private SequenceGeneratorService sequenceGeneratorService;

    @Mock
    private KafkaProducer kafkaProducerService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testRegister_withNewEmail() throws SendMessageException {
        // arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("saurabh@gmail.com");
        request.setName("Saurabh");
        request.setPassword("password");
        User user = new User();
        user.setUserId(1);
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setDetails(user);
        accountDetails.setBalance(0);
        accountDetails.setAccNumber(123456);
        String jwtToken = "jwtToken";
        AuthenticationResponse expected = new AuthenticationResponse();
        expected.setAccNo(accountDetails.getAccNumber());
        expected.setBalance(accountDetails.getBalance());
        expected.setName(user.getName());
        expected.setEmail(user.getEmail());
        expected.setToken(jwtToken);
        when(accountDetailsRepo.findByDetails_Email(request.getEmail())).thenReturn(Optional.empty());
        when(sequenceGeneratorService.generateSequenceNumber(RegisterRequest.SEQUENCE_NAME)).thenReturn(user.getUserId());
        when(userRepo.save(any(User.class))).thenReturn(user);
        when(accountDetailsRepo.save(any(AccountDetails.class))).thenReturn(accountDetails);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn(jwtToken);
        doNothing().when(kafkaProducerService).sendMessage(request.getEmail());


        AuthenticationResponse actual = authenticationService.register(request);


        assertEquals(expected, actual);
    }

    @Test
    void testRegister_withExistingEmail() {
        // arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("saurabh@gmail.com");
        when(accountDetailsRepo.findByDetails_Email(request.getEmail())).thenReturn(Optional.of(new AccountDetails()));


        AuthenticationResponse actual = authenticationService.register(request);


        assertNull(actual);
    }

    @Test
    void testRegister_withException() {
        // arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("saurabh@gmail.com");
        when(accountDetailsRepo.findByDetails_Email(request.getEmail())).thenReturn(Optional.empty());
        when(userRepo.save(any(User.class))).thenThrow(RuntimeException.class);
        assertThrows(RuntimeException.class, () -> authenticationService.register(request));

        // assert
        verify(accountDetailsRepo, times(1)).findByDetails_Email(request.getEmail());
        verify(userRepo, times(1)).save(any(User.class));
        verifyNoMoreInteractions(accountDetailsRepo, userRepo, kafkaProducerService);
    }
    @Test
    public void testRegister() throws AccountCreationException, SendMessageException {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setName("John Doe");
        registerRequest.setPassword("password");

        User user = new User();
        user.setUserId(1);
        user.setEmail(registerRequest.getEmail());
        user.setName(registerRequest.getName());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setDetails(user);
        accountDetails.setBalance(0);
        accountDetails.setAccNumber(123456);

        String jwtToken = "jwtToken";

        doReturn(Optional.empty()).when(accountDetailsRepo).findByDetails_Email(registerRequest.getEmail());
        doReturn(user).when(userRepo).save(any(User.class));
        doReturn(accountDetails).when(accountDetailsRepo).save(any(AccountDetails.class));
        doReturn(jwtToken).when(jwtService).generateToken(any(UserDetails.class));

         AuthenticationResponse response = authenticationService.register(registerRequest);

         assertNotNull(response);
        assertEquals(user.getName(), response.getName());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(accountDetails.getAccNumber(), response.getAccNo());
        assertEquals(accountDetails.getBalance(), response.getBalance());
        assertEquals(jwtToken, response.getToken());
    }
}

