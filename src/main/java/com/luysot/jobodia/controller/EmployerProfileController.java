package com.luysot.jobodia.controller;

import com.luysot.jobodia.dto.EmployerProfileDTOs.EmployerProfileRequestDto;
import com.luysot.jobodia.dto.EmployerProfileDTOs.EmployerProfileResponseDto;
import com.luysot.jobodia.model.EmployerProfiles;
import com.luysot.jobodia.service.EmployerProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
        return ResponseEntity.status(HttpStatus.CREATED).body(employerProfileService.createProfile(dto,file,authentication.getName()));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('EMPLOYER')")
    ResponseEntity<EmployerProfileResponseDto> myProfile(Authentication authentication) {
        return ResponseEntity.ok(employerProfileService.findOwnProfile(authentication.getName()));
    }

//    @PutMapping("/me")
//    @PreAuthorize("hasRole('EMPLOYER')")
//    ResponseEntity<EmployerProfileResponseDto> updateMyProfile(
//            @Valid @RequestPart(name = "profile") EmployerProfileRequestDto dto,
//            @RequestPart(name = "file", required = false) MultipartFile file,
//            Authentication authentication
//    ) throws IOException {
//        return ResponseEntity.ok(employerProfileService.updateOwnProfile(dto, file, authentication.getName()));
//    }

    @GetMapping("/me/logo")
    @PreAuthorize("hasRole('EMPLOYER')")
    ResponseEntity<Resource> myCompanyLogo(Authentication authentication) throws MalformedURLException, FileNotFoundException {
        EmployerProfiles profile = employerProfileService.findOwnProfileEntity(authentication.getName());
        Resource resource = employerProfileService.loadOwnCompanyLogo(authentication.getName());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(profile.getCompanyLogoContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(resource);
    }
}
