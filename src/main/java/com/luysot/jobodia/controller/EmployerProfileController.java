package com.luysot.jobodia.controller;

import com.luysot.jobodia.dto.EmployerProfileDTOs.EmployerProfileRequestDto;
import com.luysot.jobodia.dto.EmployerProfileDTOs.EmployerProfileResponseDto;
import com.luysot.jobodia.service.EmployerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employer-profiles")
public class EmployerProfileController {
    private final EmployerProfileService employerProfileService;

    @PostMapping
    ResponseEntity<EmployerProfileResponseDto> createProfile(
            @RequestPart(name = "profile") EmployerProfileRequestDto dto,
            @RequestPart(name = "file",required = false) MultipartFile file,
            Authentication authentication
    ) throws IOException {
        return ResponseEntity.ok(employerProfileService.createProfile(dto,file,authentication.getName()));
    }

}


