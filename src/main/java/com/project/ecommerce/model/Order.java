package com.project.ecommerce.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;

    private String paymentMethod;

    private double total;

    @Column(nullable = false)
    private String status = "NEW";

    @Column(columnDefinition = "TEXT")
    private String shippingAddress;

    private LocalDateTime createdAt;

    @Column(name = "return_requested", nullable = false)
    private boolean returnRequested = false;

    private String returnReason;

    @Column(columnDefinition = "TEXT")
    private String returnNote;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null || status.isBlank()) {
            status = "NEW";
        }
    }

    public Long getId() { return id; }
    public String getUserEmail() { return userEmail; }
    public String getPaymentMethod() { return paymentMethod; }
    public double getTotal() { return total; }
    public String getStatus() { return status; }
    public String getShippingAddress() { return shippingAddress; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isReturnRequested() { return returnRequested; }
    public String getReturnReason() { return returnReason; }
    public String getReturnNote() { return returnNote; }

    public void setId(Long id) { this.id = id; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setTotal(double total) { this.total = total; }
    public void setStatus(String status) { this.status = status; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setReturnRequested(boolean returnRequested) { this.returnRequested = returnRequested; }
    public void setReturnReason(String returnReason) { this.returnReason = returnReason; }
    public void setReturnNote(String returnNote) { this.returnNote = returnNote; }
}
