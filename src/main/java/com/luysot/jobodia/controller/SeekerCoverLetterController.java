package com.luysot.jobodia.controller;

import com.luysot.jobodia.dto.SeekerProfileDTOs.SeekerCoverLetterResponseDto;
import com.luysot.jobodia.service.SeekerCoverLetterService;
import jakarta.validation.constraints.NotBlank;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/seeker-cover-letters")
public class SeekerCoverLetterController {
    private final SeekerCoverLetterService seekerCoverLetterService;

    @PostMapping
    @PreAuthorize("hasRole('SEEKER')")
    ResponseEntity<?> uploadSeekerCoverLetter(
            @RequestParam @NotBlank(message = "Title is required")
            @Size(max = 255, message = "Title cannot exceed 255 characters")
            String title,
            @RequestParam MultipartFile file,
            Authentication authentication
    ) throws IOException {
        seekerCoverLetterService.uploadSeekerCoverLetter(
                authentication.getName(),
                title,
                file
        );
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('SEEKER')")
    ResponseEntity<Page<SeekerCoverLetterResponseDto>> findAllSeekerOwnCoverLetter(
            Authentication authentication,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.ok(seekerCoverLetterService.findAllSeekerOwnCoverLetter(authentication.getName(), pageable));
    }

    @GetMapping("/me/{id}")
    @PreAuthorize("hasRole('SEEKER')")
    ResponseEntity<SeekerCoverLetterResponseDto> findSeekerOwnCoverLetter(@PathVariable @Positive(message = "Cover letter id must be positive") Long id, Authentication authentication){
        return ResponseEntity.ok(seekerCoverLetterService.findSeekerOwnCoverLetter(id,authentication.getName()));
    }

    @PutMapping("/me/{id}")
    @PreAuthorize("hasRole('SEEKER')")
    ResponseEntity<SeekerCoverLetterResponseDto> updateSeekerOwnCoverLetter(
            @PathVariable @Positive(message = "Cover letter id must be positive") Long id,
            @RequestParam @NotBlank(message = "Title is required")
            @Size(max = 255, message = "Title cannot exceed 255 characters")
            String title,
            @RequestParam(required = false) MultipartFile file,
            Authentication authentication
    ) throws IOException {
        return ResponseEntity.ok(seekerCoverLetterService.updateSeekerOwnCoverLetter(
                id,
                authentication.getName(),
                title,
                file
        ));
    }

    @DeleteMapping("/me/{id}")
    @PreAuthorize("hasRole('SEEKER')")
    ResponseEntity<?> deleteSeekerOwnCoverLetter(@PathVariable @Positive(message = "Cover letter id must be positive") Long id, Authentication authentication) throws IOException {
        seekerCoverLetterService.deleteSeekerOwnCoverLetter(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
