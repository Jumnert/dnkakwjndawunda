package com.luysot.jobodia.service;

import com.luysot.jobodia.dto.SeekerProfileDTOs.SeekerResumeResponseDto;
import com.luysot.jobodia.model.SeekerProfiles;
import com.luysot.jobodia.model.SeekerResumes;
import com.luysot.jobodia.model.Users;
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
public class SeekerResumeService {
    private final SeekerResumesRepository seekerResumesRepository;
    private final UserRepository userRepository;
    private final SeekerProfileRepository seekerProfileRepository;
    private static final List<String> ALLOWED_TYPES = List.of(
            "application/pdf"
    );

    public void uploadSeekerResume(
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
                "uploads/seeker-resume/" + user.getUsername();

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

        SeekerResumes seekerResume = new SeekerResumes();

        seekerResume.setTitle(title);
        seekerResume.setResumeOriginalName(file.getOriginalFilename());
        seekerResume.setResumeStoredName(storedName);
        seekerResume.setResumeContentType(contentType);

        seekerResume.setSeeker(seekerProfiles);

        SeekerResumes saved =
                seekerResumesRepository.save(seekerResume);

        saved.setResumeUrl(
                "/api/v1/seeker-resume/"
                        + saved.getId()
                        + "/resume"
        );

        seekerResumesRepository.save(saved);
    }

    public List<SeekerResumeResponseDto> findAllSeekerOwnResume(String email) {
        Users user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found!!"));
        SeekerProfiles seekerProfiles = seekerProfileRepository
                .findByUser(user)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found!!"));
        List<SeekerResumes> resumes = seekerResumesRepository.findBySeeker(seekerProfiles);

        return resumes.stream().map(resume -> SeekerResumeResponseDto.builder()
                .id(resume.getId())
                .title(resume.getTitle())
                .resumeUrl(resume.getResumeUrl())
                .build())
                .toList();
    }

    public SeekerResumeResponseDto findSeekerOwnResume(Long id,String email){
        Users user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found!!"));
        SeekerProfiles seekerProfiles = seekerProfileRepository
                .findByUser(user)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found!!"));
        SeekerResumes resume = seekerResumesRepository.findByIdAndSeeker(id,seekerProfiles).orElseThrow(() -> new UsernameNotFoundException("User not found!!"));


        return SeekerResumeResponseDto.builder().id(resume.getId()).title(resume.getTitle()).resumeUrl(resume.getResumeUrl()).build();
    }

}
