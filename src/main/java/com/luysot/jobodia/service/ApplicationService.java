package com.luysot.jobodia.service;

import com.luysot.jobodia.dto.ApplicationDTOs.ApplicationRequestDto;
import com.luysot.jobodia.dto.ApplicationDTOs.ApplicationResponseDto;
import com.luysot.jobodia.mapper.ApplicationMapper;
import com.luysot.jobodia.model.*;
import com.luysot.jobodia.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final SeekerProfileRepository seekerProfileRepository;
    private final JobRepository jobRepository;
    private final SeekerResumeRepository seekerResumeRepository;
    private final SeekerCoverLetterRepository seekerCoverLetterRepository;
    private final ApplicationMapper applicationMapper;

    @Transactional
    public ApplicationResponseDto apply(String email, ApplicationRequestDto dto){
        Users user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found."));
        SeekerProfiles seeker = seekerProfileRepository.findByUser(user)
                .orElseThrow(()-> new UsernameNotFoundException("User not found."));
        SeekerResumes resume = seekerResumeRepository.findByIdAndSeeker(dto.resumeId(), seeker)
                .orElseThrow(()-> new RuntimeException("Resume not found"));
        SeekerCoverLetters coverLetter = seekerCoverLetterRepository.findByIdAndSeeker(dto.coverLetterId(),seeker)
                .orElseThrow(()-> new RuntimeException("Cover letter not found"));
        Jobs job = jobRepository.findById(dto.jobId())
                .orElseThrow(()-> new RuntimeException("Job not found"));

        Applications application = new Applications();
        application.setSeeker(seeker);
        application.setJob(job);
        application.setResume(resume);
        application.setCoverLetter(coverLetter);

        Applications savedApplication = applicationRepository.save(application);

        return applicationMapper.toDto(savedApplication);
    }
}