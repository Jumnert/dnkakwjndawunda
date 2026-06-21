package com.luysot.jobodia.mapper;

import com.luysot.jobodia.dto.EmployerProfileDTOs.EmployerProfileResponseDto;
import com.luysot.jobodia.model.EmployerProfiles;
import org.springframework.stereotype.Component;

@Component
public class EmployerProfileMapper {
    public EmployerProfileResponseDto toDto(EmployerProfiles profile){
        return EmployerProfileResponseDto.builder()
                .id(profile.getId())
                .companyName(profile.getCompanyName())
                .phoneNumber(profile.getPhoneNumber())
                .location(profile.getLocation())
                .description(profile.getDescription())
                .email(profile.getUser().getEmail())
                .userId(profile.getUser().getUserId())
                .companyLogoUrl(profile.getCompanyLogoUrl())
                .build();
    }
}
