package com.learning.bankingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class InternetBankingRegisterDto {

    private String accountNumber;

    private String loginPassword;

    private String confirmLoginPassword;

    private String transactionPassword;

    private String confirmTransactionPassword;

    private String otp;
}
