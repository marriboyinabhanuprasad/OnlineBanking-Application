package com.learning.bankingsystem.mapper;

import com.learning.bankingsystem.dto.OccupationDto;
import com.learning.bankingsystem.entity.Occupation;
import org.mapstruct.Mapper;

@Mapper
public interface OccupationMapper {
    Occupation occupationDtoToOccupation(OccupationDto occupationDto);
}
