package com.project.ecommerce.repository;

import com.project.ecommerce.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByProductIdOrderByReviewTimeDesc(Long productId, Pageable pageable);

    List<Review> findByCustomerEmailOrderByReviewTimeDesc(String customerEmail);

    boolean existsByProductIdAndCustomerEmail(Long productId, String customerEmail);

    Optional<Review> findByIdAndCustomerEmail(Long id, String customerEmail);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double averageRatingByProductId(@Param("productId") Long productId);

    long countByProductId(Long productId);
}
