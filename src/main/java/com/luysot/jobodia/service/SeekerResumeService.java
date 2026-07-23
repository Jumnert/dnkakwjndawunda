package com.luysot.jobodia.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.luysot.jobodia.dto.SeekerProfileDTOs.SeekerResumeResponseDto;
import com.luysot.jobodia.exception.InvalidRequestException;
import com.luysot.jobodia.exception.ResourceNotFoundException;
import com.luysot.jobodia.model.SeekerProfiles;
import com.luysot.jobodia.model.SeekerResumes;
import com.luysot.jobodia.model.Users;
import com.luysot.jobodia.repository.SeekerProfileRepository;
import com.luysot.jobodia.repository.SeekerResumeRepository;
import com.luysot.jobodia.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeekerResumeService {
    private final SeekerResumeRepository seekerResumeRepository;
    private final UserRepository userRepository;
    private final Cloudinary cloudinary;
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
                        new ResourceNotFoundException("User not found"));

        SeekerProfiles seekerProfiles = seekerProfileRepository
                .findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Seeker profile not found"));

        if (file.isEmpty()) {
            throw new InvalidRequestException("Resume file is required");
        }

        SeekerResumes seekerResume = new SeekerResumes();
        seekerResume.setTitle(title);

        String contentType = validateResumeFile(file);

        Map<?, ?> result = uploadResumeAsset(user, file);
        seekerResume.setResumeOriginalName(file.getOriginalFilename());
        seekerResume.setResumePublicId(result.get("public_id").toString());
        seekerResume.setResumeUrl(result.get("secure_url").toString());
        seekerResume.setResumeContentType(contentType);

        seekerResume.setSeeker(seekerProfiles);

        seekerResumeRepository.save(seekerResume);
    }

    @Transactional
    public SeekerResumeResponseDto updateSeekerOwnResume(
            Long id,
            String email,
            String title,
            MultipartFile file) throws IOException {

        SeekerResumes resume = findSeekerOwnResumeEntity(id, email);
        resume.setTitle(title);

        if (file != null) {
            if (file.isEmpty()) {
                throw new InvalidRequestException("Resume file is required");
            }

            String contentType = validateResumeFile(file);
            String oldPublicId = resume.getResumePublicId();

            Map<?, ?> result = uploadResumeAsset(resume.getSeeker().getUser(), file);

            resume.setResumeOriginalName(file.getOriginalFilename());
            resume.setResumePublicId(result.get("public_id").toString());
            resume.setResumeUrl(result.get("secure_url").toString());
            resume.setResumeContentType(contentType);

            deleteResumeAsset(oldPublicId);
        }

        return toResumeResponse(seekerResumeRepository.save(resume));
    }

    public Page<SeekerResumeResponseDto> findAllSeekerOwnResume(String email, Pageable pageable) {
        Users user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        SeekerProfiles seekerProfiles = seekerProfileRepository
                .findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Seeker profile not found"));

        return seekerResumeRepository.findBySeeker(seekerProfiles, pageable)
                .map(this::toResumeResponse);
    }

    public SeekerResumeResponseDto findSeekerOwnResume(Long id,String email){
        return toResumeResponse(findSeekerOwnResumeEntity(id, email));
    }

    public SeekerResumes findSeekerOwnResumeEntity(Long id, String email) {
        Users user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        SeekerProfiles seekerProfiles = seekerProfileRepository
                .findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Seeker profile not found"));
        return seekerResumeRepository.findByIdAndSeeker(id,seekerProfiles).orElseThrow(() -> new ResourceNotFoundException("Resume not found"));
    }

    @Transactional
    public void deleteSeekerOwnResume(Long id, String email) throws IOException {
        Users user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        SeekerProfiles seekerProfiles = seekerProfileRepository
                .findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Seeker profile not found"));
        SeekerResumes resume = seekerResumeRepository.findByIdAndSeeker(id, seekerProfiles)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));
        deleteResumeAsset(resume.getResumePublicId());
        seekerResumeRepository.deleteByIdAndSeeker(id,seekerProfiles);
    }

    private String loadResumeAssetUrl(SeekerResumes resume) {
        String publicId = resume.getResumePublicId();

        if (publicId == null || publicId.isBlank()) {
            return null;
        }

        return cloudinary.url()
                .secure(true)
                .resourceType("raw")
                .type("authenticated")
                .signed(true)
                .generate(publicId);
    }

    private String validateResumeFile(MultipartFile file) {
        String contentType = file.getContentType();

        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new InvalidRequestException("Only PDF files are allowed");
        }

        return contentType;
    }

    private Map<?, ?> uploadResumeAsset(Users user, MultipartFile file) throws IOException {
        String uploadDir = "seeker-resume/" + user.getUsername();

        return cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", uploadDir,
                        "resource_type", "raw",
                        "type", "authenticated",
                        "use_filename", false,
                        "unique_filename", true
                )
        );
    }

    private void deleteResumeAsset(String publicId) throws IOException {
        if (publicId == null || publicId.isBlank()) {
            return;
        }

        cloudinary.uploader().destroy(
                publicId,
                ObjectUtils.asMap(
                        "resource_type", "raw",
                        "type", "authenticated",
                        "invalidate", true
                )
        );
    }

    private SeekerResumeResponseDto toResumeResponse(SeekerResumes resume) {
        return SeekerResumeResponseDto.builder()
                .id(resume.getId())
                .title(resume.getTitle())
                .resumeUrl(loadResumeAssetUrl(resume))
                .build();
    }
}
