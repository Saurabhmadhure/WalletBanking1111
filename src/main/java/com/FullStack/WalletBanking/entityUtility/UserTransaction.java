package com.FullStack.WalletBanking.entityUtility;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.transaction.TransactionStatus;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTransaction {
    private Long userTransactionId;
    private Double amount;
    @JsonFormat(pattern="yyyy-MMM-dd hh:mm:ss a")
    private LocalDateTime transactionDateTime;
    private String remarks;
    private String info;
    private Integer pointsEarned;
    private Character isRedeemed;
    private String message;

    private TransactionStatus transactionStatus;
}
