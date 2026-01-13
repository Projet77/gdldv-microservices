package com.gdldv.user.dto;

import java.time.LocalDateTime;

public class UserDocumentsResponse {
    private Long userId;
    private String identityCardUrl;
    private String passportUrl;
    private String driverLicensePhotoUrl;
    private String documentVerificationStatus;
    private LocalDateTime documentsUploadedAt;
    private boolean hasIdentityCard;
    private boolean hasPassport;
    private boolean hasDriverLicense;

    public UserDocumentsResponse() {}

    // Builder pattern
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UserDocumentsResponse response = new UserDocumentsResponse();
        
        public Builder userId(Long userId) { response.userId = userId; return this; }
        public Builder identityCardUrl(String url) { response.identityCardUrl = url; return this; }
        public Builder passportUrl(String url) { response.passportUrl = url; return this; }
        public Builder driverLicensePhotoUrl(String url) { response.driverLicensePhotoUrl = url; return this; }
        public Builder documentVerificationStatus(String status) { response.documentVerificationStatus = status; return this; }
        public Builder documentsUploadedAt(LocalDateTime time) { response.documentsUploadedAt = time; return this; }
        public Builder hasIdentityCard(boolean has) { response.hasIdentityCard = has; return this; }
        public Builder hasPassport(boolean has) { response.hasPassport = has; return this; }
        public Builder hasDriverLicense(boolean has) { response.hasDriverLicense = has; return this; }
        
        public UserDocumentsResponse build() { return response; }
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getIdentityCardUrl() { return identityCardUrl; }
    public void setIdentityCardUrl(String identityCardUrl) { this.identityCardUrl = identityCardUrl; }
    public String getPassportUrl() { return passportUrl; }
    public void setPassportUrl(String passportUrl) { this.passportUrl = passportUrl; }
    public String getDriverLicensePhotoUrl() { return driverLicensePhotoUrl; }
    public void setDriverLicensePhotoUrl(String driverLicensePhotoUrl) { this.driverLicensePhotoUrl = driverLicensePhotoUrl; }
    public String getDocumentVerificationStatus() { return documentVerificationStatus; }
    public void setDocumentVerificationStatus(String documentVerificationStatus) { this.documentVerificationStatus = documentVerificationStatus; }
    public LocalDateTime getDocumentsUploadedAt() { return documentsUploadedAt; }
    public void setDocumentsUploadedAt(LocalDateTime documentsUploadedAt) { this.documentsUploadedAt = documentsUploadedAt; }
    public boolean isHasIdentityCard() { return hasIdentityCard; }
    public void setHasIdentityCard(boolean hasIdentityCard) { this.hasIdentityCard = hasIdentityCard; }
    public boolean isHasPassport() { return hasPassport; }
    public void setHasPassport(boolean hasPassport) { this.hasPassport = hasPassport; }
    public boolean isHasDriverLicense() { return hasDriverLicense; }
    public void setHasDriverLicense(boolean hasDriverLicense) { this.hasDriverLicense = hasDriverLicense; }
}
