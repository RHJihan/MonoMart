package com.monomart.service;

import com.monomart.domain.Category;
import com.monomart.domain.Product;
import com.monomart.dto.product.ProductDtos;
import com.monomart.mapper.Mappers;
import com.monomart.repository.CategoryRepository;
import com.monomart.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final Mappers mappers;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, Mappers mappers) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.mappers = mappers;
    }

    public Page<Product> list(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Page<Product> listByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable);
    }

    public Page<Product> searchByName(String query, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(query, pageable);
    }

    public Product get(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    @Transactional
    public Product create(ProductDtos.CreateProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(category);
        product.setImageUrl(request.getImageUrl());
        return productRepository.save(product);
    }

    @Transactional
    public Product update(Long id, ProductDtos.UpdateProductRequest request) {
        Product product = get(id);
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(category);
        product.setImageUrl(request.getImageUrl());
        return productRepository.save(product);
    }

    @Transactional
    public Product updateImage(Long id, String imageUrlOrBase64) {
        Product product = get(id);
        product.setImageUrl(imageUrlOrBase64);
        return productRepository.save(product);
    }

    @Transactional
    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}


