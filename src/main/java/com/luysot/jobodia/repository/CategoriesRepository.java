package com.luysot.jobodia.repository;

import com.luysot.jobodia.model.Categories;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriesRepository extends JpaRepository<Categories, Long> {
}