package com.monomart.controller;

import com.monomart.dto.category.CategoryDtos;
import com.monomart.service.CategoryService;
import com.monomart.mapper.Mappers;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final Mappers mappers;

    public CategoryController(CategoryService categoryService, Mappers mappers) { this.categoryService = categoryService; this.mappers = mappers; }

    @GetMapping
    public Page<CategoryDtos.CategoryResponse> list(Pageable pageable) { return categoryService.list(pageable).map(mappers::toCategoryResponse); }

    @GetMapping("/{id}")
    public CategoryDtos.CategoryResponse get(@PathVariable Long id) { return mappers.toCategoryResponse(categoryService.get(id)); }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CategoryDtos.CategoryResponse> create(@Valid @RequestBody CategoryDtos.CreateCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mappers.toCategoryResponse(categoryService.create(request)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public CategoryDtos.CategoryResponse update(@PathVariable Long id, @Valid @RequestBody CategoryDtos.UpdateCategoryRequest request) {
        return mappers.toCategoryResponse(categoryService.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}


