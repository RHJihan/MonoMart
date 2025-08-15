package com.monomart.controller;

import com.monomart.dto.cart.CartDtos;
import com.monomart.service.CartService;
import com.monomart.mapper.Mappers;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.monomart.security.AuthenticatedUser;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;
    private final Mappers mappers;

    public CartController(CartService cartService, Mappers mappers) { this.cartService = cartService; this.mappers = mappers; }

    private Long currentUserId(Authentication auth) {
        Object principal = auth.getPrincipal();
        if (principal instanceof AuthenticatedUser au) {
            return au.getUserId();
        }
        throw new IllegalStateException("Invalid authentication principal");
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<CartDtos.CartItemResponse> list(Authentication auth) {
        return cartService.getCartItems(currentUserId(auth)).stream().map(mappers::toCartItemResponse).toList();
    }

    @PostMapping
    public ResponseEntity<CartDtos.CartItemResponse> add(@Valid @RequestBody CartDtos.AddCartItemRequest request, Authentication auth) {
        return ResponseEntity.ok(mappers.toCartItemResponse(cartService.addItem(currentUserId(auth), request)));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> remove(@PathVariable Long productId, Authentication auth) {
        cartService.removeItem(currentUserId(auth), productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/total")
    public BigDecimal total(Authentication auth) {
        return cartService.computeTotal(currentUserId(auth));
    }
}


