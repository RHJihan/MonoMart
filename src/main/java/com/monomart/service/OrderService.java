package com.monomart.service;

import com.monomart.entities.CartItem;
import com.monomart.entities.Order;
import com.monomart.entities.OrderItem;
import com.monomart.entities.Product;
import com.monomart.entities.User;
import com.monomart.entities.enums.OrderStatus;
import com.monomart.repository.CartItemRepository;
import com.monomart.repository.OrderItemRepository;
import com.monomart.repository.OrderRepository;
import com.monomart.repository.ProductRepository;
import com.monomart.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, CartItemRepository cartItemRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public Page<Order> listAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public Page<Order> listByUser(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable);
    }

    public Order get(Long id) { return orderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Order not found")); }

    @Transactional
    public Order placeOrder(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        if (cartItems.isEmpty()) throw new IllegalArgumentException("Cart is empty");

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem ci : cartItems) {
            Product p = ci.getProduct();
            if (ci.getQuantity() > p.getStockQuantity())
                throw new IllegalArgumentException("Insufficient stock for product: " + p.getName());
            total = total.add(p.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())));
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(total);
        order = orderRepository.save(order);

        for (CartItem ci : cartItems) {
            Product p = ci.getProduct();
            p.setStockQuantity(p.getStockQuantity() - ci.getQuantity());
            productRepository.save(p);

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(p);
            oi.setQuantity(ci.getQuantity());
            oi.setPrice(p.getPrice());
            orderItemRepository.save(oi);
        }

        cartItemRepository.deleteByUserId(userId);
        return order;
    }

    @Transactional
    public Order updateStatus(Long orderId, OrderStatus status) {
        Order order = get(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }
}


