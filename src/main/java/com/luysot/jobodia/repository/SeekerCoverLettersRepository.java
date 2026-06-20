package com.luysot.jobodia.repository;

import com.luysot.jobodia.model.SeekerCoverLetters;
import com.luysot.jobodia.model.SeekerProfiles;
import com.luysot.jobodia.model.SeekerResumes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeekerCoverLettersRepository extends JpaRepository<SeekerCoverLetters, Long> {
    List<SeekerCoverLetters> findBySeeker(SeekerProfiles seeker);
}