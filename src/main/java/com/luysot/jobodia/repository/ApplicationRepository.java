package com.luysot.jobodia.repository;

import com.luysot.jobodia.model.Applications;
import com.luysot.jobodia.model.EmployerProfiles;
import com.luysot.jobodia.model.Jobs;
import com.luysot.jobodia.model.SeekerProfiles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Applications, Long> {
    Page<Applications> findBySeeker(SeekerProfiles seeker, Pageable pageable);
    Optional<Applications> findByIdAndSeeker(Long id, SeekerProfiles seeker);
    void deleteByIdAndSeeker(Long id, SeekerProfiles seeker);
    Page<Applications> findByJobEmployerId(Long employerId, Pageable pageable);

    Optional<Applications> findByJobEmployerId_AndId(Long jobEmployerId, Long id);
    Boolean existsByJobAndSeeker(Jobs job, SeekerProfiles seeker);
}