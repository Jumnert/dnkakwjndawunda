package com.luysot.jobodia.dto.ApplicationDTOs;

import com.luysot.jobodia.model.enums.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ApplicationResponseDto(
        Long id,

        @NotNull(message = "Job id can't be null.")
        Long jobId,

        @NotNull(message = "Seeker id can't be null.")
        Long seekerId,

        @NotNull(message = "Resume id can't be null.")
        Long resumeId,

        @NotNull(message = "Cover letter id can't be null.")
        Long coverLetterId,
        
        ApplicationStatus status
) {
}
