package com.monomart.controller;

import com.monomart.dto.product.ProductDtos;
import com.monomart.mapper.Mappers;
import com.monomart.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products", description = "Product management endpoints")
public class ProductController {

    private final ProductService productService;
    private final Mappers mappers;

    public ProductController(ProductService productService, Mappers mappers) {
        this.productService = productService;
        this.mappers = mappers;
    }

    @GetMapping
    @Operation(summary = "List all products", description = "Get paginated list of all products")
    @SecurityRequirements({}) // Override global security - no authentication required
    public Page<ProductDtos.ProductResponse> list(Pageable pageable) { return productService.list(pageable).map(mappers::toProductResponse); }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "List products by category", description = "Get paginated list of products filtered by category")
    @SecurityRequirements({}) // Override global security - no authentication required
    public Page<ProductDtos.ProductResponse> listByCategory(@PathVariable Long categoryId, Pageable pageable) {
        return productService.listByCategory(categoryId, pageable).map(mappers::toProductResponse);
    }

    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Search products by name")
    @SecurityRequirements({}) // Override global security - no authentication required
    public Page<ProductDtos.ProductResponse> search(@RequestParam("q") String q, Pageable pageable) {
        return productService.searchByName(q, pageable).map(mappers::toProductResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Get detailed information about a specific product")
    @SecurityRequirements({}) // Override global security - no authentication required
    public ProductDtos.ProductResponse get(@PathVariable Long id) { return mappers.toProductResponse(productService.get(id)); }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create a new product", description = "Admin only - Create a new product")
    public ResponseEntity<ProductDtos.ProductResponse> create(@Valid @RequestBody ProductDtos.CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mappers.toProductResponse(productService.create(request)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update a product", description = "Admin only - Update an existing product")
    public ProductDtos.ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductDtos.UpdateProductRequest request) {
        return mappers.toProductResponse(productService.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", description = "Admin only - Delete a product")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/image")
    @Operation(summary = "Update product image", description = "Admin only - Update product image")
    public ProductDtos.ProductResponse updateImage(@PathVariable Long id, @RequestBody String imageUrlOrBase64) {
        return mappers.toProductResponse(productService.updateImage(id, imageUrlOrBase64));
    }
}


