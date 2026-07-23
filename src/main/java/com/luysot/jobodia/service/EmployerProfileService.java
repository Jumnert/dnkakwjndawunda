package com.luysot.jobodia.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.luysot.jobodia.dto.EmployerProfileDTOs.EmployerProfileRequestDto;
import com.luysot.jobodia.dto.EmployerProfileDTOs.EmployerProfileResponseDto;
import com.luysot.jobodia.exception.DuplicateResourceException;
import com.luysot.jobodia.exception.InvalidRequestException;
import com.luysot.jobodia.exception.ResourceNotFoundException;
import com.luysot.jobodia.mapper.EmployerProfileMapper;
import com.luysot.jobodia.model.EmployerProfiles;
import com.luysot.jobodia.model.Users;
import com.luysot.jobodia.repository.EmployerProfileRepository;
import com.luysot.jobodia.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployerProfileService {
    private final EmployerProfileRepository employerProfileRepository;
    private final EmployerProfileMapper employerProfileMapper;
    private final UserRepository userRepository;
    private final Cloudinary cloudinary;
    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/svg+xml",
            "image/avif",
            "image/webp"
    );

    @Transactional
    public EmployerProfileResponseDto createProfile(EmployerProfileRequestDto request, MultipartFile file , String email) throws IOException{
        Users user = userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User not found"));

        if(employerProfileRepository.findByUser(user).isPresent()){
            throw new DuplicateResourceException("Profile already exists");
        }

        EmployerProfiles employerProfile = new EmployerProfiles();

        employerProfile.setCompanyName(request.companyName());
        employerProfile.setPhoneNumber(request.phoneNumber());
        employerProfile.setLocation(request.location());
        employerProfile.setDescription(request.description());
        employerProfile.setUser(user);

        if(file != null && !file.isEmpty()){
            String contentType = file.getContentType();

            if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
                throw new InvalidRequestException("Only image files are allowed.");
            }

            try{
                String uploadDir = "employer-profiles/" + user.getUsername() + "/profiles";
                Map<?,?> result = cloudinary.uploader().upload(
                        file.getBytes(),
                        ObjectUtils.asMap("folder", uploadDir)
                );
                employerProfile.setCompanyLogoOriginalName(file.getOriginalFilename());
                employerProfile.setCompanyLogoPublicId(result.get("public_id").toString());
                employerProfile.setCompanyLogoUrl(result.get("secure_url").toString());
                employerProfile.setCompanyLogoContentType(contentType);
            }
            catch (Exception e){
                throw new RuntimeException(e);
            }
        }

        EmployerProfiles savedProfile = employerProfileRepository.save(employerProfile);

        employerProfileRepository.save(savedProfile);
        return employerProfileMapper.toDto(savedProfile);
    }

    public EmployerProfiles findOwnProfileEntity(String email) {
        Users user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return employerProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Employer profile not found"));
    }

    public EmployerProfileResponseDto findOwnProfile(String email) {
        EmployerProfiles profile = findOwnProfileEntity(email);
        if (profile.getCompanyLogoPublicId() != null && !"/api/v1/employer-profiles/me/logo".equals(profile.getCompanyLogoUrl())) {
            profile.setCompanyLogoUrl("/api/v1/employer-profiles/me/logo");
            profile = employerProfileRepository.save(profile);
        }
        return employerProfileMapper.toDto(profile);
    }

//    @Transactional
//    public EmployerProfileResponseDto updateOwnProfile(EmployerProfileRequestDto request, MultipartFile file, String email) throws IOException {
//        Users user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
//        EmployerProfiles employerProfile = employerProfileRepository.findByUser(user)
//                .orElseThrow(() -> new ResourceNotFoundException("Employer profile not found"));
//
//        employerProfile.setCompanyName(request.companyName());
//        employerProfile.setPhoneNumber(request.phoneNumber());
//        employerProfile.setLocation(request.location());
//        employerProfile.setDescription(request.description());
//
//        if (file != null && !file.isEmpty()) {
//            String contentType = file.getContentType();
//            if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
//                throw new InvalidRequestException("Only image files are allowed.");
//            }
//
//            String uploadDir = "uploads/employer-profiles/" + user.getUsername();
//            String originalName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
//            File dir = new File(uploadDir);
//            if (!dir.exists()) {
//                dir.mkdirs();
//            }
//
//            Path uploadPath = Paths.get(uploadDir);
//            String storedName = UUID.randomUUID() + "_(" + user.getUsername() + ")_" + originalName;
//            Path path = uploadPath.resolve(storedName);
//            file.transferTo(path);
//
//            employerProfile.setCompanyLogoContentType(contentType);
//            employerProfile.setCompanyLogoOriginalName(originalName);
//            employerProfile.setCompanyLogoStoredName(storedName);
//        }
//
//        EmployerProfiles savedProfile = employerProfileRepository.save(employerProfile);
//        if (savedProfile.getCompanyLogoPublicId() != null) {
//            savedProfile.setCompanyLogoUrl("/api/v1/employer-profiles/me/logo");
//            savedProfile = employerProfileRepository.save(savedProfile);
//        }
//
//        return employerProfileMapper.toDto(savedProfile);
//    }

    public Resource loadCompanyLogo(Long id) throws FileNotFoundException, MalformedURLException {
        EmployerProfiles employerProfiles = employerProfileRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Employer profile not found"));

        String storedName = employerProfiles.getCompanyLogoPublicId();


        if (storedName == null || storedName.isBlank()) {
            throw new FileNotFoundException("Profile picture not found");
        }

        Path path = Paths.get("uploads")
                .resolve("employer-profiles")
                .resolve(employerProfiles.getUser().getUsername())
                .resolve(storedName);

        if (!Files.exists(path)) {
            throw new FileNotFoundException("Profile picture file not found");
        }

        return new UrlResource(path.toUri());
    }

    public Resource loadOwnCompanyLogo(String email) throws FileNotFoundException, MalformedURLException {
        EmployerProfiles employerProfiles = findOwnProfileEntity(email);
        if (employerProfiles.getCompanyLogoPublicId() == null || employerProfiles.getCompanyLogoPublicId().isBlank()) {
            throw new FileNotFoundException("Company logo not found");
        }

        Path path = Paths.get("uploads")
                .resolve("employer-profiles")
                .resolve(employerProfiles.getUser().getUsername())
                .resolve(employerProfiles.getCompanyLogoPublicId());

        if (!Files.exists(path)) {
            throw new FileNotFoundException("Company logo file not found");
        }

        return new UrlResource(path.toUri());
    }
}
