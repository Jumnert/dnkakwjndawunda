package com.luysot.jobodia.dto.EmployerProfileDTOs;

import lombok.Builder;

@Builder
public record EmployerProfileRequestDto(
        String companyName,
        String phoneNumber,
        String location,
        String description
) {
}
