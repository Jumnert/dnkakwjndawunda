package com.luysot.jobodia.dto.JobDTOs;

import com.luysot.jobodia.model.enums.JobStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateJobStatusRequestDto(
        @NotNull(message = "Job status is required")
        JobStatus status
) {
}
