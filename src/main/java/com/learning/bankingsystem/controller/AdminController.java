package com.learning.bankingsystem.controller;

import com.learning.bankingsystem.dto.SuccessResponseDto;
import com.learning.bankingsystem.entity.AdminActionType;
import com.learning.bankingsystem.entity.User;
import com.learning.bankingsystem.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;



    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/action/account/{userId}")
    public ResponseEntity<SuccessResponseDto> actionAccount(@PathVariable UUID userId,
                                                            @RequestParam AdminActionType adminActionType,
                                                            @RequestParam(required = false, name = "comments") String comments) {
        return new ResponseEntity<>(adminService.actionAccount(userId, adminActionType, comments), HttpStatus.OK);
    }

    @GetMapping("/pendingActions")
    public ResponseEntity<List<User>> getPendingUsers() {
        return new ResponseEntity<>(adminService.getPendingUsers(), HttpStatus.OK);
    }
}
