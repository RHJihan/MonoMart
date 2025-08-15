package com.monomart.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Data;

public class ProductDtos {
    @Data
    public static class CreateProductRequest {
        @NotBlank
        @Size(min = 2, max = 150)
        private String name;
        @Size(max = 1000)
        private String description;
        @NotNull
        @jakarta.validation.constraints.DecimalMin(value = "0.0", inclusive = true)
        private BigDecimal price;
        @NotNull
        @Min(0)
        private Integer stockQuantity;
        @NotNull
        private Long categoryId;
        @Size(max = 500)
        private String imageUrl;
    }

    @Data
    public static class UpdateProductRequest {
        @NotBlank
        @Size(min = 2, max = 150)
        private String name;
        @Size(max = 1000)
        private String description;
        @NotNull
        @jakarta.validation.constraints.DecimalMin(value = "0.0", inclusive = true)
        private BigDecimal price;
        @NotNull
        @Min(0)
        private Integer stockQuantity;
        @NotNull
        private Long categoryId;
        @Size(max = 500)
        private String imageUrl;
    }

    @lombok.Builder
    @lombok.Value
    public static class ProductResponse {
        Long id;
        String name;
        String description;
        BigDecimal price;
        Integer stockQuantity;
        Long categoryId;
        String imageUrl;
    }
}


