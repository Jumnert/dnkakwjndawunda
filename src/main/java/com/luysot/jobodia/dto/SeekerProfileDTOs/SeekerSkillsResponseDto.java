package com.luysot.jobodia.dto.SeekerProfileDTOs;

import com.luysot.jobodia.dto.SkillsDTOs.SkillResponseDto;
import com.luysot.jobodia.model.Skills;
import com.luysot.jobodia.model.enums.UserGender;
import lombok.Builder;

import java.util.Set;

@Builder
public record SeekerSkillsResponseDto(
        Long id,
        String username,
        String email,
        String phoneNumber,
        String profilePictureUrl,
        UserGender gender,
        String address,
        String userId,
        Set<SkillResponseDto> skills
) {
}
