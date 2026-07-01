package com.luysot.jobodia.controller;

import com.luysot.jobodia.dto.ApplicationDTOs.ApplicationRequestDto;
import com.luysot.jobodia.dto.ApplicationDTOs.ApplicationResponseDto;
import com.luysot.jobodia.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/applications")
public class ApplicationController {
    private final ApplicationService applicationService;

    @PostMapping
//    @PreAuthorize("hasRole('SEEKER')")
    ResponseEntity<ApplicationResponseDto> apply(Authentication authentication,@Valid @RequestBody ApplicationRequestDto request)
    {
        return ResponseEntity.ok(applicationService.apply(authentication.getName(),request));
    }

}
