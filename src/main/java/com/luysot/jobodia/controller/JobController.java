package com.luysot.jobodia.controller;

import com.luysot.jobodia.dto.JobDTOs.JobRequestDto;
import com.luysot.jobodia.dto.JobDTOs.JobResponseDto;
import com.luysot.jobodia.dto.JobDTOs.UpdateJobStatusRequestDto;
import com.luysot.jobodia.model.enums.JobLevel;
import com.luysot.jobodia.model.enums.JobSite;
import com.luysot.jobodia.model.enums.JobTime;
import com.luysot.jobodia.service.JobService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
@RequestMapping("/api/v1/jobs")
public class JobController {
    private final JobService jobService;

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    ResponseEntity<JobResponseDto> addJob(@Valid @RequestBody JobRequestDto request, Authentication authentication){
        return ResponseEntity.status(HttpStatus.CREATED).body(jobService.addJob(authentication.getName(),request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    ResponseEntity<JobResponseDto> updateJob(@PathVariable @Positive(message = "Job id must be positive") Long id,@Valid @RequestBody JobRequestDto request, Authentication authentication){
        return ResponseEntity.ok(jobService.updateJob(id,request,authentication.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    ResponseEntity<?> deleteJob(@PathVariable @Positive(message = "Job id must be positive") Long id, Authentication authentication){
        jobService.deleteJob(id,authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    ResponseEntity<Page<JobResponseDto>> findJobs(
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable){
        return ResponseEntity.ok(jobService.findJobs(pageable));
    }

    @GetMapping("/search")
    ResponseEntity<Page<JobResponseDto>> search(
            @RequestParam(required = false) @Size(max = 255, message = "Title cannot exceed 255 characters") String title,
            @RequestParam(required = false) @Size(max = 255, message = "Industry cannot exceed 255 characters") String industry,
            @RequestParam(required = false) @Size(max = 255, message = "Company cannot exceed 255 characters") String company,
            @RequestParam(required = false) @Size(max = 255, message = "Category cannot exceed 255 characters") String category,
            @RequestParam(required = false)JobTime jobTime,
            @RequestParam(required = false) JobLevel jobLevel,
            @RequestParam(required = false) JobSite jobSite,
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
            ){
        return ResponseEntity.ok(jobService.searchJob(
                title,industry,company, category,jobTime, jobLevel,jobSite, pageable
        ));
    }

    @GetMapping("/newly-added")
    ResponseEntity<Page<JobResponseDto>> findNewlyAddedJobs(
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.ok(jobService.findNewlyAddedJob(pageable));
    }

    @GetMapping("/{id}")
    ResponseEntity<JobResponseDto> findJob(@PathVariable @Positive(message = "Job id must be positive") Long id){
        return ResponseEntity.ok(jobService.findJob(id));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('EMPLOYER')")
    ResponseEntity<JobResponseDto> updateJobStatus(
            @PathVariable @Positive(message = "Job id must be positive") Long id,
            @Valid @RequestBody UpdateJobStatusRequestDto request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(jobService.updateJobStatus(id, request, authentication.getName()));
    }

//    @PostMapping("/{id}/publish")
//    @PreAuthorize("hasRole('EMPLOYER')")
//    ResponseEntity<JobResponseDto> publishJob(
//            @PathVariable @Positive(message = "Job id must be positive") Long id,
//            Authentication authentication
//    ) {
//        return ResponseEntity.ok(jobService.publishJob(id, authentication.getName()));
//    }
//
//    @PostMapping("/{id}/archive")
//    @PreAuthorize("hasRole('EMPLOYER')")
//    ResponseEntity<JobResponseDto> archiveJob(
//            @PathVariable @Positive(message = "Job id must be positive") Long id,
//            Authentication authentication
//    ) {
//        return ResponseEntity.ok(jobService.archiveJob(id, authentication.getName()));
//    }


    @GetMapping("/me")
    @PreAuthorize("hasRole('EMPLOYER')")
    ResponseEntity<Page<JobResponseDto>> findOwnEmployerJobs(
            Authentication authentication,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.ok(jobService.findOwnEmployerJobs(authentication.getName(), pageable));
    }

    @GetMapping({"/me/{id}", "/{id}/me"})
    @PreAuthorize("hasRole('EMPLOYER')")
    ResponseEntity<JobResponseDto> findOwnEmployerJob(Authentication authentication,@PathVariable @Positive(message = "Job id must be positive") Long id){
        return ResponseEntity.ok(jobService.findOwnEmployerJob(authentication.getName(), id));
    }
}
