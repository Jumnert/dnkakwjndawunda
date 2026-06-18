package com.luysot.jobodia.service;

import com.luysot.jobodia.dto.SeekerProfileDTOs.SeekerProfileResponseDto;
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

        // FIX: link to seeker
        seekerResume.setSeeker(seekerProfiles);

        // save first to generate ID
        SeekerResumes saved =
                seekerResumesRepository.save(seekerResume);

        // FIX: use resume ID instead of user ID
        saved.setResumeUrl(
                "/api/v1/seeker-resume/"
                        + saved.getId()
                        + "/resume"
        );

        seekerResumesRepository.save(saved);
    }

}
