package com.project.ecommerce.repository;

import com.project.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findAll(Pageable pageable);

    Page<Product> findByCategoryIgnoreCase(String category, Pageable pageable);

    // ✅ NEW: FILTER + SEARCH
    Page<Product> findByCategoryIgnoreCaseContainingAndNameIgnoreCaseContaining(
            String category,
            String name,
            Pageable pageable
    );

    // DUPLICATE CHECK
    boolean existsByNameIgnoreCaseAndCategoryIgnoreCaseAndPrice(
            String name,
            String category,
            Double price
    );
}