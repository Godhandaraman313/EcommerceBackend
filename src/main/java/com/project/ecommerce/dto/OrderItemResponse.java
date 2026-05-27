package com.project.ecommerce.dto;

import com.project.ecommerce.model.OrderItem;

public class OrderItemResponse {

    private Long id;
    private Long productId;
    private String productName;
    private int quantity;
    private double price;
    private double subtotal;

    public static OrderItemResponse from(OrderItem item) {
        OrderItemResponse dto = new OrderItemResponse();
        dto.id = item.getId();
        dto.productId = item.getProductId();
        dto.productName = item.getProductName();
        dto.quantity = item.getQuantity();
        dto.price = item.getPrice();
        dto.subtotal = item.getPrice() * item.getQuantity();
        return dto;
    }

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public double getSubtotal() { return subtotal; }
}
