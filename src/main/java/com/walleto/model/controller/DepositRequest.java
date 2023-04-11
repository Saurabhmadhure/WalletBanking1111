package com.walleto.model.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class DepositRequest {
@Id
    private int accountNo;
    private int amount;

}

