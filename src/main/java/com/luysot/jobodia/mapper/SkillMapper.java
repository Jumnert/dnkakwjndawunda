package com.luysot.jobodia.mapper;

import com.luysot.jobodia.dto.SkillsDTOs.SkillResponseDto;
import com.luysot.jobodia.model.Skills;
import org.springframework.stereotype.Component;

@Component
public class SkillMapper {

    public SkillResponseDto toDto(Skills skill){
        return SkillResponseDto.builder()
                .id(skill.getId()).skillName(skill.getSkillName())
                .build();
    }


}
