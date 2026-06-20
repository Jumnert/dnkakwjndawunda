package com.luysot.jobodia.repository;

import com.luysot.jobodia.model.SeekerProfiles;
import com.luysot.jobodia.model.SeekerResumes;
import com.luysot.jobodia.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeekerResumesRepository extends JpaRepository<SeekerResumes, Long> {
    List<SeekerResumes> findBySeeker(SeekerProfiles seeker);
    Optional<SeekerResumes> findByIdAndSeeker(Long id,SeekerProfiles seeker);
}