package com.luysot.jobodia.mapper;

import com.luysot.jobodia.dto.SeekerProfileDTOs.SeekerProfileResponseDto;
import com.luysot.jobodia.model.SeekerProfiles;
import org.springframework.stereotype.Component;

@Component
public class SeekerProfileMapper {
    public SeekerProfileResponseDto toDto(SeekerProfiles profile){
        return SeekerProfileResponseDto.builder()
                .id(profile.getId())
                .username(profile.getUser().getUsername())
                .email(profile.getUser().getEmail())
                .phoneNumber(profile.getPhoneNumber())
                .profilePictureUrl(profile.getProfilePictureUrl())
                .gender(profile.getGender())
                .address(profile.getAddress())
                .userId(profile.getUser().getUserId())
                .build();
    }
}
