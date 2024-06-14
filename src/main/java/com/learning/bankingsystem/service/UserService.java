package com.learning.bankingsystem.service;

import com.learning.bankingsystem.dto.*;
import com.learning.bankingsystem.entity.OtpType;

import java.util.UUID;

public interface UserService {
    JwtAuthResponseDto login(LoginDto loginDto);

    void openAccount(OpenAccountDto openAccountDto);

    SuccessResponseDto sendOtp(String accountNumber, OtpType otpType);
    
    SuccessResponseDto resetPassword(UUID userId, ForgotPasswordRequestDto forgotPasswordRequestDto);

    SuccessResponseDto registerInternetBanking(InternetBankingRegisterDto internetBankingDto);

    SuccessResponseDto forgotUserId(String accountNumber, Long otp);

    SuccessResponseDto sendOtpWithUsername(String username, OtpType otpType);

    SuccessResponseDto forgotPassword(String username, Long otp);
}
