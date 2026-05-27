package com.project.ecommerce.dto;

import com.project.ecommerce.model.Review;

import java.time.LocalDateTime;

public class ReviewResponse {

    private Long id;
    private String headline;
    private String comment;
    private int rating;
    private LocalDateTime reviewTime;
    private String customerEmail;
    private String customerName;
    private Long productId;
    private String productName;

    public static ReviewResponse from(Review review) {
        ReviewResponse dto = new ReviewResponse();
        dto.id = review.getId();
        dto.headline = review.getHeadline();
        dto.comment = review.getComment();
        dto.rating = review.getRating();
        dto.reviewTime = review.getReviewTime();
        dto.customerEmail = review.getCustomerEmail();
        dto.customerName = review.getCustomerName();
        if (review.getProduct() != null) {
            dto.productId = review.getProduct().getId();
            dto.productName = review.getProduct().getName();
        }
        return dto;
    }

    public Long getId() { return id; }
    public String getHeadline() { return headline; }
    public String getComment() { return comment; }
    public int getRating() { return rating; }
    public LocalDateTime getReviewTime() { return reviewTime; }
    public String getCustomerEmail() { return customerEmail; }
    public String getCustomerName() { return customerName; }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
}
