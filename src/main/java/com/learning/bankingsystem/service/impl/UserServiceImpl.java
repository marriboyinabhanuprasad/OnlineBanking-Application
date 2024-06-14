package com.learning.bankingsystem.service.impl;

import com.learning.bankingsystem.dto.*;
import com.learning.bankingsystem.entity.*;
import com.learning.bankingsystem.exception.FoundException;
import com.learning.bankingsystem.exception.InvalidInputException;
import com.learning.bankingsystem.exception.NotFoundException;
import com.learning.bankingsystem.mapper.AddressMapper;
import com.learning.bankingsystem.mapper.OccupationMapper;
import com.learning.bankingsystem.mapper.UserMapper;
import com.learning.bankingsystem.repository.*;
import com.learning.bankingsystem.security.JwtTokenProvider;
import com.learning.bankingsystem.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordRepository passwordRepository;

    private final UserRoleRepository userRoleRepository;

    private final OccupationRepository occupationRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserMapper userMapper;

    private final AddressMapper addressMapper;

    private final OccupationMapper occupationMapper;

    private final AddressRepository addressRepository;

    private static final long INITIAL_ACCOUNT_NUMBER = 100000000000L;

    private final Random random = new SecureRandom();

    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    private final ProfileRepository profileRepository;
    private final OtpRepository otpRepository;

    @Value("${default.send.email}")
    String defaultEmail;


    @Override
    public JwtAuthResponseDto login(LoginDto loginDto) {
        var user = userRepository.findByEmail(loginDto.getEmail());
        if (user == null) {
            throw new UsernameNotFoundException("User not found for email: " + loginDto.getEmail());
        }

        var password = passwordRepository.findByUser_UuidAndTypeAndStatus(user.getUuid(), PasswordType.LOGIN_PASSWORD, PasswordStatus.ACTIVE);
        if (password == null) {
            throw new InvalidInputException("RESET_PASSWORD", "Password not found for user: " + loginDto.getEmail() + ". Please reset you password ");
        }
        var profile = profileRepository.findByUser_Uuid(user.getUuid());

        if (profile.getProfileStatus().equals(ProfileStatus.INACTIVE)) {
            throw new InvalidInputException("ACCOUNT_INACTIVE", "Your account is Inactive");
        }

        if (profile.getAdminActionType().equals(AdminActionType.WAITING_FOR_APPROVAL)) {
            throw new InvalidInputException("ADMIN_APPROVAL_PENDING", "Your account not approved by Admin");
        } else if (profile.getAdminActionType().equals(AdminActionType.DECLINED)) {
            throw new InvalidInputException("ACCOUNT_DECLINED", "Your account was declined by Admin");
        }
        var userRoles = userRoleRepository.findByUser_Uuid(user.getUuid());

        if (!passwordEncoder.matches(loginDto.getPassword(), password.getPassword())) {
            if (password.getInvalidPasswordEntryCount() >= 2) {
                password.setStatus(PasswordStatus.EXPIRED);
                passwordRepository.save(password);
                throw new InvalidInputException("MAXIMUM_TRIES_REACHED", "You have entered incorrect password for 3 times, Reset your password now to login");
            }
            password.setInvalidPasswordEntryCount(password.getInvalidPasswordEntryCount() + 1);
            passwordRepository.save(password);
            throw new InvalidInputException("INCORRECT_PASSWORD", "Entered password is incorrect");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        var roles = userRoles.stream()
                .map(userRole -> userRole.getRole().toString())
                .toList();

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);

        return JwtAuthResponseDto.builder()
                .accessToken(token)
                .userId(user.getUuid())
                .tokenType("Bearer")
                .roles(roles)
                .build();
    }

    public void openAccount(OpenAccountDto openAccountDto) {
        if (userRepository.findByEmail(openAccountDto.getEmail()) != null) {
            throw new FoundException("USER_ALREADY_EXISTS", "User already exists with email: " + openAccountDto.getEmail());
        }

        var user = userMapper.openAccountDtoToUser(openAccountDto);
        user.setAccountNumber(generateAccountNumber());

        var savedUser = userRepository.save(user);

        profileRepository.save(Profile.builder()
                .user(savedUser)
                .profileStatus(ProfileStatus.ACTIVE)
                .adminActionType(AdminActionType.WAITING_FOR_APPROVAL)
                .build());

        var residentialAddress = addressMapper.addressDtoTOAddress(openAccountDto.getResidentialAddress());
        residentialAddress.setAddressType(AddressType.RESIDENTIAL);
        residentialAddress.setUser(savedUser);
        addressRepository.save(residentialAddress);

        var permanentAddress = addressMapper.addressDtoTOAddress(openAccountDto.getPermanentAddress());
        permanentAddress.setAddressType(AddressType.PERMANENT);
        permanentAddress.setUser(savedUser);
        addressRepository.save(permanentAddress);

        userRoleRepository.save(UserRole.builder()
                .user(savedUser)
                .role(RoleType.USER)
                .build());

        var occupation = occupationMapper.occupationDtoToOccupation(openAccountDto.getOccupation());
        occupation.setUser(savedUser);
        occupationRepository.save(occupation);

        sendOpenAccountEmail(savedUser.getUuid());
    }

    @Override
    public SuccessResponseDto sendOtp(String accountNumber, OtpType otpType) {
        var user = userRepository.getByAccountNumber(accountNumber);
        if (user == null) {
            throw new InvalidInputException("INVALID_ACCOUNT_NUMBER", "Account number not exists: " + accountNumber);
        }
        return sendOtpToUser(user, otpType);
    }


    @Override
    public SuccessResponseDto resetPassword(UUID userId, ForgotPasswordRequestDto forgotPasswordRequestDto) {
        var user = getById(userId);

        if (!forgotPasswordRequestDto.getPassword().equals(forgotPasswordRequestDto.getConfirmPassword())) {
            throw new InvalidInputException("PASSWORD_MISMATCH", "Password and Confirm Password not matched");
        }

        var password = passwordRepository.findByUser_UuidAndTypeAndStatus(user.getUuid(), PasswordType.LOGIN_PASSWORD, PasswordStatus.ACTIVE);
        if (password != null) {
            password.setStatus(PasswordStatus.INACTIVE);
            passwordRepository.save(password);
        }

        passwordRepository.save(Password.builder()
                .user(user)
                .password(passwordEncoder.encode(forgotPasswordRequestDto.getPassword()))
                .status(PasswordStatus.ACTIVE)
                .type(PasswordType.LOGIN_PASSWORD)
                .invalidPasswordEntryCount(0)
                .build());
        return SuccessResponseDto.builder()
                .success(true)
                .userId(userId)
                .message("Password Reset Successful")
                .build();
    }

    @Override
    public SuccessResponseDto registerInternetBanking(InternetBankingRegisterDto internetBankingDto) {
        var user = userRepository.getByAccountNumber(internetBankingDto.getAccountNumber());
        if (user == null) {
            throw new InvalidInputException("INVALID_ACCOUNT_NUMBER", "Account not exists with account number: " + internetBankingDto.getAccountNumber());
        }
        if (!internetBankingDto.getLoginPassword().equals(internetBankingDto.getConfirmLoginPassword())) {
            throw new InvalidInputException("PASSWORD_MISMATCH", "Login Password and Confirm Login Password not matched");
        }
        if (!internetBankingDto.getTransactionPassword().equals(internetBankingDto.getConfirmTransactionPassword())) {
            throw new InvalidInputException("PASSWORD_MISMATCH", "Transaction Password and Confirm Transaction Password not matched");
        }
        var loginPassword = passwordRepository.findByUser_UuidAndTypeAndStatus(user.getUuid(), PasswordType.LOGIN_PASSWORD, PasswordStatus.ACTIVE);
        if (loginPassword != null) {
            loginPassword.setStatus(PasswordStatus.INACTIVE);
            passwordRepository.save(loginPassword);
        }

        var transactionPassword = passwordRepository.findByUser_UuidAndTypeAndStatus(user.getUuid(), PasswordType.TRANSACTION_PASSWORD, PasswordStatus.ACTIVE);
        if (transactionPassword != null) {
            transactionPassword.setStatus(PasswordStatus.INACTIVE);
            passwordRepository.save(transactionPassword);
        }

        passwordRepository.save(Password.builder()
                .user(user)
                .password(passwordEncoder.encode(internetBankingDto.getLoginPassword()))
                .type(PasswordType.LOGIN_PASSWORD)
                .status(PasswordStatus.ACTIVE)
                .invalidPasswordEntryCount(0)
                .build());

        passwordRepository.save(Password.builder()
                .user(user)
                .password(passwordEncoder.encode(internetBankingDto.getTransactionPassword()))
                .type(PasswordType.TRANSACTION_PASSWORD)
                .status(PasswordStatus.ACTIVE)
                .invalidPasswordEntryCount(0)
                .build());

        return SuccessResponseDto.builder()
                .success(true)
                .userId(user.getUuid())
                .message("Internet Banking Registration is Successful")
                .build();
    }

    @Override
    public SuccessResponseDto forgotUserId(String accountNumber, Long otpNumber) {
        var user = userRepository.getByAccountNumber(accountNumber);
        if (user == null) {
            throw new NotFoundException("ACCOUNT_NOT_FOUND", "Account not found with number: " + accountNumber);
        }

        var otp = otpRepository.findByUser_UuidAndStatusAndType(user.getUuid(), OtpStatus.ACTIVE, OtpType.FORGOT_USERID);
        if (otp == null) {
            otpRepository.save(Otp.builder()
                    .user(user)
                    .otp(generateOTP())
                    .type(OtpType.FORGOT_USERID)
                    .status(OtpStatus.ACTIVE)
                    .build());

            return SuccessResponseDto.builder()
                    .success(false)
                    .userId(user.getUuid())
                    .message("Otp sent successfully")
                    .build();
        }
        if (!otpNumber.equals(otp.getOtp())) {
            throw new InvalidInputException("INVALID_OTP", "Invalid OTP");
        }
        otp.setStatus(OtpStatus.INACTIVE);
        otpRepository.save(otp);

        sendForgotUserIdMail(user.getUuid());

        return SuccessResponseDto.builder()
                .success(true)
                .userId(user.getUuid())
                .message("UserId has been sent to mail, Please check")
                .build();
    }

    @Override
    public SuccessResponseDto sendOtpWithUsername(String username, OtpType otpType) {
        var user = userRepository.findByEmail(username);
        if (user == null) {
            throw new NotFoundException("USER_NOT_FOUND", "User not found with username: " + username);
        }
        return sendOtpToUser(user, otpType);
    }

    @Override
    public SuccessResponseDto forgotPassword(String username, Long otpNumber) {
        var user = userRepository.findByEmail(username);
        if (user == null) {
            throw new NotFoundException("ACCOUNT_NOT_FOUND", "Account not found with username: " + username);
        }

        var otp = otpRepository.findByUser_UuidAndStatusAndType(user.getUuid(), OtpStatus.ACTIVE, OtpType.RESET_PASSWORD);
        if (otp == null) {
            otpRepository.save(Otp.builder()
                    .user(user)
                    .otp(generateOTP())
                    .type(OtpType.RESET_PASSWORD)
                    .status(OtpStatus.ACTIVE)
                    .build());
            sendResetPasswordMail(user.getUuid());
            return SuccessResponseDto.builder()
                    .success(false)
                    .userId(user.getUuid())
                    .message("Otp sent successfully")
                    .build();
        } else if (!otpNumber.equals(otp.getOtp())) {
            throw new InvalidInputException("INVALID_OTP", "Invalid OTP");
        }

        return SuccessResponseDto.builder()
                .success(true)
                .userId(user.getUuid())
                .message("Password reset successful")
                .build();
    }


    private SuccessResponseDto sendOtpToUser(User user, OtpType otpType) {
        var existingOtp = otpRepository.findByUser_UuidAndStatusAndType(user.getUuid(), OtpStatus.ACTIVE, otpType);
        if (existingOtp != null) {
            existingOtp.setStatus(OtpStatus.INACTIVE);
            otpRepository.save(existingOtp);
        }

        otpRepository.save(Otp.builder()
                .user(user)
                .status(OtpStatus.ACTIVE)
                .otp(generateOTP())
                .type(otpType)
                .build());

        if (otpType.equals(OtpType.FORGOT_USERID)) {
            sendForgotUserIdOTPMail(user.getUuid());
        } else if (otpType.equals(OtpType.RESET_PASSWORD)) {
            sendResetPasswordMail(user.getUuid());
        } else if (otpType.equals(OtpType.INTERNET_BANKING_REGISTRATION)) {
            sendInternetBankingRegstrationOtpMail(user.getUuid());
        }

        return SuccessResponseDto.builder()
                .success(true)
                .userId(user.getUuid())
                .message("Otp sent successfully")
                .build();
    }

    private String generateAccountNumber() {
        long count = userRepository.count();
        return String.format("%012d", INITIAL_ACCOUNT_NUMBER + count + 1);
    }

    private Long generateOTP() {
        return 100000L + random.nextLong(900000L);
    }

    private void sendEmail(UUID userId, String subject, String templateName, Map<String, Object> variables) {
        Thread thread = new Thread(() -> {
            var user = getById(userId);

            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message);

                helper.setFrom(defaultEmail, subject);
                helper.setTo(user.getEmail());
                helper.setSubject(subject);

                Context context = new Context();
                context.setVariables(variables);

                helper.setText(templateEngine.process(templateName, context), true);

                mailSender.send(message);
            } catch (MessagingException | UnsupportedEncodingException e) {
                log.error("Error sending email to user: {}", userId, e);
            }
        });
        thread.start();
    }

    public void sendOpenAccountEmail(UUID userId) {
        var user = getById(userId);
        Map<String, Object> variables = Map.of(
                "name", user.getFirstName() + " " + user.getLastName(),
                "accountNumber", user.getAccountNumber(),
                "email", user.getEmail(),
                "phoneNumber", user.getMobileNumber()
        );
        sendEmail(userId, "Open Account Successful", "/mail/open-account-mail", variables);
    }

    public void sendForgotUserIdMail(UUID userId) {
        var user = getById(userId);
        Map<String, Object> variables = Map.of(
                "name", user.getFirstName() + " " + user.getLastName(),
                "userId", user.getEmail()
        );
        sendEmail(userId, "Forgot User ID", "/mail/send-forgot-userId-mail", variables);
    }

    public void sendForgotUserIdOTPMail(UUID userId) {
        var user = getById(userId);
        var otp = otpRepository.findByUser_UuidAndStatusAndType(userId, OtpStatus.ACTIVE, OtpType.FORGOT_USERID);
        Map<String, Object> variables = Map.of(
                "name", user.getFirstName() + " " + user.getLastName(),
                "OTP", otp.getOtp()
        );
        sendEmail(userId, "Forgot User ID", "/mail/send-forgot-userid-otp-mail", variables);
    }

    public void sendResetPasswordMail(UUID userId) {
        var user = getById(userId);
        var otp = otpRepository.findByUser_UuidAndStatusAndType(userId, OtpStatus.ACTIVE, OtpType.RESET_PASSWORD);

        Map<String, Object> variables = Map.of(
                "name", user.getFirstName() + " " + user.getLastName(),
                "OTP", otp.getOtp()
        );
        sendEmail(userId, "Reset Password OTP", "/mail/send-resetpassword-otp-mail", variables);
    }

    public void sendInternetBankingRegstrationOtpMail(UUID userId) {
        var user = getById(userId);
        var otp = otpRepository.findByUser_UuidAndStatusAndType(userId, OtpStatus.ACTIVE, OtpType.INTERNET_BANKING_REGISTRATION);

        Map<String, Object> variables = Map.of(
                "name", user.getFirstName() + " " + user.getLastName(),
                "OTP", otp.getOtp()
        );
        sendEmail(userId, "Internet Banking Registration OTP", "/mail/send-internet-banking-registration-otp-mail", variables);
    }

    private User getById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND", "User not found with ID: " + userId));
    }
}
