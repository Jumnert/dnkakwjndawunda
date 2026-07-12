package com.luysot.jobodia.controller;

import com.luysot.jobodia.dto.ApplicationDTOs.ApplicationRequestDto;
import com.luysot.jobodia.dto.ApplicationDTOs.ApplicationNotifyRequestDto;
import com.luysot.jobodia.dto.ApplicationDTOs.ApplicationResponseDto;
import com.luysot.jobodia.dto.ApplicationDTOs.UpdateApplicationStatusRequestDto;
import com.luysot.jobodia.service.ApplicationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/applications")
public class ApplicationController {
    private final ApplicationService applicationService;

    @PostMapping
    @PreAuthorize("hasRole('SEEKER')")
    ResponseEntity<ApplicationResponseDto> apply(Authentication authentication,@Valid @RequestBody ApplicationRequestDto request)
    {
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationService.apply(authentication.getName(),request));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('SEEKER')")
    ResponseEntity<Page<ApplicationResponseDto>> findSeekerOwnApplications(
            Authentication authentication,
            @PageableDefault(size = 10, sort = "appliedAt", direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.ok(applicationService.findSeekerOwnApplications(authentication.getName(), pageable));
    }

    @GetMapping("/me/{id}")
    @PreAuthorize("hasRole('SEEKER')")
    ResponseEntity<ApplicationResponseDto> findSeekerOwnApplication(@PathVariable @Positive(message = "Application id must be positive") Long id, Authentication authentication){
        return ResponseEntity.ok(applicationService.findSeekerOwnApplication(id,authentication.getName()));
    }

    @DeleteMapping("/me/{id}")
    @PreAuthorize("hasRole('SEEKER')")
    ResponseEntity<Void> deleteSeekerOwnApplication(@PathVariable @Positive(message = "Application id must be positive") Long id, Authentication authentication){
        applicationService.deleteSeekerOwnApplication(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/applicants")
    @PreAuthorize("hasRole('EMPLOYER')")
    ResponseEntity<Page<ApplicationResponseDto>> findApplicants(
            Authentication authentication,
            @PageableDefault(size = 10, sort = "appliedAt", direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.ok(applicationService.findApplicants(authentication.getName(), pageable));
    }

    @GetMapping("/applicants/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    ResponseEntity<ApplicationResponseDto> findApplicant(@PathVariable @Positive(message = "Application id must be positive") Long id,Authentication authentication){
        return ResponseEntity.ok(applicationService.findApplicant(id,authentication.getName()));
    }

    @PatchMapping("/applicants/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    ResponseEntity<ApplicationResponseDto> updateApplicationStatus(
            @Positive(message = "Application id must be positive") @PathVariable Long id,
            @Valid @RequestBody UpdateApplicationStatusRequestDto reqStatus,
            Authentication authentication){
        return ResponseEntity.ok(applicationService.updateApplicationStatus(id,reqStatus,authentication.getName()));
    }

    @PostMapping({"/{id}/notify", "/applicants/{id}/notify"})
    @PreAuthorize("hasRole('EMPLOYER')")
    ResponseEntity<Void> notifyApplicant(
            @PathVariable @Positive(message = "Application id must be positive") Long id,
            @Valid @RequestBody ApplicationNotifyRequestDto request,
            Authentication authentication
    ) {
        applicationService.notifyApplicant(id, request, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
