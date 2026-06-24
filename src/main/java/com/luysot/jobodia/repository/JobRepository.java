package com.luysot.jobodia.repository;

import com.luysot.jobodia.model.Jobs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Jobs, Long> {
}