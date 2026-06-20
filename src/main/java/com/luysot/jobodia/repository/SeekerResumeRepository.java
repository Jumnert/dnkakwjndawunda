package com.luysot.jobodia.repository;

import com.luysot.jobodia.model.SeekerProfiles;
import com.luysot.jobodia.model.SeekerResumes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeekerResumeRepository extends JpaRepository<SeekerResumes, Long> {
    List<SeekerResumes> findBySeeker(SeekerProfiles seeker);
    Optional<SeekerResumes> findByIdAndSeeker(Long id,SeekerProfiles seeker);
    void deleteByIdAndSeeker(Long id, SeekerProfiles seeker);
}