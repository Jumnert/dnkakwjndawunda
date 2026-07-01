package com.luysot.jobodia.dto.ApplicationDTOs;

import com.luysot.jobodia.model.enums.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ApplicationRequestDto(
        @NotNull(message = "Job id can't be null.")
        Long jobId,

        @NotNull(message = "Resume id can't be null.")
        Long resumeId,

        @NotNull(message = "Cover letter id can't be null.")
        Long coverLetterId
) {
}
