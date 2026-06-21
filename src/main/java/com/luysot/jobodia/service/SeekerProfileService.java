package com.luysot.jobodia.service;

import com.luysot.jobodia.dto.SeekerProfileDTOs.SeekerProfileRequestDto;
import com.luysot.jobodia.dto.SeekerProfileDTOs.SeekerProfileResponseDto;
import com.luysot.jobodia.dto.SeekerProfileDTOs.SeekerSkillsRequestDto;
import com.luysot.jobodia.dto.SeekerProfileDTOs.SeekerSkillsResponseDto;
import com.luysot.jobodia.mapper.SeekerProfileMapper;
import com.luysot.jobodia.mapper.SkillMapper;
import com.luysot.jobodia.model.SeekerProfiles;
import com.luysot.jobodia.model.Skills;
import com.luysot.jobodia.model.Users;
import com.luysot.jobodia.repository.SeekerProfileRepository;
import com.luysot.jobodia.repository.SkillRepository;
import com.luysot.jobodia.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeekerProfileService {
    private final SeekerProfileRepository seekerProfileRepository;
    private final SeekerProfileMapper seekerProfileMapper;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/svg+xml",
            "image/avif",
            "image/webp"
    );

    public SeekerSkillsResponseDto myProfile(String email){
        Users user = userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User with the following email is not found!"));

        SeekerProfiles seeker = seekerProfileRepository.findByUser(user).orElseThrow(()->new RuntimeException("User with the following email is not found!"));


        return SeekerSkillsResponseDto.builder()
                .id(seeker.getId())
                .username(seeker.getUser().getUsername())
                .email(seeker.getUser().getEmail())
                .phoneNumber(seeker.getPhoneNumber())
                .profilePictureUrl(seeker.getProfilePictureUrl())
                .gender(seeker.getGender())
                .address(seeker.getAddress())
                .userId(seeker.getUser().getUserId())
                .skills(seeker.getSkills().stream()
                        .map(skillMapper::toDto)
                        .collect(Collectors.toSet()))
                .build();
    }

    @Transactional
    public SeekerProfileResponseDto createProfile(SeekerProfileRequestDto request, MultipartFile file , String email) throws IOException{
        Users user = userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User by the following email is not found!"));

        if(seekerProfileRepository.findByUser(user).isPresent()){
            throw new RuntimeException("Profile already exists!");
        }

        SeekerProfiles seekerProfile = new SeekerProfiles();

        seekerProfile.setPhoneNumber(request.phoneNumber());
        seekerProfile.setGender(request.gender());
        seekerProfile.setAddress(request.address());
        seekerProfile.setUser(user);

        if(!file.isEmpty() && file != null){
            String contentType = file.getContentType();

            if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
                throw new IllegalArgumentException("Only image files are allowed.");
            }

            String uploadDir = "uploads/seeker-profile/" + user.getUsername();
            String originalName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            File dir = new File(uploadDir);

            if(!dir.exists()) dir.mkdirs();

            Path uploadPath = Paths.get(uploadDir);
            String storedName = UUID.randomUUID() + "_" + "(" + user.getUsername() + ")" +originalName;
            Path path = uploadPath.resolve(storedName);

            file.transferTo(path);

            seekerProfile.setProfilePictureContentType(contentType);
            seekerProfile.setProfilePictureOriginalName(originalName);
            seekerProfile.setProfilePictureStoredName(storedName);
            seekerProfile.setProfilePictureUrl("/api/v1/seeker-profile/" + user.getId() + "/profile-picture");
        }

        SeekerProfiles savedProfile = seekerProfileRepository.save(seekerProfile);
        return seekerProfileMapper.toDto(savedProfile);
    }

    public Resource viewSeekerProfilePicture(String email) throws FileNotFoundException, MalformedURLException {
        Users user = userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User by the following email is not found!"));
        SeekerProfiles seekerProfiles = seekerProfileRepository.findByUser(user).orElseThrow(()->new RuntimeException("User by the following email is not found!"));

        String storedName = seekerProfiles.getProfilePictureStoredName();


        if (storedName == null || storedName.isBlank()) {
            throw new FileNotFoundException("Profile picture not found");
        }

        Path path = Paths.get("uploads")
                .resolve("seeker-profile")
                .resolve(user.getUsername())
                .resolve(storedName);

        if (!Files.exists(path)) {
            throw new FileNotFoundException("Profile picture file not found");
        }

        return new UrlResource(path.toUri());
    }

    public SeekerSkillsResponseDto addSeekerSkills(String email, SeekerSkillsRequestDto dto) {
        Users user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Email not found!!"));

        Set<Skills> newSkills = new HashSet<>(skillRepository.findAllById(dto.skillId()));

        SeekerProfiles seeker = seekerProfileRepository.findByUser(user).orElseThrow(() -> new UsernameNotFoundException("Email not found!!"));

        seeker.getSkills().addAll(newSkills);

        seekerProfileRepository.save(seeker);

        return SeekerSkillsResponseDto.builder()
                .id(seeker.getId())
                .username(seeker.getUser().getUsername())
                .email(seeker.getUser().getEmail())
                .phoneNumber(seeker.getPhoneNumber())
                .profilePictureUrl(seeker.getProfilePictureUrl())
                .gender(seeker.getGender())
                .address(seeker.getAddress())
                .userId(seeker.getUser().getUserId())
                .skills(seeker.getSkills().stream()
                        .map(skillMapper::toDto)
                        .collect(Collectors.toSet()))
                .build();
    }
}
