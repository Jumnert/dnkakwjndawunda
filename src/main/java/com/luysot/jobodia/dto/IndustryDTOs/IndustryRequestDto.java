package com.luysot.jobodia.dto.IndustryDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record IndustryRequestDto(
        @NotBlank(message = "Industry name is required")
        @Size(max = 100, message = "Industry name cannot exceed 100 characters")
        String industryName
) {
}
