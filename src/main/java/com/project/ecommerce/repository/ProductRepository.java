package com.project.ecommerce.repository;

import com.project.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p.category.name, COUNT(p) FROM Product p GROUP BY p.category.name ORDER BY p.category.name ASC")
    List<Object[]> findCategorySummaries();

    @Query("""
        SELECT p FROM Product p
        WHERE (:category = '' OR LOWER(p.category.name) LIKE LOWER(CONCAT('%', :category, '%')))
          AND (:brandId IS NULL OR p.brand.id = :brandId)
          AND (
            :search = ''
            OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(COALESCE(p.description, '')) LIKE LOWER(CONCAT('%', :search, '%'))
          )
        """)
    Page<Product> searchProducts(
            @Param("category") String category,
            @Param("brandId") Long brandId,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE LOWER(p.name) = LOWER(?1) AND LOWER(p.category.name) = LOWER(?2) AND p.price = ?3")
    boolean existsByNameIgnoreCaseAndCategoryNameIgnoreCaseAndPrice(
            String name,
            String categoryName,
            Double price
    );
}
