package com.learning.bankingsystem.repository;

import com.learning.bankingsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    User findByEmail(String email);

    User getByAccountNumber(String accountNumber);
}
