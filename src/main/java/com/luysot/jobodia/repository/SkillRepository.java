package com.luysot.jobodia.repository;

import com.luysot.jobodia.model.Skills;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepository extends JpaRepository<Skills, Long> {
}