package com.project.ecommerce.controller;

import com.project.ecommerce.dto.ReviewRequest;
import com.project.ecommerce.dto.ReviewResponse;
import com.project.ecommerce.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("http://localhost:5173")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/api/products/{productId}/reviews")
    public Page<ReviewResponse> listForProduct(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return reviewService.listByProduct(
                productId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "reviewTime"))
        );
    }

    @PostMapping("/api/products/{productId}/reviews")
    public ReviewResponse create(
            @PathVariable Long productId,
            @RequestBody ReviewRequest request,
            Authentication authentication
    ) {
        return reviewService.create(productId, authentication.getName(), request);
    }

    @GetMapping("/api/reviews/me")
    public List<ReviewResponse> myReviews(Authentication authentication) {
        return reviewService.listByCustomer(authentication.getName());
    }

    @DeleteMapping("/api/reviews/{id}")
    public void delete(@PathVariable Long id, Authentication authentication) {
        reviewService.delete(id, authentication.getName());
    }
}
