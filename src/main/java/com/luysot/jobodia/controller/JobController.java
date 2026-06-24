package com.luysot.jobodia.controller;

import com.luysot.jobodia.dto.JobDTOs.JobRequestDto;
import com.luysot.jobodia.dto.JobDTOs.JobResponseDto;
import com.luysot.jobodia.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/jobs")
public class JobController {
    private final JobService jobService;

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    ResponseEntity<JobResponseDto> addJob(@RequestBody JobRequestDto request, Authentication authentication){
        return ResponseEntity.ok(jobService.addJob(authentication.getName(),request));
    }
}
