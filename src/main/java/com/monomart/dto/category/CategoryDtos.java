package com.monomart.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class CategoryDtos {
    @Data
    public static class CreateCategoryRequest {
        @NotBlank
        @Size(min = 2, max = 100)
        private String name;
        @Size(max = 500)
        private String description;
    }

    @Data
    public static class UpdateCategoryRequest {
        @NotBlank
        @Size(min = 2, max = 100)
        private String name;
        @Size(max = 500)
        private String description;
    }

    @lombok.Builder
    @lombok.Value
    public static class CategoryResponse {
        Long id;
        String name;
        String description;
    }
}


