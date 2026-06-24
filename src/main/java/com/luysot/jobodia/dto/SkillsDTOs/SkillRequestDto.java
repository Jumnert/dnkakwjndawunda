package com.luysot.jobodia.dto.SkillsDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SkillRequestDto(
        @NotBlank(message = "Skill name is required")
        @Size(max = 100, message = "Skill name cannot exceed 100 characters")
        String skillName
) {
}
