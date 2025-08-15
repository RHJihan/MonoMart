package com.monomart.service;

import com.monomart.domain.Category;
import com.monomart.dto.category.CategoryDtos;
import com.monomart.mapper.Mappers;
import com.monomart.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final Mappers mappers;

    public CategoryService(CategoryRepository categoryRepository, Mappers mappers) {
        this.categoryRepository = categoryRepository;
        this.mappers = mappers;
    }

    @Transactional
    public Category create(CategoryDtos.CreateCategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName()))
            throw new IllegalArgumentException("Category name already exists");
        Category category = mappers.toCategory(request);
        return categoryRepository.save(category);
    }

    public Page<Category> list(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    public Category get(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }

    @Transactional
    public Category update(Long id, CategoryDtos.UpdateCategoryRequest request) {
        Category category = get(id);
        if (!category.getName().equalsIgnoreCase(request.getName()) && categoryRepository.existsByNameIgnoreCase(request.getName()))
            throw new IllegalArgumentException("Category name already exists");
        mappers.updateCategoryFromDto(request, category);
        return categoryRepository.save(category);
    }

    @Transactional
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}


