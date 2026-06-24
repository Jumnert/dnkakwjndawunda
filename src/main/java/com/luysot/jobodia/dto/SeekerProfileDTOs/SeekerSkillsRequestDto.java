package com.luysot.jobodia.dto.SeekerProfileDTOs;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.Set;

@Builder
public record SeekerSkillsRequestDto(
        @NotEmpty(message = "At least one skill ID is required")
        Set<Long> skillId
) {
}
