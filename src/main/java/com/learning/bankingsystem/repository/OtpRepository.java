package com.learning.bankingsystem.repository;

import com.learning.bankingsystem.entity.Otp;
import com.learning.bankingsystem.entity.OtpStatus;
import com.learning.bankingsystem.entity.OtpType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OtpRepository extends JpaRepository<Otp, UUID> {
    Otp findByUser_UuidAndStatusAndType(UUID userId, OtpStatus status, OtpType type);
}
