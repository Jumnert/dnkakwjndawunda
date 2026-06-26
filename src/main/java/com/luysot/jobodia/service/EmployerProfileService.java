package com.luysot.jobodia.service;

import com.luysot.jobodia.dto.EmployerProfileDTOs.EmployerProfileRequestDto;
import com.luysot.jobodia.dto.EmployerProfileDTOs.EmployerProfileResponseDto;
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
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployerProfileService {
    private final EmployerProfileRepository employerProfileRepository;
    private final EmployerProfileMapper employerProfileMapper;
    private final UserRepository userRepository;
    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/svg+xml",
            "image/avif",
            "image/webp"
    );

    @Transactional
    public EmployerProfileResponseDto createProfile(EmployerProfileRequestDto request, MultipartFile file , String email) throws IOException{
        Users user = userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User by the following email is not found!"));

        if(employerProfileRepository.findByUser(user).isPresent()){
            throw new RuntimeException("Profile already exists!");
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
                throw new IllegalArgumentException("Only image files are allowed.");
            }

            String uploadDir = "uploads/employer-profiles/" + user.getUsername();
            String originalName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            File dir = new File(uploadDir);

            if(!dir.exists()) dir.mkdirs();

            Path uploadPath = Paths.get(uploadDir);
            String storedName = UUID.randomUUID() + "_" + "(" + user.getUsername() + ")" +originalName;
            Path path = uploadPath.resolve(storedName);

            file.transferTo(path);

            employerProfile.setCompanyLogoContentType(contentType);
            employerProfile.setCompanyLogoOriginalName(originalName);
            employerProfile.setCompanyLogoStoredName(storedName);
            employerProfile.setCompanyLogoUrl("/api/v1/employer-profiles/" + user.getId() + "/company-logo");
        }

        EmployerProfiles savedProfile = employerProfileRepository.save(employerProfile);
        return employerProfileMapper.toDto(savedProfile);
    }

    public Resource viewCompnayLogo(String email) throws FileNotFoundException, MalformedURLException {
        Users user = userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User by the following email is not found!"));
        EmployerProfiles employerProfiles = employerProfileRepository.findByUser(user).orElseThrow(()->new RuntimeException("User by the following email is not found!"));

        String storedName = employerProfiles.getCompanyLogoStoredName();


        if (storedName == null || storedName.isBlank()) {
            throw new FileNotFoundException("Profile picture not found");
        }

        Path path = Paths.get("uploads")
                .resolve("employer-profile")
                .resolve(user.getUsername())
                .resolve(storedName);

        if (!Files.exists(path)) {
            throw new FileNotFoundException("Profile picture file not found");
        }

        return new UrlResource(path.toUri());
    }
}
