package com.luysot.jobodia.dto.IndustryDTOs;

import lombok.Builder;

@Builder
public record IndustryRequestDto(
        String industryName
) {
}
