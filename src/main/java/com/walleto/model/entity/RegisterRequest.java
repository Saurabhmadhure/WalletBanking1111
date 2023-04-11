package com.walleto.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @Transient
    public static  String SEQUENCE_NAME="user_sequence";
    @Id
    int _id;
    private String name;
    @Indexed(unique = true, direction = IndexDirection.DESCENDING)
    private String email;
    private int accNumber;
    private int balance;
    private String isRedeemed;

    private String password;
    private AccountDetails accountDetails;
    public User toUser() {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
//        user.setUserRole(User_Role.USER);
        return user;
    }
    public AccountDetails toAccountDetails() {
        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setAccNumber(accNumber);
        accountDetails.setBalance(balance);
        accountDetails.setDetails(toUser());
        accountDetails.setIsRedeemed(isRedeemed);
        return accountDetails;
    }
}
