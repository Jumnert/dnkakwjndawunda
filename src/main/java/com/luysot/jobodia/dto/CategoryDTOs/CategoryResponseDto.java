package com.luysot.jobodia.dto.CategoryDTOs;

import lombok.Builder;

@Builder
public record CategoryResponseDto(
        Long id,
        String categoryName
) {
}
