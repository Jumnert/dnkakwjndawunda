package com.luysot.jobodia.service;

import com.luysot.jobodia.dto.ApplicationDTOs.ApplicationRequestDto;
import com.luysot.jobodia.dto.ApplicationDTOs.ApplicationNotifyRequestDto;
import com.luysot.jobodia.dto.ApplicationDTOs.ApplicationResponseDto;
import com.luysot.jobodia.dto.ApplicationDTOs.UpdateApplicationStatusRequestDto;
import com.luysot.jobodia.exception.DuplicateResourceException;
import com.luysot.jobodia.exception.ResourceNotFoundException;
import com.luysot.jobodia.mapper.ApplicationMapper;
import com.luysot.jobodia.model.*;
import com.luysot.jobodia.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
    private final EmployerProfileRepository employerProfileRepository;
    private final EmailService emailService;

    @Transactional
    public ApplicationResponseDto apply(String email, ApplicationRequestDto dto){
        SeekerProfiles seeker = getSeekerProfiles(email);
        SeekerResumes resume = seekerResumeRepository.findByIdAndSeeker(dto.resumeId(), seeker)
                .orElseThrow(()-> new ResourceNotFoundException("Resume not found"));
        SeekerCoverLetters coverLetter = seekerCoverLetterRepository.findByIdAndSeeker(dto.coverLetterId(),seeker)
                .orElseThrow(()-> new ResourceNotFoundException("Cover letter not found"));
        Jobs job = jobRepository.findById(dto.jobId())
                .orElseThrow(()-> new ResourceNotFoundException("Job not found"));

        if(applicationRepository.existsByJobAndSeeker(job,seeker)){
           throw new DuplicateResourceException("Application already exists");
        }
        Applications application = new Applications();
        application.setSeeker(seeker);
        application.setJob(job);
        application.setResume(resume);
        application.setCoverLetter(coverLetter);

        Applications savedApplication = applicationRepository.save(application);

        return applicationMapper.toDto(savedApplication);
    }

    public Page<ApplicationResponseDto> findSeekerOwnApplications(String email, Pageable pageable){
        SeekerProfiles seeker = getSeekerProfiles(email);

        return applicationRepository.findBySeeker(seeker, pageable)
                .map(applicationMapper::toDto);
    }

    public ApplicationResponseDto findSeekerOwnApplication(Long id, String email){
        SeekerProfiles seeker = getSeekerProfiles(email);

        Applications applications = applicationRepository.findByIdAndSeeker(id,seeker)
                .orElseThrow(()->new ResourceNotFoundException("Application not found"));

        return applicationMapper.toDto(applications);
    }

    @Transactional
    public void deleteSeekerOwnApplication(Long id, String email){
        SeekerProfiles seeker = getSeekerProfiles(email);

        applicationRepository.findByIdAndSeeker(id, seeker)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        applicationRepository.deleteByIdAndSeeker(id,seeker);
    }

    public Page<ApplicationResponseDto> findApplicants(String email, Pageable pageable){
        EmployerProfiles employer = getEmployerProfiles(email);

        return applicationRepository.findByJobEmployerId(employer.getId(), pageable)
                .map(applicationMapper::toDto);
    }

    public ApplicationResponseDto findApplicant(Long applicationId,String email){
        EmployerProfiles employer = getEmployerProfiles(email);

        Applications application = applicationRepository.findByJobEmployerId_AndId(employer.getId(),applicationId)
                .orElseThrow(()->new ResourceNotFoundException("Applicant not found"));

        return applicationMapper.toDto(application);
    }


    public ApplicationResponseDto updateApplicationStatus( Long applicationId, UpdateApplicationStatusRequestDto reqStatus,String email){
        EmployerProfiles employer = getEmployerProfiles(email);

        Applications application = applicationRepository.findByJobEmployerId_AndId(employer.getId(),applicationId)
                .orElseThrow(()->new ResourceNotFoundException("Applicant not found"));

        application.setStatus(reqStatus.status());
        applicationRepository.save(application);

        return applicationMapper.toDto(application);
    }

    @Transactional
    public void notifyApplicant(Long applicationId, ApplicationNotifyRequestDto request, String email) {
        EmployerProfiles employer = getEmployerProfiles(email);
        Applications application = applicationRepository.findByJobEmployerId_AndId(employer.getId(), applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Applicant not found"));

        String seekerEmail = application.getSeeker().getUser().getEmail();
        String body = request.message()
                + "\n\nJob: " + application.getJob().getTitle()
                + "\nCompany: " + application.getJob().getEmployer().getCompanyName();

        emailService.sendApplicationNotification(seekerEmail, request.subject(), body);
    }


    private @NonNull SeekerProfiles getSeekerProfiles(String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));
        return seekerProfileRepository.findByUser(user)
                .orElseThrow(()-> new ResourceNotFoundException("Seeker profile not found"));
    }

    private @NonNull EmployerProfiles getEmployerProfiles(String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));
        return employerProfileRepository.findByUser(user)
                .orElseThrow(()-> new ResourceNotFoundException("Employer profile not found"));
    }
}
