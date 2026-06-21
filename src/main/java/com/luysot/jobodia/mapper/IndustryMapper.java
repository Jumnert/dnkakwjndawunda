package com.luysot.jobodia.mapper;

import com.luysot.jobodia.dto.IndustryDTOs.IndustryResponseDto;
import com.luysot.jobodia.model.Industries;
import org.springframework.stereotype.Component;

@Component
public class IndustryMapper {
    public IndustryResponseDto toDto(Industries industry){
        return IndustryResponseDto.builder().id(industry.getId()).industryName(industry.getIndustryName()).build();
    }
}
