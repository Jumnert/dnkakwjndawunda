package com.luysot.jobodia.repository;

import com.luysot.jobodia.model.Categories;
import com.luysot.jobodia.model.EmployerProfiles;
import com.luysot.jobodia.model.Jobs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface JobRepository extends JpaRepository<Jobs, Long> , JpaSpecificationExecutor<Jobs> {
    Page<Jobs> findByEmployer(EmployerProfiles employerProfile, Pageable pageable);
    Optional<Jobs> findByIdAndEmployer(Long id,EmployerProfiles employerProfiles);
    Set<Jobs> findByCategoriesCategoryName(String categoryName);
    void deleteByIdAndEmployer(Long id,EmployerProfiles employerProfiles);
    List<Jobs> findTop5ByOrderByCreatedAtDesc();
}