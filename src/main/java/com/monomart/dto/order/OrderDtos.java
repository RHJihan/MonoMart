package com.monomart.dto.order;

import com.monomart.domain.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public class OrderDtos {
    @lombok.Builder
    @lombok.Value
    public static class OrderResponse {
        Long id;
        java.math.BigDecimal totalAmount;
        OrderStatus status;
        java.time.Instant createdAt;
    }

    @lombok.Data
    public static class UpdateOrderStatusRequest {
        @NotNull
        private OrderStatus status;
    }
}


