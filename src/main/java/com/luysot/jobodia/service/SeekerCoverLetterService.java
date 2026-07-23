package com.luysot.jobodia.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.luysot.jobodia.dto.SeekerProfileDTOs.SeekerCoverLetterResponseDto;
import com.luysot.jobodia.exception.InvalidRequestException;
import com.luysot.jobodia.exception.ResourceNotFoundException;
import com.luysot.jobodia.model.SeekerCoverLetters;
import com.luysot.jobodia.model.SeekerProfiles;
import com.luysot.jobodia.model.Users;
import com.luysot.jobodia.repository.SeekerCoverLetterRepository;
import com.luysot.jobodia.repository.SeekerProfileRepository;
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
public class SeekerCoverLetterService {
    private final SeekerCoverLetterRepository seekerCoverLetterRepository;
    private final UserRepository userRepository;
    private final Cloudinary cloudinary;
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
                        new ResourceNotFoundException("User not found"));

        SeekerProfiles seekerProfiles = seekerProfileRepository
                .findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Seeker profile not found"));

        if (file.isEmpty()) {
            throw new InvalidRequestException("Cover letter file is required");
        }

        SeekerCoverLetters seekerCoverLetter = new SeekerCoverLetters();
        seekerCoverLetter.setTitle(title);

        String contentType = validateCoverLetterFile(file);

        Map<?, ?> result = uploadCoverLetterAsset(user, file);
        seekerCoverLetter.setCoverLetterOriginalName(file.getOriginalFilename());
        seekerCoverLetter.setCoverLetterPublicId(result.get("public_id").toString());
        seekerCoverLetter.setCoverLetterUrl(result.get("secure_url").toString());
        seekerCoverLetter.setCoverLetterContentType(contentType);

        seekerCoverLetter.setSeeker(seekerProfiles);

        seekerCoverLetterRepository.save(seekerCoverLetter);
    }

    @Transactional
    public SeekerCoverLetterResponseDto updateSeekerOwnCoverLetter(
            Long id,
            String email,
            String title,
            MultipartFile file) throws IOException {

        SeekerCoverLetters coverLetter = findSeekerOwnCoverLetterEntity(id, email);
        coverLetter.setTitle(title);

        if (file != null) {
            if (file.isEmpty()) {
                throw new InvalidRequestException("Cover letter file is required");
            }

            String contentType = validateCoverLetterFile(file);
            String oldPublicId = coverLetter.getCoverLetterPublicId();

            Map<?, ?> result = uploadCoverLetterAsset(coverLetter.getSeeker().getUser(), file);

            coverLetter.setCoverLetterOriginalName(file.getOriginalFilename());
            coverLetter.setCoverLetterPublicId(result.get("public_id").toString());
            coverLetter.setCoverLetterUrl(result.get("secure_url").toString());
            coverLetter.setCoverLetterContentType(contentType);

            deleteCoverLetterAsset(oldPublicId);
        }

        return toCoverLetterResponse(seekerCoverLetterRepository.save(coverLetter));
    }

    public Page<SeekerCoverLetterResponseDto> findAllSeekerOwnCoverLetter(String email, Pageable pageable) {
        Users user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        SeekerProfiles seekerProfiles = seekerProfileRepository
                .findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Seeker profile not found"));

        return seekerCoverLetterRepository.findBySeeker(seekerProfiles, pageable)
                .map(this::toCoverLetterResponse);
    }

    public SeekerCoverLetterResponseDto findSeekerOwnCoverLetter(Long id,String email){
        return toCoverLetterResponse(findSeekerOwnCoverLetterEntity(id, email));
    }

    public SeekerCoverLetters findSeekerOwnCoverLetterEntity(Long id, String email) {
        Users user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        SeekerProfiles seekerProfiles = seekerProfileRepository
                .findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Seeker profile not found"));
        return seekerCoverLetterRepository.findByIdAndSeeker(id,seekerProfiles).orElseThrow(() -> new ResourceNotFoundException("Cover letter not found"));
    }

    @Transactional
    public void deleteSeekerOwnCoverLetter(Long id, String email) throws IOException {
        Users user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        SeekerProfiles seekerProfiles = seekerProfileRepository
                .findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Seeker profile not found"));
        SeekerCoverLetters coverLetter = seekerCoverLetterRepository.findByIdAndSeeker(id, seekerProfiles)
                .orElseThrow(() -> new ResourceNotFoundException("Cover letter not found"));
        deleteCoverLetterAsset(coverLetter.getCoverLetterPublicId());
        seekerCoverLetterRepository.deleteByIdAndSeeker(id,seekerProfiles);
    }

    private String loadCoverLetterAssetUrl(SeekerCoverLetters coverLetter) {
        String publicId = coverLetter.getCoverLetterPublicId();

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

    private String validateCoverLetterFile(MultipartFile file) {
        String contentType = file.getContentType();

        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new InvalidRequestException("Only PDF files are allowed");
        }

        return contentType;
    }

    private Map<?, ?> uploadCoverLetterAsset(Users user, MultipartFile file) throws IOException {
        String uploadDir = "seeker-cover-letter/" + user.getUsername();

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

    private void deleteCoverLetterAsset(String publicId) throws IOException {
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

    private SeekerCoverLetterResponseDto toCoverLetterResponse(SeekerCoverLetters coverLetter) {
        return SeekerCoverLetterResponseDto.builder()
                .id(coverLetter.getId())
                .title(coverLetter.getTitle())
                .coverLetterUrl(loadCoverLetterAssetUrl(coverLetter))
                .build();
    }
}
