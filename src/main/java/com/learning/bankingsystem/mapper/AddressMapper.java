package com.learning.bankingsystem.mapper;

import com.learning.bankingsystem.dto.AddressDto;
import com.learning.bankingsystem.entity.Address;
import org.mapstruct.Mapper;

@Mapper
public interface AddressMapper {
    Address addressDtoTOAddress(AddressDto addressDto);
}
