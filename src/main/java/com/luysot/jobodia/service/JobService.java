package com.luysot.jobodia.service;

import com.luysot.jobodia.dto.JobDTOs.JobRequestDto;
import com.luysot.jobodia.dto.JobDTOs.JobResponseDto;
import com.luysot.jobodia.dto.JobDTOs.UpdateJobStatusRequestDto;
import com.luysot.jobodia.mapper.JobMapper;
import com.luysot.jobodia.model.*;
import com.luysot.jobodia.model.enums.JobLevel;
import com.luysot.jobodia.model.enums.JobSite;
import com.luysot.jobodia.model.enums.JobStatus;
import com.luysot.jobodia.model.enums.JobTime;
import com.luysot.jobodia.exception.DuplicateResourceException;
import com.luysot.jobodia.exception.InvalidRequestException;
import com.luysot.jobodia.exception.ResourceNotFoundException;
import com.luysot.jobodia.repository.*;
import com.luysot.jobodia.service.specification.JobSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final EmployerProfileRepository employerProfileRepository;
    private final CategoryRepository categoryRepository;
    private final SkillRepository skillRepository;
    private final IndustryRepository industryRepository;
    private final JobMapper jobMapper;
    private final JobSpecification jobSpecification;

    public JobResponseDto addJob(String email, JobRequestDto request){
        Users user = findUserByEmail(email);
        EmployerProfiles employer = findEmployerByUser(user);

        Set<Categories> categories = loadCategories(request.categoriesId());
        Set<Skills> skills = loadSkills(request.skillsId());

        Industries industry = industryRepository.findById(request.industriesId())
                .orElseThrow(() -> new ResourceNotFoundException("Industry not found"));

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
        job.setStatus(JobStatus.OPEN);
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

    public JobResponseDto updateJob(Long id, JobRequestDto request, String email){
        Jobs existingJob = findOwnEmployerJobEntity(email,id);
        Set<Categories> categories = loadCategories(request.categoriesId());
        Set<Skills> skills = loadSkills(request.skillsId());

        Industries industry = industryRepository.findById(request.industriesId())
                .orElseThrow(() -> new ResourceNotFoundException("Industry not found"));

        existingJob.setTitle(request.title());
        existingJob.setMinSalary(request.minSalary());
        existingJob.setMaxSalary(request.maxSalary());
        existingJob.setResponsibilities(request.responsibilities());
        existingJob.setRequirements(request.requirements());
        existingJob.setDescription(request.description());
        existingJob.setSummary(request.summary());
        existingJob.setBenefits(request.benefits());
        existingJob.setJobType(request.jobType());
        existingJob.setJobLevel(request.jobLevel());
        existingJob.setJobGender(request.jobGender());
        existingJob.setJobSite(request.jobSite());
        existingJob.setStatus(existingJob.getStatus() == null ? JobStatus.DRAFT : existingJob.getStatus());
        existingJob.setYearsOfExperience(request.yearsOfExperience());
        existingJob.setLanguage(request.languages());
        existingJob.setQualification(request.qualifications());
        existingJob.setAvailablePosition(request.availablePosition());
        existingJob.setExpireAt(request.expiresAt());
        existingJob.setCategories(categories);
        existingJob.setSkills(skills);
        existingJob.setIndustry(industry);

        Jobs savedJob = jobRepository.save(existingJob);
        return jobMapper.toDto(savedJob);
    }

    @Transactional
    public void deleteJob(Long id, String email){
        EmployerProfiles employer = findEmployerByUser(findUserByEmail(email));
        findOwnEmployerJobEntity(email, id);
        jobRepository.deleteByIdAndEmployer(id,employer);
    }

    public JobResponseDto updateJobStatus(Long id, UpdateJobStatusRequestDto request, String email) {
        Jobs job = findOwnEmployerJobEntity(email, id);
        JobStatus nextStatus = request.status();

        if (!canTransition(job.getStatus(), nextStatus)) {
            throw new InvalidRequestException("Invalid job status transition from " + job.getStatus() + " to " + nextStatus);
        }

        job.setStatus(nextStatus);
        return jobMapper.toDto(jobRepository.save(job));
    }

//    public JobResponseDto publishJob(Long id, String email) {
//        return updateJobStatus(id, new UpdateJobStatusRequestDto(JobStatus.OPEN), email);
//    }
//
//    public JobResponseDto archiveJob(Long id, String email) {
//        return updateJobStatus(id, new UpdateJobStatusRequestDto(JobStatus.ARCHIVED), email);
//    }

    public Page<JobResponseDto> findJobs(Pageable pageable){
        return jobRepository.findAll(pageable).map(jobMapper::toDto);
    }

    public Page<JobResponseDto> searchJob(
            String title,
            String industry,
            String company,
            String category,
            JobTime jobType,
            JobLevel jobLevel,
            JobSite jobSite,
            Pageable pageable
    ){
        Specification<Jobs> spec = (root, query, cb) -> cb.conjunction();
        if (title != null && !title.isBlank()) {
            spec = spec.and(JobSpecification.hasTitle(title));
        }

        if (industry != null && !industry.isBlank()) {
            spec = spec.and(JobSpecification.hasIndustry(industry));
        }

        if (company != null && !company.isBlank()) {
            spec = spec.and(JobSpecification.hasCompany(company));
        }

        if (category != null && !category.isBlank()) {
            spec = spec.and(JobSpecification.hasCategory(category));
        }

        if (jobType != null) {
            spec = spec.and(JobSpecification.hasJobType(jobType));
        }

        if (jobLevel != null) {
            spec = spec.and(JobSpecification.hasJobLevel(jobLevel));
        }

        if (jobSite != null) {
            spec = spec.and(JobSpecification.hasJobSite(jobSite));
        }


        return jobRepository.findAll(spec, pageable).map(jobMapper::toDto);
    }

    public Page<JobResponseDto> findNewlyAddedJob(Pageable pageable){
        return jobRepository.findAll(pageable).map(jobMapper::toDto);
    }

    public JobResponseDto findJob(Long id){
        Jobs job = jobRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Job not found"));
        return jobMapper.toDto(job);
    }


    public Page<JobResponseDto> findOwnEmployerJobs(String email, Pageable pageable){
        EmployerProfiles employer = findEmployerByUser(findUserByEmail(email));

        return jobRepository.findByEmployer(employer, pageable).map(jobMapper::toDto);
    }

    public JobResponseDto findOwnEmployerJob(String email, Long id){
        return jobMapper.toDto(findOwnEmployerJobEntity(email, id));
    }

    private Jobs findOwnEmployerJobEntity(String email, Long id) {
        EmployerProfiles employer = findEmployerByUser(findUserByEmail(email));
        return jobRepository.findByIdAndEmployer(id,employer).orElseThrow(()->new ResourceNotFoundException("Job not found"));
    }

    private Users findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private EmployerProfiles findEmployerByUser(Users user) {
        return employerProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Employer profile not found"));
    }

    private Set<Categories> loadCategories(Set<Long> categoryIds) {
        Set<Categories> categories = new HashSet<>(categoryRepository.findAllById(categoryIds));
        ensureAllIdsLoaded("Category", categoryIds, categories.stream().map(Categories::getId).collect(Collectors.toSet()));
        return categories;
    }

    private Set<Skills> loadSkills(Set<Long> skillIds) {
        Set<Skills> skills = new HashSet<>(skillRepository.findAllById(skillIds));
        ensureAllIdsLoaded("Skill", skillIds, skills.stream().map(Skills::getId).collect(Collectors.toSet()));
        return skills;
    }

    private void ensureAllIdsLoaded(String label, Set<Long> requestedIds, Set<Long> loadedIds) {
        if (requestedIds == null || requestedIds.isEmpty()) {
            throw new InvalidRequestException(label + " ids are required");
        }

        Set<Long> missingIds = new HashSet<>(requestedIds);
        missingIds.removeAll(loadedIds);

        if (!missingIds.isEmpty()) {
            throw new InvalidRequestException("Invalid " + label.toLowerCase() + " id(s): " + missingIds);
        }
    }

    private boolean canTransition(JobStatus current, JobStatus next) {
        if (current == null) {
            current = JobStatus.DRAFT;
        }

        return switch (current) {
            case DRAFT -> next == JobStatus.OPEN || next == JobStatus.ARCHIVED;
            case OPEN -> next == JobStatus.PAUSED || next == JobStatus.CLOSED || next == JobStatus.ARCHIVED;
            case PAUSED -> next == JobStatus.OPEN || next == JobStatus.CLOSED || next == JobStatus.ARCHIVED;
            case CLOSED -> next == JobStatus.ARCHIVED;
            case ARCHIVED -> false;
        };
    }
}
