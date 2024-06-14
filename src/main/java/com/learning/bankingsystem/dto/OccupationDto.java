package com.learning.bankingsystem.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OccupationDto {
    private String occupationType;

    private String sourceIncome;

    private String grossAnnualIncome;
}
