package com.luysot.jobodia.controller;

import com.luysot.jobodia.dto.SeekerProfileDTOs.SeekerCoverLetterResponseDto;
import com.luysot.jobodia.model.SeekerCoverLetters;
import com.luysot.jobodia.service.SeekerCoverLetterService;
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
@RequestMapping("/api/v1/seeker-cover-letter")
public class SeekerCoverLetterController {
    private final SeekerCoverLetterService seekerCoverLetterService;

    @PostMapping
    ResponseEntity<?> uploadSeekerCoverLetter(
            @RequestParam String title,
            @RequestParam MultipartFile file,
            Authentication authentication
            ) throws IOException {
        seekerCoverLetterService.uploadSeekerCoverLetter(
                authentication.getName(),
                title,
                file
        );
        return ResponseEntity.ok("Cover letter uploaded!!");
    }

    @GetMapping("/me")
    ResponseEntity<List<SeekerCoverLetterResponseDto>> findAllSeekerOwnCoverLetter(Authentication authentication){
        return ResponseEntity.ok(seekerCoverLetterService.findAllSeekerOwnCoverLetter(authentication.getName()));
    }
}
