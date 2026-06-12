package com.luysot.jobodia.dto;

import jakarta.validation.constraints.Email;

public record VerifyUserDto(
        @Email
        String email,
        String otp
) {
}
