package com.project.ecommerce.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonSetter;

@Entity
@Table(
    name = "product",
    indexes = {
        @Index(name = "idx_category", columnList = "category_id"),
        @Index(name = "idx_price", columnList = "price")
    }
)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private Double price;

    @Column(name = "image_url", length = 512)
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "in_stock", nullable = false)
    private boolean inStock = true;

    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @Column(name = "review_count")
    private Integer reviewCount = 0;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Transient
    private Long brandId;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<ProductImage> images = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<ProductDetail> details = new java.util.ArrayList<>();

    public Long getBrandId() {
        return brand != null ? brand.getId() : brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category != null ? category.getName() : null; }
    public Category getCategoryEntity() { return category; }
    public Double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public String getDescription() { return description; }
    public boolean isInStock() { return inStock; }
    public Double getAverageRating() { return averageRating; }
    public Integer getReviewCount() { return reviewCount; }
    public Brand getBrand() { return brand; }
    
    public java.util.List<ProductImage> getImages() { return images; }
    public java.util.List<ProductDetail> getDetails() { return details; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCategory(Category category) { this.category = category; }

    @JsonSetter("category")
    public void setCategoryByName(String categoryName) {
        if (categoryName != null && !categoryName.isBlank()) {
            this.category = new Category(categoryName);
        } else {
            this.category = null;
        }
    }
    public void setPrice(Double price) { this.price = price; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setDescription(String description) { this.description = description; }
    public void setInStock(boolean inStock) { this.inStock = inStock; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
    public void setBrand(Brand brand) { this.brand = brand; }
    
    public void addExtraImage(String imageName) {
        this.images.add(new ProductImage(imageName, this));
    }

    public void removeExtraImage(String imagePath) {
        images.removeIf(img -> img.getName().equals(imagePath));
    }

    public void addDetail(String name, String value) {
        this.details.add(new ProductDetail(name, value, this));
    }
}
