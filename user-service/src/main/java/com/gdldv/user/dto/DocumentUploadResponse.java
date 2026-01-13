package com.gdldv.user.dto;

public class DocumentUploadResponse {
    private String documentType;
    private String fileUrl;
    private String message;

    public DocumentUploadResponse() {}

    public DocumentUploadResponse(String documentType, String fileUrl, String message) {
        this.documentType = documentType;
        this.fileUrl = fileUrl;
        this.message = message;
    }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
