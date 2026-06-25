package com.luysot.jobodia.mapper;

import com.luysot.jobodia.dto.JobDTOs.JobResponseDto;
import com.luysot.jobodia.model.*;
import com.luysot.jobodia.repository.EmployerProfileRepository;
import com.luysot.jobodia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JobMapper {
    private final EmployerProfileMapper employerProfileMapper;

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
                .yearsOfExperience(job.getYearsOfExperience())
                .languages(job.getLanguage())
                .qualifications(job.getQualification())
                .availablePosition(job.getAvailablePosition())
                .expiresAt(job.getExpireAt())
                .categoriesId(job.getCategories().stream().map(Categories::getId).collect(Collectors.toSet()))
                .skillsId(job.getSkills().stream().map(Skills::getId).collect(Collectors.toSet()))
                .industriesId(List.of(job.getIndustry().getId()))
                .employer(employerProfileMapper.toDto(job.getEmployer()))
                .build();
    }
}
