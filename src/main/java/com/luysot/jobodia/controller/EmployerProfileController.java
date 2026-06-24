package com.luysot.jobodia.controller;

import com.luysot.jobodia.dto.EmployerProfileDTOs.EmployerProfileRequestDto;
import com.luysot.jobodia.dto.EmployerProfileDTOs.EmployerProfileResponseDto;
import com.luysot.jobodia.service.EmployerProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employer-profiles")
public class EmployerProfileController {
    private final EmployerProfileService employerProfileService;

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    ResponseEntity<EmployerProfileResponseDto> createProfile(
            @Valid @RequestPart(name = "profile") EmployerProfileRequestDto dto,
            @RequestPart(name = "file",required = false) MultipartFile file,
            Authentication authentication
    ) throws IOException {
        return ResponseEntity.ok(employerProfileService.createProfile(dto,file,authentication.getName()));
    }

    @PreAuthorize("hasRole('EMPLOYER')")
    @GetMapping("/company-logo")
    ResponseEntity<Resource> companyProfile(
            Authentication authentication
    ) throws MalformedURLException, FileNotFoundException {
        return ResponseEntity.ok(employerProfileService.viewCompnayLogo(authentication.getName()));
    }
}
