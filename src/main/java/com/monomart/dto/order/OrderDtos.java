package com.monomart.dto.order;

import com.monomart.entities.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public class OrderDtos {
    @lombok.Builder
    @lombok.Value
    public static class OrderResponse {
        Long id;
        java.math.BigDecimal totalAmount;
        OrderStatus status;
        OrderAddressResponse address;
        java.time.Instant createdAt;
    }

    @lombok.Builder
    @lombok.Value
    public static class OrderAddressResponse {
        Long id;
        String addressType;
        String addressLine1;
        String addressLine2;
        String upazila;
        String city;
        String country;
        String phone;
        String email;
    }

    @lombok.Data
    public static class UpdateOrderStatusRequest {
        @NotNull
        private OrderStatus status;
    }

    @lombok.Data
    public static class PlaceOrderRequest {
        @NotNull
        private Long addressId;
    }
}
