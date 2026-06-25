package com.luysot.jobodia.service;

import com.luysot.jobodia.dto.CategoryDTOs.CategoryRequestDto;
import com.luysot.jobodia.dto.CategoryDTOs.CategoryResponseDto;
import com.luysot.jobodia.mapper.CategoryMapper;
import com.luysot.jobodia.model.Categories;
import com.luysot.jobodia.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryResponseDto addCategory(CategoryRequestDto dto){
        Categories category = new Categories();
        category.setCategoryName(dto.categoryName());

        return categoryMapper.toDto(categoryRepository.save(category));
    }

    public Set<CategoryResponseDto> findCategories(){
        return new HashSet<>(categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .toList());
    }

    public CategoryResponseDto findCategory(Long id){
        return categoryMapper.toDto(categoryRepository.findById(id).orElseThrow(()->new RuntimeException("Category not found!!")));
    }

    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto dto){
        Categories category = categoryRepository.findById(id).orElseThrow(()->new RuntimeException("Category not found!!"));
        category.setCategoryName(dto.categoryName());

        categoryRepository.save(category);

        return categoryMapper.toDto(category);
    }

    public void deleteCategory(Long id){
        categoryRepository.deleteById(id);
    }
}
