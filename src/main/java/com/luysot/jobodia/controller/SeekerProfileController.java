package com.luysot.jobodia.controller;

import com.luysot.jobodia.dto.SeekerProfileDTOs.SeekerProfileRequestDto;
import com.luysot.jobodia.dto.SeekerProfileDTOs.SeekerProfileResponseDto;
import com.luysot.jobodia.dto.SeekerProfileDTOs.SeekerSkillsRequestDto;
import com.luysot.jobodia.dto.SeekerProfileDTOs.SeekerSkillsResponseDto;
import com.luysot.jobodia.service.SeekerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/seeker-profiles")
public class SeekerProfileController {
    private final SeekerProfileService seekerProfileService;

    @GetMapping
    ResponseEntity<SeekerProfileResponseDto> myProfile(Authentication authentication){
        return ResponseEntity.ok(seekerProfileService.myProfile(authentication.getName()));
    }

    @PostMapping
    ResponseEntity<SeekerProfileResponseDto> createProfile(
            @RequestBody SeekerProfileRequestDto dto,
            Authentication authentication
            ){
        return ResponseEntity.ok(seekerProfileService.createProfile(dto,authentication.getName()));
    }

    @PostMapping("/picture")
    ResponseEntity<?> uploadProfilePicture(
            @RequestParam MultipartFile file,
            Authentication authentication
            ) throws IOException {
        seekerProfileService.uploadProfilePicture(authentication.getName(),file);
        return ResponseEntity.ok("Uploaded");
    }

    @PostMapping("/skills")
    ResponseEntity<SeekerSkillsResponseDto> addSeekerSkills(@RequestBody SeekerSkillsRequestDto dto, Authentication authentication){
        return ResponseEntity.ok(seekerProfileService.addSeekerSkills(authentication.getName(),dto));
    }
}
