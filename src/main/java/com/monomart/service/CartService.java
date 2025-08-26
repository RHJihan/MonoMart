package com.monomart.service;

import com.monomart.entities.CartItem;
import com.monomart.entities.Product;
import com.monomart.entities.User;
import com.monomart.dto.cart.CartDtos;
import com.monomart.repository.CartItemRepository;
import com.monomart.repository.ProductRepository;
import com.monomart.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(CartItemRepository cartItemRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public List<CartItem> getCartItems(Long userId) {
        return cartItemRepository.findByUserId(userId);
    }

    @Transactional
    public CartItem addItem(Long userId, CartDtos.AddCartItemRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Product product = productRepository.findById(request.getProductId()).orElseThrow(() -> new IllegalArgumentException("Product not found"));
        CartItem item = cartItemRepository.findByUserIdAndProductId(userId, request.getProductId()).orElseGet(() -> {
            CartItem ci = new CartItem();
            ci.setUser(user);
            ci.setProduct(product);
            ci.setQuantity(0);
            return ci;
        });
        int newQty = item.getQuantity() + request.getQuantity();
        if (newQty > product.getStockQuantity()) {
            throw new IllegalArgumentException("Quantity exceeds available stock");
        }
        item.setQuantity(newQty);
        return cartItemRepository.save(item);
    }

    @Transactional
    public void removeItem(Long userId, Long productId) {
        cartItemRepository.findByUserIdAndProductId(userId, productId)
                .ifPresent(cartItemRepository::delete);
    }

    public BigDecimal computeTotal(Long userId) {
        return getCartItems(userId).stream()
                .map(ci -> ci.getProduct().getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }
}


