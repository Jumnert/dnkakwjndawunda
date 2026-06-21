package com.luysot.jobodia.dto.EmployerProfileDTOs;

import lombok.Builder;

@Builder
public record EmployerProfileResponseDto(
        Long id,
        String email,
        String companyName,
        String phoneNumber,
        String location,
        String description,
        String userId,
        String companyLogoUrl
) {
}
