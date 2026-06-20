package com.luysot.jobodia.dto.SeekerProfileDTOs;

import lombok.Builder;

@Builder
public record SeekerResumeResponseDto(
        Long id,
        String title,
        String resumeUrl
) {
}
