package com.luysot.jobodia.repository;

import com.luysot.jobodia.model.Applications;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Applications, Long> {
}