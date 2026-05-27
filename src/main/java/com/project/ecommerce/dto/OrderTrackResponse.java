package com.project.ecommerce.dto;

import com.project.ecommerce.model.OrderTrack;

import java.time.LocalDateTime;

public class OrderTrackResponse {

    private Long id;
    private String status;
    private String notes;
    private LocalDateTime createdAt;

    public static OrderTrackResponse from(OrderTrack track) {
        OrderTrackResponse dto = new OrderTrackResponse();
        dto.id = track.getId();
        dto.status = track.getStatus();
        dto.notes = track.getNotes();
        dto.createdAt = track.getCreatedAt();
        return dto;
    }

    public Long getId() { return id; }
    public String getStatus() { return status; }
    public String getNotes() { return notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
