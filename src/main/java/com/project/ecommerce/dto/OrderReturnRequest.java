package com.project.ecommerce.dto;

public class OrderReturnRequest {

    private String reason;
    private String note;

    public String getReason() { return reason; }
    public String getNote() { return note; }
    public void setReason(String reason) { this.reason = reason; }
    public void setNote(String note) { this.note = note; }
}
