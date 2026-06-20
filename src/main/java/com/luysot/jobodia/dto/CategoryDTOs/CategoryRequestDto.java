package com.luysot.jobodia.dto.CategoryDTOs;

import lombok.Builder;

@Builder
public record CategoryRequestDto(
        String categoryName
) {
}
