package com.luysot.jobodia.dto.SeekerProfileDTOs;

import com.luysot.jobodia.model.enums.UserGender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record SeekerProfileRequestDto(
        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^\\+?[0-9\\s\\-\\(\\)]{8,20}$", message = "Invalid phone number format")
        String phoneNumber,

        @NotNull(message = "Gender is required")
        UserGender gender,

        @NotBlank(message = "Address is required")
        @Size(max = 255, message = "Address cannot exceed 255 characters")
        String address
) {
}
