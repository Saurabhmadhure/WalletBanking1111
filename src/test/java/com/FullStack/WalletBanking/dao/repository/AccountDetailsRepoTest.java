package com.FullStack.WalletBanking.dao.repository;

import com.FullStack.WalletBanking.model.AccountDetails;
import com.FullStack.WalletBanking.model.domain.User;

import com.FullStack.WalletBanking.model.Transaction;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Optional;

@SpringBootTest
class AccountDetailsRepoTest {
    @Autowired
    private AccountDetailsRepo accountDetailsRepo;
    @Autowired
    private  EmailService emailService;

    @Test
    void findByDetails_Email() {
         AccountDetails account = new AccountDetails(1234, 500, new User(1,"saurabh@gmail.com","Saurabh Madhure","123456789"), new ArrayList<Transaction>());
        accountDetailsRepo.save(account);


        Optional<AccountDetails> byId = accountDetailsRepo.findByDetails_Email("saurabh@gmail.com");
        assert(byId.get()).equals(account);
    }
    @Test
    void sendEmail() throws MessagingException {
//        EmailDetails emailDetails=new EmailDetails("saurabhmadhure364@gmail.com","OTP is 1122","WALLET VERIFICATION");
        emailService.sendEmail("saurabhmadhure364@gmail.com",1122);
    }

}