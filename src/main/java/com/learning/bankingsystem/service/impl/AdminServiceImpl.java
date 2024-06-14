package com.learning.bankingsystem.service.impl;

import com.learning.bankingsystem.dto.AdminActionDto;
import com.learning.bankingsystem.dto.SuccessResponseDto;
import com.learning.bankingsystem.entity.AdminActionType;
import com.learning.bankingsystem.entity.ProfileStatus;
import com.learning.bankingsystem.entity.User;
import com.learning.bankingsystem.exception.NotFoundException;
import com.learning.bankingsystem.repository.ProfileRepository;
import com.learning.bankingsystem.repository.UserRepository;
import com.learning.bankingsystem.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    private final ProfileRepository profileRepository;

    @Override
    public SuccessResponseDto actionAccount(UUID userId, AdminActionType adminActionType, String comments) {
        var user = getById(userId);
        var profile = profileRepository.findByUser_Uuid(user.getUuid());
        System.out.println(profile);
        profile.setAdminActionType(adminActionType);
        if (comments != null) {
            profile.setComments(comments);
        }
        profileRepository.save(profile);

        return SuccessResponseDto.builder()
                .success(true)
                .userId(userId)
                .message("Account has been " + adminActionType.toString() + " by admin")
                .build();
    }

    @Override
    public List<User> getPendingUsers() {
        var profiles = profileRepository.getByAdminActionTypeAndProfileStatus(AdminActionType.WAITING_FOR_APPROVAL, ProfileStatus.ACTIVE);

        return profiles.stream()
                .map(profile -> profile.getUser())
                .toList();
    }

    private User getById(UUID userId) {
        return userRepository.findById(userId).
                orElseThrow(() -> new NotFoundException("USER_NOT_FOUND", "User not found with Id: " + userId));
    }
}
