package com.monomart.controller;

import com.monomart.dto.cart.CartDtos;
import com.monomart.dto.order.OrderDtos;
import com.monomart.dto.user.UserAddressDtos;
import com.monomart.dto.user.UserProfileResponse;
import com.monomart.entities.User;
import com.monomart.mapper.Mappers;
import com.monomart.security.AuthenticatedUser;
import com.monomart.service.CartService;
import com.monomart.service.OrderService;
import com.monomart.service.UserAddressService;
import com.monomart.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final OrderService orderService;
    private final CartService cartService;
    private final UserAddressService addressService;
    private final UserService userService;
    private final Mappers mappers;

    public UserController(OrderService orderService,
                          CartService cartService,
                          UserAddressService addressService,
                          UserService userService,
                          Mappers mappers) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.addressService = addressService;
        this.userService = userService;
        this.mappers = mappers;
    }

    private Long currentUserId(Authentication auth) {
        Object principal = auth.getPrincipal();
        if (principal instanceof AuthenticatedUser au) return au.getUserId();
        throw new IllegalStateException("Invalid principal");
    }

    private Page<CartDtos.CartItemResponse> buildCartPage(Long userId, Pageable pageable) {
        List<CartDtos.CartItemResponse> items = cartService.getCartItems(userId)
                .stream()
                .map(mappers::toCartItemResponse)
                .toList();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), items.size());
        List<CartDtos.CartItemResponse> pageContent = start > end ? List.of() : items.subList(start, end);
        return new PageImpl<>(pageContent, pageable, items.size());
    }

    private Page<OrderDtos.OrderResponse> buildOrderPage(Long userId, Pageable pageable) {
        List<OrderDtos.OrderResponse> orders = orderService.listByUser(userId, pageable)
                .stream()
                .map(o -> OrderDtos.OrderResponse.builder()
                        .id(o.getId())
                        .totalAmount(o.getTotalAmount())
                        .status(o.getStatus())
                        .createdAt(o.getCreatedAt())
                        .build())
                .toList();
        return new PageImpl<>(orders, pageable, orders.size());
    }

    private UserProfileResponse buildProfile(User user, Pageable cartPageable, Pageable orderPageable) {
        Long userId = user.getId();

        List<UserAddressDtos.Response> addresses = addressService.getAddressesByUser(user)
                .stream()
                .map(mappers::toUserAddressResponse)
                .toList();

        Page<CartDtos.CartItemResponse> cartItems = buildCartPage(userId, cartPageable);
        Page<OrderDtos.OrderResponse> orders = buildOrderPage(userId, orderPageable);

        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .addresses(addresses)
                .cartItems(cartItems)
                .orders(orders)
                .build();
    }

    // ------------------ Logged-in user ------------------
    @GetMapping("/me")
    @Transactional(readOnly = true)
    public ResponseEntity<UserProfileResponse> getMyProfile(
            Authentication auth,
            @Qualifier("cart") Pageable cartPageable,
            @Qualifier("order") Pageable orderPageable
    ) {
        User user = userService.findById(currentUserId(auth));
        return ResponseEntity.ok(buildProfile(user, cartPageable, orderPageable));
    }


    // ------------------ Admin ------------------
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public ResponseEntity<UserProfileResponse> getUserProfile(
            @PathVariable Long userId,
            @Qualifier("cart") Pageable cartPageable,
            @Qualifier("order") Pageable orderPageable
    ) {
        User user = userService.findById(userId);
        return ResponseEntity.ok(buildProfile(user, cartPageable, orderPageable));
    }
}
