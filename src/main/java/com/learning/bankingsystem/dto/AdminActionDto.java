package com.learning.bankingsystem.dto;

import com.learning.bankingsystem.entity.AdminActionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminActionDto {

    private AdminActionType adminAction;

    private String Comments;
}
