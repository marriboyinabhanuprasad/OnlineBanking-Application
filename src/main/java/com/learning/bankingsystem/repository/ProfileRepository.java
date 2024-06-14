package com.learning.bankingsystem.repository;

import com.learning.bankingsystem.entity.AdminActionType;
import com.learning.bankingsystem.entity.Profile;
import com.learning.bankingsystem.entity.ProfileStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    Profile findByUser_Uuid(UUID userUuid);

    List<Profile> getByAdminActionTypeAndProfileStatus(AdminActionType adminActionType, ProfileStatus profileStatus);
}
