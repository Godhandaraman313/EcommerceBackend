package com.project.ecommerce.service;

import com.project.ecommerce.dto.ReviewRequest;
import com.project.ecommerce.dto.ReviewResponse;
import com.project.ecommerce.model.Product;
import com.project.ecommerce.model.Review;
import com.project.ecommerce.model.User;
import com.project.ecommerce.repository.ProductRepository;
import com.project.ecommerce.repository.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    public ReviewService(
            ReviewRepository reviewRepository,
            ProductRepository productRepository,
            UserService userService
    ) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userService = userService;
    }

    public Page<ReviewResponse> listByProduct(Long productId, Pageable pageable) {
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Product not found");
        }
        return reviewRepository.findByProductIdOrderByReviewTimeDesc(productId, pageable)
                .map(ReviewResponse::from);
    }

    public List<ReviewResponse> listByCustomer(String email) {
        return reviewRepository.findByCustomerEmailOrderByReviewTimeDesc(email).stream()
                .map(ReviewResponse::from)
                .toList();
    }

    @Transactional
    public ReviewResponse create(Long productId, String email, ReviewRequest request) {
        validateRequest(request);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (reviewRepository.existsByProductIdAndCustomerEmail(productId, email)) {
            throw new RuntimeException("You have already reviewed this product");
        }

        User user = userService.getByEmail(email);
        String displayName = buildDisplayName(user);

        Review review = new Review();
        review.setHeadline(request.getHeadline().trim());
        review.setComment(request.getComment().trim());
        review.setRating(request.getRating());
        review.setReviewTime(LocalDateTime.now());
        review.setCustomerEmail(email);
        review.setCustomerName(displayName);
        review.setProduct(product);

        Review saved = reviewRepository.save(review);
        refreshProductRatings(productId);

        return ReviewResponse.from(saved);
    }

    @Transactional
    public void delete(Long reviewId, String email) {
        Review review = reviewRepository.findByIdAndCustomerEmail(reviewId, email)
                .orElseThrow(() -> new RuntimeException("Review not found or not yours"));

        Long productId = review.getProduct().getId();
        reviewRepository.delete(review);
        refreshProductRatings(productId);
    }

    private void refreshProductRatings(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Double avg = reviewRepository.averageRatingByProductId(productId);
        long count = reviewRepository.countByProductId(productId);

        product.setAverageRating(avg == null ? 0.0 : avg);
        product.setReviewCount((int) count);
        productRepository.save(product);
    }

    private void validateRequest(ReviewRequest request) {
        if (request.getHeadline() == null || request.getHeadline().isBlank()) {
            throw new RuntimeException("Headline is required");
        }
        if (request.getComment() == null || request.getComment().isBlank()) {
            throw new RuntimeException("Comment is required");
        }
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }
    }

    private String buildDisplayName(User user) {
        String first = user.getFirstName() != null ? user.getFirstName().trim() : "";
        String last = user.getLastName() != null ? user.getLastName().trim() : "";
        String full = (first + " " + last).trim();
        if (!full.isEmpty()) return full;
        return user.getUsername() != null ? user.getUsername() : user.getEmail();
    }
}
