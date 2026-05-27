package com.project.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "product_images")
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 512)
    private String name;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;

    public ProductImage() {}

    public ProductImage(String name, Product product) {
        this.name = name;
        this.product = product;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    @Transient
    public String getImagePath() {
        if (name != null && name.startsWith("http")) return name;
        // In this implementation, if using S3, it would typically just store the full URL or a relative path
        // For S3, we might store just the file name and construct the URL, or store full URL.
        return this.name;
    }
}
