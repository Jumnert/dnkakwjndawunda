package com.luysot.jobodia.controller;

import com.luysot.jobodia.dto.SeekerProfileDTOs.SeekerResumeResponseDto;
import com.luysot.jobodia.service.SeekerResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/seeker-resume")
public class SeekerResumeController {
    private final SeekerResumeService seekerResumeService;

    @PostMapping
    ResponseEntity<?> uploadSeekerResume(
            @RequestParam String title,
            @RequestParam MultipartFile file,
            Authentication authentication
            ) throws IOException {
        seekerResumeService.uploadSeekerResume(
                authentication.getName(),
                title,
                file
        );
        return ResponseEntity.ok("Resume uploaded!!");
    }

    @GetMapping("/me")
    ResponseEntity<List<SeekerResumeResponseDto>> findAllSeekerOwnResume(Authentication authentication){
        return ResponseEntity.ok(seekerResumeService.findAllSeekerOwnResume(authentication.getName()));
    }

    @GetMapping("/me/{id}")
    ResponseEntity<SeekerResumeResponseDto> findSeekerOwnResume(@PathVariable Long id, Authentication authentication){
        return ResponseEntity.ok(seekerResumeService.findSeekerOwnResume(id,authentication.getName()));
    }
}
