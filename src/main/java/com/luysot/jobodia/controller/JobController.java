package com.luysot.jobodia.controller;

import com.luysot.jobodia.dto.JobDTOs.JobRequestDto;
import com.luysot.jobodia.dto.JobDTOs.JobResponseDto;
import com.luysot.jobodia.model.Jobs;
import com.luysot.jobodia.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

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

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    ResponseEntity<JobResponseDto> updateJob(@PathVariable Long id,@RequestBody JobRequestDto request, Authentication authentication){
        return ResponseEntity.ok(jobService.updateJob(id,request,authentication.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    ResponseEntity<?> deleteJob(@PathVariable Long id, Authentication authentication){
        jobService.deleteJob(id,authentication.getName());
        return ResponseEntity.ok("Job deleted");
    }


    @GetMapping("/{id}")
    ResponseEntity<JobResponseDto> findJob(@PathVariable Long id){
        return ResponseEntity.ok(jobService.findJob(id));
    }

    @GetMapping(params = {"categoryName"})
    ResponseEntity<Set<JobResponseDto>> findJobByCategory(@RequestParam String categoryName){
        return ResponseEntity.ok(jobService.findJobByCategoryName(categoryName));
    }

    @GetMapping
    ResponseEntity<Set<JobResponseDto>> findOwnEmployerJobs(Authentication authentication){
        return ResponseEntity.ok(jobService.findOwnEmployerJobs(authentication.getName()));
    }

    @GetMapping("/{id}/me")
    ResponseEntity<JobResponseDto> findOwnEmployerJob(Authentication authentication,@PathVariable Long id){
        return ResponseEntity.ok(jobService.findOwnEmployerJob(authentication.getName(), id));
    }
}
