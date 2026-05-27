package com.project.ecommerce.repository;

import com.project.ecommerce.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUserEmailOrderByCreatedAtDesc(String userEmail, Pageable pageable);

    Optional<Order> findByIdAndUserEmail(Long id, String userEmail);

    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Order> findByUserEmailContainingIgnoreCaseOrderByCreatedAtDesc(String keyword, Pageable pageable);
}
