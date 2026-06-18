package com.luysot.jobodia.repository;

import com.luysot.jobodia.model.SeekerProfiles;
import com.luysot.jobodia.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeekerProfileRepository extends JpaRepository<SeekerProfiles, Long> {
    Optional<SeekerProfiles> findByUser(Users user);
}