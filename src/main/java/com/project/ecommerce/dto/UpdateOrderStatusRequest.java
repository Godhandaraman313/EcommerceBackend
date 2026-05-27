package com.project.ecommerce.dto;

public class UpdateOrderStatusRequest {

    private String status;
    private String notes;

    public String getStatus() { return status; }
    public String getNotes() { return notes; }
    public void setStatus(String status) { this.status = status; }
    public void setNotes(String notes) { this.notes = notes; }
}
