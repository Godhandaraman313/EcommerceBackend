package com.project.ecommerce.repository;

import com.project.ecommerce.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserEmail(String userEmail);

    Optional<CartItem> findByUserEmailAndProduct_Id(String userEmail, Long productId);

    void deleteByUserEmail(String userEmail);
}
