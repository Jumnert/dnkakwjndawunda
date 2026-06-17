package com.luysot.jobodia.dto.SkillsDTOs;

import lombok.Builder;

@Builder
public record SkillResponseDto(
        Long id,
        String skillName
) {
}
