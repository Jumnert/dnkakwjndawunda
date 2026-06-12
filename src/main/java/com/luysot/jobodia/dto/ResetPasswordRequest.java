package com.luysot.jobodia.dto;

import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        String email,
        @Size(min = 8)
        String password,
        String otp
) {
}
