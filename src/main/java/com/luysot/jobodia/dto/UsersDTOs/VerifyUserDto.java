package com.luysot.jobodia.dto.UsersDTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record VerifyUserDto(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "OTP is required")
        @Size(min = 6, max = 6, message = "OTP must be exactly 6 digits")
        @Pattern(regexp = "^[0-9]{6}$", message = "OTP must contain only digits")
        String otp
) {
}
