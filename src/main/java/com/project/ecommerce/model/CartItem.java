package com.project.ecommerce.model;

import jakarta.persistence.*;

@Entity
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id") 
    private Product product;

    private int quantity;

    private String userEmail;

    // getters
    public Long getId() { return id; }
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public String getUserEmail() { return userEmail; }

    // setters
    public void setId(Long id) { this.id = id; }
    public void setProduct(Product product) { this.product = product; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
}