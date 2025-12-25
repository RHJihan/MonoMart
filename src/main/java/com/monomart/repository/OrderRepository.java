package com.monomart.repository;

import com.monomart.entities.Order;
import com.monomart.entities.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = { "address" })
    Page<Order> findByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = { "address" })
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = { "address" })
    Page<Order> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = { "address" })
    Optional<Order> findById(Long id);
}
