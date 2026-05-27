package com.project.ecommerce.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String headline;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment;

    @Column(nullable = false)
    private int rating;

    @Column(name = "review_time", nullable = false)
    private LocalDateTime reviewTime;

    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @Column(name = "customer_name")
    private String customerName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public Long getId() { return id; }
    public String getHeadline() { return headline; }
    public String getComment() { return comment; }
    public int getRating() { return rating; }
    public LocalDateTime getReviewTime() { return reviewTime; }
    public String getCustomerEmail() { return customerEmail; }
    public String getCustomerName() { return customerName; }
    public Product getProduct() { return product; }

    public void setId(Long id) { this.id = id; }
    public void setHeadline(String headline) { this.headline = headline; }
    public void setComment(String comment) { this.comment = comment; }
    public void setRating(int rating) { this.rating = rating; }
    public void setReviewTime(LocalDateTime reviewTime) { this.reviewTime = reviewTime; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setProduct(Product product) { this.product = product; }
}
