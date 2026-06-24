package com.luysot.jobodia.dto.EmployerProfileDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record EmployerProfileRequestDto(
        @NotBlank(message = "Company name is required")
        @Size(max = 255, message = "Company name cannot exceed 255 characters")
        String companyName,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^\\+?[0-9\\s\\-\\(\\)]{8,20}$", message = "Invalid phone number format")
        String phoneNumber,

        @NotBlank(message = "Location is required")
        @Size(max = 255, message = "Location cannot exceed 255 characters")
        String location,

        @NotBlank(message = "Description is required")
        @Size(max = 5000, message = "Description cannot exceed 5000 characters")
        String description
) {
}
