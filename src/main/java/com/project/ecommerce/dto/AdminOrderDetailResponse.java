package com.project.ecommerce.dto;

import com.project.ecommerce.model.Order;

import java.time.LocalDateTime;
import java.util.List;

public class AdminOrderDetailResponse {

    private Long id;
    private LocalDateTime createdAt;
    private double total;
    private String status;
    private String paymentMethod;
    private String userEmail;
    private String recipientName;
    private String phoneNumber;
    private String shippingAddress;
    private List<OrderItemResponse> items;
    private List<OrderTrackResponse> tracks;

    public static AdminOrderDetailResponse from(
            Order order,
            List<OrderItemResponse> items,
            List<OrderTrackResponse> tracks,
            String recipientName,
            String phone
    ) {
        AdminOrderDetailResponse dto = new AdminOrderDetailResponse();
        dto.id = order.getId();
        dto.createdAt = order.getCreatedAt();
        dto.total = order.getTotal();
        dto.status = order.getStatus();
        dto.paymentMethod = order.getPaymentMethod();
        dto.userEmail = order.getUserEmail();
        dto.recipientName = recipientName;
        dto.phoneNumber = phone;
        dto.shippingAddress = order.getShippingAddress();
        dto.items = items;
        dto.tracks = tracks;
        return dto;
    }

    public Long getId() { return id; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public double getTotal() { return total; }
    public String getStatus() { return status; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getUserEmail() { return userEmail; }
    public String getRecipientName() { return recipientName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getShippingAddress() { return shippingAddress; }
    public List<OrderItemResponse> getItems() { return items; }
    public List<OrderTrackResponse> getTracks() { return tracks; }
}
