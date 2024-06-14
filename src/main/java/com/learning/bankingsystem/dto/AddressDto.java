package com.learning.bankingsystem.dto;

import com.learning.bankingsystem.entity.AddressType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressDto {
    private String addressLine1;

    private String addressLine2;

    private String landmark;

    private String state;

    private String city;

    private String pincode;
}
