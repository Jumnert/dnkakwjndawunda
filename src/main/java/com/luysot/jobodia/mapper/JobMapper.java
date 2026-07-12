package com.luysot.jobodia.mapper;

import com.luysot.jobodia.dto.JobDTOs.JobResponseDto;
import com.luysot.jobodia.exception.ResourceNotFoundException;
import com.luysot.jobodia.model.*;
import com.luysot.jobodia.model.enums.JobStatus;
import com.luysot.jobodia.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JobMapper {
    private final EmployerProfileMapper employerProfileMapper;
    private final IndustryRepository industryRepository;
    private final SkillRepository skillRepository;
    private final CategoryRepository categoryRepository;
    private final EmployerProfileRepository employerProfileRepository;


    public JobResponseDto toDto(Jobs job){
        return JobResponseDto.builder()
                .id(job.getId())
                .title(job.getTitle())
                .minSalary(job.getMinSalary())
                .maxSalary(job.getMaxSalary())
                .responsibilities(job.getResponsibilities())
                .requirements(job.getRequirements())
                .description(job.getDescription())
                .summary(job.getSummary())
                .benefits(job.getBenefits())
                .jobType(job.getJobType())
                .jobLevel(job.getJobLevel())
                .jobGender(job.getJobGender())
                .jobSite(job.getJobSite())
                .status(job.getStatus() == null ? JobStatus.DRAFT : job.getStatus())
                .yearsOfExperience(job.getYearsOfExperience())
                .languages(job.getLanguage())
                .qualifications(job.getQualification())
                .availablePosition(job.getAvailablePosition())
                .expiresAt(job.getExpireAt())
                .categoriesId(job.getCategories().stream().map(Categories::getId).collect(Collectors.toSet()))
                .skillsId(job.getSkills().stream().map(Skills::getId).collect(Collectors.toSet()))
                .industriesId(job.getIndustry().getId())
                .employer(employerProfileMapper.toDto(job.getEmployer()))
                .build();
    }

    public Jobs toEntity(JobResponseDto response) {
        Industries industries = industryRepository.findById(response.industriesId()) .orElseThrow(() -> new ResourceNotFoundException("Industry not found"));
        Set<Categories> categories = new HashSet<>(categoryRepository.findAllById(response.categoriesId()));

        Set<Skills> skills = new HashSet<>(skillRepository.findAllById(response.skillsId()));

        EmployerProfiles employerProfiles = employerProfileRepository.findById(response.employer().id()).orElseThrow(()->new ResourceNotFoundException("Employer profile not found"));

        return Jobs.builder()
                .id(response.id())
                .title(response.title())
                .minSalary(response.minSalary())
                .maxSalary(response.maxSalary())
                .responsibilities(response.responsibilities())
                .requirements(response.requirements())
                .description(response.description())
                .summary(response.summary())
                .benefits(response.benefits())
                .jobType(response.jobType())
                .jobLevel(response.jobLevel())
                .jobGender(response.jobGender())
                .jobSite(response.jobSite())
                .status(response.status() == null ? JobStatus.DRAFT : response.status())
                .yearsOfExperience(response.yearsOfExperience())
                .language(response.languages())
                .qualification(response.qualifications())
                .availablePosition(response.availablePosition())
                .expireAt(response.expiresAt())
                .industry(industries)
                .employer(employerProfiles)
                .categories(categories)
                .skills(skills)
                .build();
    }
}
