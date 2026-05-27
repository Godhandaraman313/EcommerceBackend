package com.project.ecommerce.dto;

import com.project.ecommerce.model.Order;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDetailResponse {

    private Long id;
    private LocalDateTime createdAt;
    private double total;
    private String status;
    private String paymentMethod;
    private String shippingAddress;
    private List<OrderItemResponse> items;
    private List<OrderTrackResponse> tracks;
    private boolean returnRequested;
    private String returnReason;
    private String returnNote;
    private boolean canRequestReturn;

    public static OrderDetailResponse from(Order order, List<OrderItemResponse> items, List<OrderTrackResponse> tracks) {
        OrderDetailResponse dto = new OrderDetailResponse();
        dto.id = order.getId();
        dto.createdAt = order.getCreatedAt();
        dto.total = order.getTotal();
        dto.status = order.getStatus();
        dto.paymentMethod = order.getPaymentMethod();
        dto.shippingAddress = order.getShippingAddress();
        dto.items = items;
        dto.tracks = tracks;
        dto.returnRequested = order.isReturnRequested();
        dto.returnReason = order.getReturnReason();
        dto.returnNote = order.getReturnNote();
        dto.canRequestReturn = "DELIVERED".equalsIgnoreCase(order.getStatus()) && !order.isReturnRequested();
        return dto;
    }

    public Long getId() { return id; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public double getTotal() { return total; }
    public String getStatus() { return status; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getShippingAddress() { return shippingAddress; }
    public List<OrderItemResponse> getItems() { return items; }
    public List<OrderTrackResponse> getTracks() { return tracks; }
    public boolean isReturnRequested() { return returnRequested; }
    public String getReturnReason() { return returnReason; }
    public String getReturnNote() { return returnNote; }
    public boolean isCanRequestReturn() { return canRequestReturn; }
}
