package com.FullStack.WalletBanking.entityUtility;

import com.FullStack.WalletBanking.model.AccountDetails;

import lombok.Data;

import java.util.List;
@Data
public class Response {
    private List<AccountDetails> list;
}
