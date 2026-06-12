package com.luysot.jobodia.dto;

import com.luysot.jobodia.model.enums.Roles;
import jakarta.validation.constraints.Email;
import lombok.Builder;

@Builder
public record UserResponseDto(
        String username,
        @Email
        String email,
        String userId,
        Boolean isVerified,
        Roles role
) {
}
