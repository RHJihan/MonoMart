package com.monomart.controller;

import com.monomart.entities.Order;
import com.monomart.dto.order.OrderDtos;
import com.monomart.security.AuthenticatedUser;
import com.monomart.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    private Long currentUserId(Authentication auth) {
        Object principal = auth.getPrincipal();
        if (principal instanceof AuthenticatedUser au)
            return au.getUserId();
        throw new IllegalStateException("Invalid principal");
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<OrderDtos.OrderResponse> listAll(Pageable pageable) {
        return orderService.listAll(pageable).map(o -> OrderDtos.OrderResponse.builder()
                .id(o.getId())
                .totalAmount(o.getTotalAmount())
                .status(o.getStatus())
                .address(o.getAddress() != null ? OrderDtos.OrderAddressResponse.builder()
                        .id(o.getAddress().getId())
                        .addressType(o.getAddress().getAddressType())
                        .addressLine1(o.getAddress().getAddressLine1())
                        .addressLine2(o.getAddress().getAddressLine2())
                        .upazila(o.getAddress().getUpazila())
                        .city(o.getAddress().getCity())
                        .country(o.getAddress().getCountry())
                        .phone(o.getAddress().getPhone())
                        .email(o.getAddress().getEmail())
                        .build() : null)
                .createdAt(o.getCreatedAt())
                .build());
    }

    @GetMapping("/me")
    public Page<OrderDtos.OrderResponse> myOrders(Pageable pageable, Authentication auth) {
        return orderService.listByUser(currentUserId(auth), pageable).map(o -> OrderDtos.OrderResponse.builder()
                .id(o.getId())
                .totalAmount(o.getTotalAmount())
                .status(o.getStatus())
                .address(o.getAddress() != null ? OrderDtos.OrderAddressResponse.builder()
                        .id(o.getAddress().getId())
                        .addressType(o.getAddress().getAddressType())
                        .addressLine1(o.getAddress().getAddressLine1())
                        .addressLine2(o.getAddress().getAddressLine2())
                        .upazila(o.getAddress().getUpazila())
                        .city(o.getAddress().getCity())
                        .country(o.getAddress().getCountry())
                        .phone(o.getAddress().getPhone())
                        .email(o.getAddress().getEmail())
                        .build() : null)
                .createdAt(o.getCreatedAt())
                .build());
    }

    @PostMapping("/place")
    public ResponseEntity<OrderDtos.OrderResponse> place(
            @RequestBody @jakarta.validation.Valid OrderDtos.PlaceOrderRequest request, Authentication auth) {
        Order o = orderService.placeOrder(currentUserId(auth), request.getAddressId());
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderDtos.OrderResponse.builder()
                .id(o.getId())
                .totalAmount(o.getTotalAmount())
                .status(o.getStatus())
                .address(o.getAddress() != null ? OrderDtos.OrderAddressResponse.builder()
                        .id(o.getAddress().getId())
                        .addressType(o.getAddress().getAddressType())
                        .addressLine1(o.getAddress().getAddressLine1())
                        .addressLine2(o.getAddress().getAddressLine2())
                        .upazila(o.getAddress().getUpazila())
                        .city(o.getAddress().getCity())
                        .country(o.getAddress().getCountry())
                        .phone(o.getAddress().getPhone())
                        .email(o.getAddress().getEmail())
                        .build() : null)
                .createdAt(o.getCreatedAt())
                .build());
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderDtos.OrderResponse updateStatus(@PathVariable Long orderId,
            @RequestBody OrderDtos.UpdateOrderStatusRequest request) {
        Order o = orderService.updateStatus(orderId, request.getStatus());
        return OrderDtos.OrderResponse.builder()
                .id(o.getId())
                .totalAmount(o.getTotalAmount())
                .status(o.getStatus())
                .address(o.getAddress() != null ? OrderDtos.OrderAddressResponse.builder()
                        .id(o.getAddress().getId())
                        .addressType(o.getAddress().getAddressType())
                        .addressLine1(o.getAddress().getAddressLine1())
                        .addressLine2(o.getAddress().getAddressLine2())
                        .upazila(o.getAddress().getUpazila())
                        .city(o.getAddress().getCity())
                        .country(o.getAddress().getCountry())
                        .phone(o.getAddress().getPhone())
                        .email(o.getAddress().getEmail())
                        .build() : null)
                .createdAt(o.getCreatedAt())
                .build();
    }
}
