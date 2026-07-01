package com.luysot.jobodia.mapper;

import com.luysot.jobodia.dto.ApplicationDTOs.ApplicationResponseDto;
import com.luysot.jobodia.model.Applications;
import org.springframework.stereotype.Component;

@Component
public class ApplicationMapper {
    public ApplicationResponseDto toDto(Applications application){
        return ApplicationResponseDto.builder()
                .id(application.getId())
                .jobId(application.getJob().getId())
                .seekerId(application.getSeeker().getId())
                .resumeId(application.getResume().getId())
                .coverLetterId(application.getCoverLetter().getId())
                .status(application.getStatus())
                .build();
    }
}
