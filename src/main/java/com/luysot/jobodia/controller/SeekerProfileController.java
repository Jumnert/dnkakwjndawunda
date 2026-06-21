package com.luysot.jobodia.controller;

import com.luysot.jobodia.dto.SeekerProfileDTOs.SeekerProfileRequestDto;
import com.luysot.jobodia.dto.SeekerProfileDTOs.SeekerProfileResponseDto;
import com.luysot.jobodia.dto.SeekerProfileDTOs.SeekerSkillsRequestDto;
import com.luysot.jobodia.dto.SeekerProfileDTOs.SeekerSkillsResponseDto;
import com.luysot.jobodia.model.SeekerProfiles;
import com.luysot.jobodia.model.Users;
import com.luysot.jobodia.repository.SeekerProfileRepository;
import com.luysot.jobodia.repository.UserRepository;
import com.luysot.jobodia.service.SeekerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/seeker-profiles")
public class SeekerProfileController {
    private final SeekerProfileService seekerProfileService;
    private final UserRepository userRepository;
    private final SeekerProfileRepository seekerProfileRepository;

    @GetMapping
    ResponseEntity<SeekerSkillsResponseDto> myProfile(Authentication authentication){
        return ResponseEntity.ok(seekerProfileService.myProfile(authentication.getName()));
    }

    @PostMapping
    ResponseEntity<SeekerProfileResponseDto> createProfile(
            @RequestPart(name = "profile") SeekerProfileRequestDto dto,
            @RequestPart(name = "file",required = false) MultipartFile file,
            Authentication authentication
            ) throws IOException {
        return ResponseEntity.ok(seekerProfileService.createProfile(dto,file,authentication.getName()));
    }

    @GetMapping("/picture")
    ResponseEntity<Resource> viewProfilePicture(
            Authentication authentication
    ) throws MalformedURLException, FileNotFoundException {

        Resource resource = seekerProfileService.viewSeekerProfilePicture(authentication.getName());

        Users user = userRepository.findByEmail(authentication.getName())
                .orElseThrow();

        SeekerProfiles seekerProfile = seekerProfileRepository
                .findByUser(user)
                .orElseThrow();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        seekerProfile.getProfilePictureContentType()
                ))
                .body(resource);
    }

    @PostMapping("/skills")
    ResponseEntity<SeekerSkillsResponseDto> addSeekerSkills(@RequestBody SeekerSkillsRequestDto dto, Authentication authentication){
        return ResponseEntity.ok(seekerProfileService.addSeekerSkills(authentication.getName(),dto));
    }
}
