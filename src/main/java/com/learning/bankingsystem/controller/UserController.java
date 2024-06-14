package com.learning.bankingsystem.controller;

import com.learning.bankingsystem.dto.*;
import com.learning.bankingsystem.entity.OtpType;
import com.learning.bankingsystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/home")
    public String showHome(Model model) {
        return "Home";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginDto", new LoginDto());
        return "login";
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponseDto> login(@RequestBody LoginDto loginDto) {
        return new ResponseEntity<>(userService.login(loginDto), HttpStatus.OK);
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("openAccountDto", new OpenAccountDto());
        return "register";
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody OpenAccountDto openAccountDto) {
        userService.openAccount(openAccountDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        return "Dashboard";
    }

    @PostMapping("/send-otp")
    public ResponseEntity<SuccessResponseDto> sendOtp(@RequestParam String accountNumber,
                                                      @RequestParam OtpType otpType) {
        return new ResponseEntity<>(userService.sendOtp(accountNumber, otpType), HttpStatus.OK);
    }

    @PostMapping("/send-otp-with-username")
    public ResponseEntity<SuccessResponseDto> sendOtpWithUserId(@RequestParam String username,
                                                                @RequestParam OtpType otpType) {
        return new ResponseEntity<>(userService.sendOtpWithUsername(username, otpType), HttpStatus.OK);
    }

    @GetMapping("/forgot-userId")
    public String forgotUserId(Model model) {
        model.addAttribute("accountNumber", "");
        model.addAttribute("otpType", OtpType.values());
        return "ForgotUserId";
    }

    @PostMapping("/forgot-userId")
    public ResponseEntity<SuccessResponseDto> forgotUserId(Model model,
                                                           @RequestParam String accountNumber,
                                                           @RequestParam Long otp) {
        var response = userService.forgotUserId(accountNumber, otp);
        model.addAttribute("userId", response.getUserId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        return "ForgotPassword";
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<SuccessResponseDto> forgotPassword(@RequestParam String username,
                                                             @RequestParam Long otp) {
        return new ResponseEntity<>(userService.forgotPassword(username, otp), HttpStatus.OK);
    }

    @GetMapping("/reset-password")
    public String resetPassword(Model model) {
        return "ResetPassword";
    }

    @PostMapping("/reset-password/{userId}")
    public ResponseEntity<SuccessResponseDto> resetPassword(@PathVariable UUID userId,
                                                             @RequestBody ForgotPasswordRequestDto forgotPasswordRequestDto) {
        return new ResponseEntity<>(userService.resetPassword(userId, forgotPasswordRequestDto), HttpStatus.OK);
    }

    @GetMapping("/register-internet-Banking")
    public String registerInternetBanking(Model model) {
        model.addAttribute("internetBankingRegisterDto", new InternetBankingRegisterDto());
        return "RegisterInternetBanking";
    }

    @PostMapping("/register-internet-Banking")
    public ResponseEntity<SuccessResponseDto> registerInternetBanking(@RequestBody InternetBankingRegisterDto internetBankingDto) {
        return new ResponseEntity<>(userService.registerInternetBanking(internetBankingDto), HttpStatus.OK);
    }
}
