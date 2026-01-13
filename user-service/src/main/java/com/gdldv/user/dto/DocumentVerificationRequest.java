package com.gdldv.user.dto;

import jakarta.validation.constraints.NotBlank;

public class DocumentVerificationRequest {
    @NotBlank
    private String status;
    private String notes;

    public DocumentVerificationRequest() {}

    public DocumentVerificationRequest(String status, String notes) {
        this.status = status;
        this.notes = notes;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
