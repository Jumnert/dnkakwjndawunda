package com.luysot.jobodia.dto.SeekerProfileDTOs;

import lombok.Builder;

import java.util.Set;

@Builder
public record SeekerSkillsRequestDto(
        Set<Long> skillId
) {
}
