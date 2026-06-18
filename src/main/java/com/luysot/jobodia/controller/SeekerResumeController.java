package com.luysot.jobodia.controller;

import com.luysot.jobodia.service.SeekerResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
}
