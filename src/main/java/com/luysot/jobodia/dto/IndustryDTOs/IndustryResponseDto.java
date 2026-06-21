package com.luysot.jobodia.dto.IndustryDTOs;

import lombok.Builder;

@Builder
public record IndustryResponseDto(
        Long id,
        String industryName
) {
}
