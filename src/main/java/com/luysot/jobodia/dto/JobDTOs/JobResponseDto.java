package com.luysot.jobodia.dto.JobDTOs;

import com.luysot.jobodia.dto.EmployerProfileDTOs.EmployerProfileResponseDto;
import com.luysot.jobodia.model.enums.JobGender;
import com.luysot.jobodia.model.enums.JobLevel;
import com.luysot.jobodia.model.enums.JobSite;
import com.luysot.jobodia.model.enums.JobStatus;
import com.luysot.jobodia.model.enums.JobTime;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Builder
public record JobResponseDto(

        Long id,

        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title cannot exceed 255 characters")
        String title,

        @NotNull(message = "Minimum salary is required")
        @DecimalMin(value = "0.00", message = "Minimum salary must be greater than or equal to 0")
        @Digits(integer = 8, fraction = 2, message = "Invalid salary format")
        BigDecimal minSalary,

        @NotNull(message = "Maximum salary is required")
        @DecimalMin(value = "0.00", message = "Maximum salary must be greater than or equal to 0")
        @Digits(integer = 8, fraction = 2, message = "Invalid salary format")
        BigDecimal maxSalary,

        @NotEmpty(message = "At least one responsibility is required")
        List<@NotBlank(message = "Responsibility cannot be blank")
            String> responsibilities,

        @NotEmpty(message = "At least one requirement is required")
        List<@NotBlank(message = "Requirement cannot be blank")
                String> requirements,

        @NotBlank(message = "Description is required")
        String description,

        @Size(max = 1000, message = "Summary cannot exceed 1000 characters")
        String summary,

        @NotEmpty(message = "At least one benefit is required")
        List<@NotBlank(message = "Benifit cannot be blank")
                String> benefits,

        @NotNull(message = "Job type is required")
        JobTime jobType,

        @NotNull(message = "Job level is required")
        JobLevel jobLevel,

        @NotNull(message = "Job gender is required")
        JobGender jobGender,

        @NotNull(message = "Job site is required")
        JobSite jobSite,

        @NotNull(message = "Job status is required")
        JobStatus status,

        @Min(value = 0, message = "Years of experience cannot be negative")
        Long yearsOfExperience,

        List<String> languages,

        List<String> qualifications,

        @NotNull(message = "Available position is required")
        @Min(value = 1, message = "Available position must be at least 1")
        Integer availablePosition,

        @NotNull(message = "Expiration date is required")
        @Future(message = "Expiration date must be in the future")
        LocalDateTime expiresAt,

        Set<Long> categoriesId,
        Set<Long> skillsId,

        Long industriesId,

        EmployerProfileResponseDto employer
) {
}
