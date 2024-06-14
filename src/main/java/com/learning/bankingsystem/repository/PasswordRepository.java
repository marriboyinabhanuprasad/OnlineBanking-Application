package com.learning.bankingsystem.repository;

import com.learning.bankingsystem.entity.Password;
import com.learning.bankingsystem.entity.PasswordStatus;
import com.learning.bankingsystem.entity.PasswordType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PasswordRepository extends JpaRepository<Password, UUID> {
    Password findByUser_UuidAndTypeAndStatus(UUID userUuid, PasswordType type, PasswordStatus status);
}
