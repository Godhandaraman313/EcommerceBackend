package com.project.ecommerce.dto;

import com.project.ecommerce.model.Order;

import java.time.LocalDateTime;
import java.util.List;

public class AdminOrderSummaryResponse {

    private Long id;
    private LocalDateTime createdAt;
    private double total;
    private String status;
    private String paymentMethod;
    private String userEmail;
    private String recipientName;
    private String phoneNumber;
    private String shippingAddress;
    private String productNames;
    private boolean picked;
    private boolean shipping;
    private boolean delivered;
    private boolean returned;
    private boolean cod;

    public static AdminOrderSummaryResponse from(Order order, List<String> productNames, String recipientName, String phone) {
        AdminOrderSummaryResponse dto = new AdminOrderSummaryResponse();
        dto.id = order.getId();
        dto.createdAt = order.getCreatedAt();
        dto.total = order.getTotal();
        dto.status = order.getStatus();
        dto.paymentMethod = order.getPaymentMethod();
        dto.userEmail = order.getUserEmail();
        dto.recipientName = recipientName;
        dto.phoneNumber = phone;
        dto.shippingAddress = order.getShippingAddress();
        dto.productNames = String.join(", ", productNames);
        dto.cod = "COD".equalsIgnoreCase(order.getPaymentMethod());
        dto.picked = statusAtLeast(order.getStatus(), "PICKED");
        dto.shipping = statusAtLeast(order.getStatus(), "SHIPPING");
        dto.delivered = statusAtLeast(order.getStatus(), "DELIVERED");
        dto.returned = "RETURNED".equalsIgnoreCase(order.getStatus());
        return dto;
    }

    private static boolean statusAtLeast(String current, String target) {
        String[] flow = {"NEW", "PROCESSING", "PICKED", "SHIPPING", "DELIVERED"};
        int currentIdx = indexOf(flow, current);
        int targetIdx = indexOf(flow, target);
        if (currentIdx < 0 || targetIdx < 0) return false;
        return currentIdx >= targetIdx;
    }

    private static int indexOf(String[] arr, String value) {
        if (value == null) return -1;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equalsIgnoreCase(value)) return i;
        }
        return -1;
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
    public String getProductNames() { return productNames; }
    public boolean isPicked() { return picked; }
    public boolean isShipping() { return shipping; }
    public boolean isDelivered() { return delivered; }
    public boolean isReturned() { return returned; }
    public boolean isCod() { return cod; }
}
