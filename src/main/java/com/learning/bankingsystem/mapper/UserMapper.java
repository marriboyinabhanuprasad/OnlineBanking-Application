package com.learning.bankingsystem.mapper;

import com.learning.bankingsystem.dto.OpenAccountDto;
import com.learning.bankingsystem.entity.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {
    User openAccountDtoToUser(OpenAccountDto openAccountDto);
}
