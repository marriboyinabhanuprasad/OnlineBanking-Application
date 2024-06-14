package com.learning.bankingsystem.exception.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class FieldErrorListResponse {
    private StatusType status = StatusType.ERROR;
    private List<ErrorField> errors;

}
