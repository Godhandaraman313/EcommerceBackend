package com.project.ecommerce.model;

import jakarta.persistence.*;

@Entity
@Table(
    name = "product",
    indexes = {
        @Index(name = "idx_category", columnList = "category"),
        @Index(name = "idx_price", columnList = "price")
    }
)
public class Product {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private Double price;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // GETTERS
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public Double getPrice() { return price; }

    // SETTERS
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setPrice(Double price) { this.price = price; }
}