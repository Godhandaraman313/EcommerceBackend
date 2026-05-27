package com.project.ecommerce.dto;

import com.project.ecommerce.model.CartItem;

public class CartItemResponse {

    private Long id;
    private Long productId;
    private String productName;
    private String category;
    private double price;
    private int quantity;
    private double subtotal;
    private String imageUrl;

    public CartItemResponse(CartItem item) {
        this.id = item.getId();
        this.productId = item.getProduct().getId();
        this.productName = item.getProduct().getName();
        this.category = item.getProduct().getCategory();
        this.price = item.getProduct().getPrice();
        this.quantity = item.getQuantity();
        this.subtotal = this.price * this.quantity;
        this.imageUrl = item.getProduct().getImageUrl();
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
