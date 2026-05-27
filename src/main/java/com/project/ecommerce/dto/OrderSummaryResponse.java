package com.project.ecommerce.dto;

import com.project.ecommerce.model.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrderSummaryResponse {

    private Long id;
    private LocalDateTime createdAt;
    private double total;
    private String status;
    private String paymentMethod;
    private String productNames;
    private boolean returnRequested;

    public static OrderSummaryResponse from(Order order, List<String> productNames) {
        OrderSummaryResponse dto = new OrderSummaryResponse();
        dto.id = order.getId();
        dto.createdAt = order.getCreatedAt();
        dto.total = order.getTotal();
        dto.status = order.getStatus();
        dto.paymentMethod = order.getPaymentMethod();
        dto.productNames = productNames.stream().collect(Collectors.joining(", "));
        dto.returnRequested = order.isReturnRequested();
        return dto;
    }

    public Long getId() { return id; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public double getTotal() { return total; }
    public String getStatus() { return status; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getProductNames() { return productNames; }
    public boolean isReturnRequested() { return returnRequested; }
}
