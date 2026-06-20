package com.luysot.jobodia.dto.SeekerProfileDTOs;

import lombok.Builder;

@Builder
public record SeekerCoverLetterResponseDto(
        Long id,
        String title,
        String coverLetterUrl
) {
}
