package com.luysot.jobodia.repository;

import com.luysot.jobodia.model.Skills;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillRepository extends JpaRepository<Skills, Long> {
}