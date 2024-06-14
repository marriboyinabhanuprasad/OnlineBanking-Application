package com.learning.bankingsystem.service;

import com.learning.bankingsystem.dto.AdminActionDto;
import com.learning.bankingsystem.dto.SuccessResponseDto;
import com.learning.bankingsystem.entity.AdminActionType;
import com.learning.bankingsystem.entity.User;

import java.util.List;
import java.util.UUID;

public interface AdminService {
    SuccessResponseDto actionAccount(UUID userId, AdminActionType adminActionType, String comments);

    List<User> getPendingUsers();
}
