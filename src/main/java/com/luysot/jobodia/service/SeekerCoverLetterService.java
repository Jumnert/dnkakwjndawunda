package com.luysot.jobodia.service;

import com.luysot.jobodia.dto.SeekerProfileDTOs.SeekerCoverLetterResponseDto;
import com.luysot.jobodia.dto.SeekerProfileDTOs.SeekerResumeResponseDto;
import com.luysot.jobodia.model.SeekerCoverLetters;
import com.luysot.jobodia.model.SeekerProfiles;
import com.luysot.jobodia.model.SeekerResumes;
import com.luysot.jobodia.model.Users;
import com.luysot.jobodia.repository.SeekerCoverLettersRepository;
import com.luysot.jobodia.repository.SeekerProfileRepository;
import com.luysot.jobodia.repository.SeekerResumesRepository;
import com.luysot.jobodia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SeekerCoverLetterService {
    private final SeekerCoverLettersRepository seekerCoverLettersRepository;
    private final UserRepository userRepository;
    private final SeekerProfileRepository seekerProfileRepository;
    private static final List<String> ALLOWED_TYPES = List.of(
            "application/pdf"
    );

    public void uploadSeekerCoverLetter(
            String email,
            String title,
            MultipartFile file) throws IOException {

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found!!"));

        SeekerProfiles seekerProfiles = seekerProfileRepository
                .findByUser(user)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found!!"));

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Resume file is required");
        }

        String contentType = file.getContentType();

        if (contentType == null ||
                !ALLOWED_TYPES.contains(contentType)) {

            throw new IllegalArgumentException(
                    "Only PDF files are allowed");
        }

        String uploadDir =
                "uploads/seeker-cover-letter/" + user.getUsername();

        File dir = new File(uploadDir);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        Path uploadPath = Paths.get(uploadDir);

        String storedName =
                UUID.randomUUID()
                        + "_("
                        + user.getUsername()
                        + ")_"
                        + file.getOriginalFilename();

        Path path = uploadPath.resolve(storedName);

        file.transferTo(path);

        SeekerCoverLetters seekerCoverLetter = new SeekerCoverLetters();

        seekerCoverLetter.setTitle(title);
        seekerCoverLetter.setCoverLetterOriginalName(file.getOriginalFilename());
        seekerCoverLetter.setCoverLetterStoredName(storedName);
        seekerCoverLetter.setCoverLetterContentType(contentType);

        seekerCoverLetter.setSeeker(seekerProfiles);

        SeekerCoverLetters saved =
                seekerCoverLettersRepository.save(seekerCoverLetter);

        // FIX: use resume ID instead of user ID
        saved.setCoverLetterUrl(
                "/api/v1/seeker-resume/"
                        + saved.getId()
                        + "/resume"
        );

        seekerCoverLettersRepository.save(saved);
    }

    public List<SeekerCoverLetterResponseDto> findAllSeekerOwnCoverLetter(String email) {
        Users user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found!!"));
        SeekerProfiles seekerProfiles = seekerProfileRepository
                .findByUser(user)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found!!"));

        List<SeekerCoverLetters> coverLetters = seekerCoverLettersRepository.findBySeeker(seekerProfiles);

        return coverLetters.stream().map(coverLetter -> SeekerCoverLetterResponseDto.builder()
                        .id(coverLetter.getId())
                        .title(coverLetter.getTitle())
                        .coverLetterUrl(coverLetter.getCoverLetterUrl())
                        .build())
                .toList();
    }

    public SeekerCoverLetterResponseDto findSeekerOwnCoverLetter(Long id,String email){
        Users user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found!!"));
        SeekerProfiles seekerProfiles = seekerProfileRepository
                .findByUser(user)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found!!"));
        SeekerCoverLetters coverLetter = seekerCoverLettersRepository.findByIdAndSeeker(id,seekerProfiles).orElseThrow(() -> new UsernameNotFoundException("User not found!!"));


        return SeekerCoverLetterResponseDto.builder().id(coverLetter.getId()).title(coverLetter.getTitle()).coverLetterUrl(coverLetter.getCoverLetterUrl()).build();
    }
}
