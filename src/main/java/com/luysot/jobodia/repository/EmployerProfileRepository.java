package com.luysot.jobodia.repository;

import com.luysot.jobodia.model.EmployerProfiles;
import com.luysot.jobodia.model.SeekerProfiles;
import com.luysot.jobodia.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployerProfileRepository extends JpaRepository<EmployerProfiles, Long> {
    Optional<EmployerProfiles> findByUser(Users user);
}