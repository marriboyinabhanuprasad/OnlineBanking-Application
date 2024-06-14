package com.learning.bankingsystem.dto;

import com.learning.bankingsystem.entity.TitleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OpenAccountDto {

    private TitleType title;

    private String firstName;

    private String middleName;

    private String lastName;

    private String fatherName;

    private String mobileNumber;

    private String email;

    private String aadharNumber;

    private Date dateOfBirth;

    private AddressDto residentialAddress;

    private AddressDto permanentAddress;

    private OccupationDto occupation;

    private Boolean wantDebitCard;

    private Boolean optForNetBanking;
}
