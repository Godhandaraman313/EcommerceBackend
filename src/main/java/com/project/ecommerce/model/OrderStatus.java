package com.project.ecommerce.model;

public enum OrderStatus {
    NEW,
    PROCESSING,
    PICKED,
    SHIPPING,
    DELIVERED,
    CANCELLED,
    RETURN_REQUESTED,
    RETURNED;

    public static OrderStatus fromString(String value) {
        return OrderStatus.valueOf(value.toUpperCase());
    }
}
