package com.luysot.jobodia.service;

import com.luysot.jobodia.dto.JobDTOs.JobRequestDto;
import com.luysot.jobodia.dto.JobDTOs.JobResponseDto;
import com.luysot.jobodia.mapper.JobMapper;
import com.luysot.jobodia.model.*;
import com.luysot.jobodia.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final EmployerProfileRepository employerProfileRepository;
    private final CategoriesRepository categoriesRepository;
    private final SkillRepository skillRepository;
    private final IndustryRepository industryRepository;
    private final JobMapper jobMapper;

    public JobResponseDto addJob(String email, JobRequestDto request){
        Users user = userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User not found!!"));
        EmployerProfiles employer = employerProfileRepository.findByUser(user).orElseThrow(()->new UsernameNotFoundException("User not found!!"));

        Set<Categories> categories = new HashSet<>(categoriesRepository.findAllById(request.categoriesId()));
        Set<Skills> skills = new HashSet<>(skillRepository.findAllById(request.skillsId()));

        Industries industry = industryRepository.findById(request.industriesId())
                .orElseThrow(() -> new RuntimeException("Industry not found"));

        Jobs job = new Jobs();
        job.setTitle(request.title());
        job.setMinSalary(request.minSalary());
        job.setMaxSalary(request.maxSalary());
        job.setResponsibilities(request.responsibilities());
        job.setRequirements(request.requirements());
        job.setDescription(request.description());
        job.setSummary(request.summary());
        job.setBenefits(request.benefits());
        job.setJobType(request.jobType());
        job.setJobLevel(request.jobLevel());
        job.setJobGender(request.jobGender());
        job.setJobSite(request.jobSite());
        job.setYearsOfExperience(request.yearsOfExperience());
        job.setLanguage(request.languages());
        job.setQualification(request.qualifications());
        job.setAvailablePosition(request.availablePosition());
        job.setExpireAt(request.expiresAt());
        job.setCategories(categories);
        job.setSkills(skills);
        job.setIndustry(industry);
        job.setEmployer(employer);

        Jobs savedJob = jobRepository.save(job);
        return jobMapper.toDto(savedJob);
    }
}
