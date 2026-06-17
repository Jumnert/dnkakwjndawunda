package com.luysot.jobodia.service;

import com.luysot.jobodia.dto.SeekerProfileDTOs.SeekerProfileRequestDto;
import com.luysot.jobodia.dto.SeekerProfileDTOs.SeekerProfileResponseDto;
import com.luysot.jobodia.dto.SeekerProfileDTOs.SeekerSkillsRequestDto;
import com.luysot.jobodia.dto.SeekerProfileDTOs.SeekerSkillsResponseDto;
import com.luysot.jobodia.mapper.SeekerProfileMapper;
import com.luysot.jobodia.mapper.SkillMapper;
import com.luysot.jobodia.model.SeekerProfile;
import com.luysot.jobodia.model.Skills;
import com.luysot.jobodia.model.Users;
import com.luysot.jobodia.repository.SeekerProfileRepository;
import com.luysot.jobodia.repository.SkillsRepository;
import com.luysot.jobodia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeekerProfileService {
    private final SeekerProfileRepository seekerProfileRepository;
    private final SeekerProfileMapper seekerProfileMapper;
    private final UserRepository userRepository;
    private final SkillsRepository skillsRepository;
    private final SkillMapper skillMapper;

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/svg+xml",
            "image/avif",
            "image/webp"
    );

    public SeekerProfileResponseDto myProfile(String email){
        Users user = userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User with the following email is not found!"));

        SeekerProfile seekerProfile = seekerProfileRepository.findByUser(user).orElseThrow(()->new RuntimeException("User with the following email is not found!"));

        SeekerProfileResponseDto profile = seekerProfileMapper.toDto(seekerProfile);

        return profile;
    }

    public SeekerProfileResponseDto createProfile(SeekerProfileRequestDto request, String email){
        Users user = userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User by the following email is not found!"));

        if(seekerProfileRepository.findByUser(user).isPresent()){
            throw new RuntimeException("Profile already exists!");
        }

        SeekerProfile seekerProfile = new SeekerProfile();

        seekerProfile.setPhoneNumber(request.phoneNumber());
        seekerProfile.setGender(request.gender());
        seekerProfile.setUser(user);

        SeekerProfile savedProfile = seekerProfileRepository.save(seekerProfile);

        return seekerProfileMapper.toDto(savedProfile);
    }

    public void uploadProfilePicture(String email, MultipartFile file) throws IOException {
        Users user = userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User by the following email is not found!"));

        SeekerProfile profile = seekerProfileRepository.findByUser(user).orElseThrow(()->new RuntimeException("User is not found"));

        String contentType = file.getContentType();

        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            return;
        }

        String uploadDir = "uploads/seekerProfile";
        File dir = new File(uploadDir);

        if(!dir.exists()) dir.mkdirs();

        Path uploadPath = Paths.get(uploadDir);
        String storedName = UUID.randomUUID() + "_" + "(" + user.getUsername() + ")" +file.getOriginalFilename();
        Path path = uploadPath.resolve(storedName);

        file.transferTo(path);

        profile.setProfilePictureContentType(contentType);
        profile.setProfilePictureOriginalName(file.getOriginalFilename());
        profile.setProfilePictureStoredName(storedName);
        profile.setProfilePictureUrl("/api/v1/seeker-profile/" + user.getId() + "/profile-picture");

        seekerProfileRepository.save(profile);
    }

    public SeekerSkillsResponseDto addSeekerSkills(String email, SeekerSkillsRequestDto dto) {
        Users user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Email not found!!"));

        Set<Skills> newSkills = new HashSet<>(skillsRepository.findAllById(dto.skillId()));

        SeekerProfile seeker = seekerProfileRepository.findByUser(user).orElseThrow(() -> new UsernameNotFoundException("Email not found!!"));

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
