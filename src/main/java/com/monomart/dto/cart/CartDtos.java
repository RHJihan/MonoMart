package com.monomart.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class CartDtos {
    @Data
    public static class AddCartItemRequest {
        @NotNull
        private Long productId;
        @NotNull
        @Min(1)
        private Integer quantity;
    }

    @lombok.Builder
    @lombok.Value
    public static class CartItemResponse {
        Long id;
        Long productId;
        String productName;
        Integer quantity;
        java.math.BigDecimal unitPrice;
        java.math.BigDecimal subtotal;
    }
}


