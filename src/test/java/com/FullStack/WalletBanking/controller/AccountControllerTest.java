package com.FullStack.WalletBanking.controller;

import com.FullStack.WalletBanking.EmailService.EmailServiceImpl;
import com.FullStack.WalletBanking.dao.repository.AccountDetailsRepo;
import com.FullStack.WalletBanking.dao.repository.TransactionRepository;
import com.FullStack.WalletBanking.model.AccountDetails;
import com.FullStack.WalletBanking.model.User;
import com.FullStack.WalletBanking.request_response_Helper.BalanceResponse;
import com.FullStack.WalletBanking.service.WalletOperations;
import com.FullStack.WalletBanking.webConfig.Config.LogoutService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.data.mongodb.core.aggregation.ConditionalOperators.Switch.CaseOperator.when;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

 @RunWith(MockitoJUnitRunner.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletOperations walletOperations;

    @MockBean
    private LogoutService logoutService;
    @Mock
    private BalanceResponse balanceResponse;
    @MockBean
    private TransactionRepository transactionRepository;
    @Mock
    private AccountDetailsRepo accountDetailsRepo;

    @MockBean
    private EmailServiceImpl emailService;

    @Test
    public void testShowUserInfo() throws Exception {
        // Mock account details data
        int accNumber = 123456;
        User user = User.builder().userId(1).email("saurabh@gmail.com").name("Saurabh").password("Password123").build();
        AccountDetails accountDetails = AccountDetails.builder().accNumber(accNumber).balance(1000).details(user).build();
        Mockito.when(accountDetailsRepo.findById(accNumber)).thenReturn(Optional.ofNullable(accountDetails));

        mockMvc.perform(get("/user/showUserInfo/{accNumber}", accNumber))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) content().contentType(MediaType.APPLICATION_JSON))
                .andExpect((ResultMatcher) jsonPath("$.accountNumber").value(accNumber))
                .andExpect((ResultMatcher) jsonPath("$.balance").value(1000))
                .andExpect((ResultMatcher) jsonPath("$.details.userId").value(1))
                .andExpect((ResultMatcher) jsonPath("$.details.email").value("saurabh@gmail.com"))
                .andExpect((ResultMatcher) jsonPath("$.details.name").value("Saurabh"))
                .andExpect((ResultMatcher) jsonPath("$.details.password").value("Password123"))
                .andExpect((ResultMatcher) jsonPath("$.details.userRole").value("USER"));

    }
    @Test
    void testShowBal() throws Exception {
        when((AggregationExpression) walletOperations.bal(anyInt())).then(balanceResponse);
        mockMvc.perform(get("/accounts/1")).andExpect(status().isOk());
    }
    @Test
    public void logOutTest() throws Exception {
        mockMvc.perform((RequestBuilder) post("/accounts/logout"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) content().string("Logout SuccessFully"));
    }

    @Test
    public void delAccountTest() throws Exception {
        int id = 12345;
        given(walletOperations.deleteAccount(id)).willReturn("Account deleted successfully");

        mockMvc.perform(delete("/accounts/del/{id}", id))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) content().string("Account deleted successfully"));
    }

//    @Test
//    void testAmountDeposit() throws Exception {
//        ObjectMapper mapper = new ObjectMapper();
//        String json = mapper.writeValueAsString(balanceResponse);
//        when(walletOperations.deposit(balan)).thenReturn(new DepositResponse());
//        mockMvc.perform(MockMvcRequestBuilders.post("/accounts/deposit")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .characterEncoding("UTF-8"))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//    }
}
