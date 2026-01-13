package com.gdldv.user.dto;

import com.gdldv.user.entity.User;
import java.util.stream.Collectors;

public class UserProfileResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String drivingLicenseNumber;
    private String drivingLicenseCountry;
    private String drivingLicenseExpiryDate; // Assuming String for simplicity, can be Date/LocalDate
    private String address;
    private String city;
    private String postalCode;
    private String country;
    private String roles; // Assuming roles are represented as a single string for display
    private Boolean active;
    private Boolean emailVerified;
    private String lastLoginAt; // Assuming String for simplicity, can be LocalDateTime

    private String documentsUploadedAt;
    private String identityCardUrl;
    private String passportUrl;
    private String driverLicensePhotoUrl;
    private String documentVerificationStatus;
    private String profileImage;

    public UserProfileResponse() {
    }

    public UserProfileResponse(Long id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public UserProfileResponse(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.drivingLicenseNumber = user.getDrivingLicenseNumber();
        this.drivingLicenseCountry = user.getDrivingLicenseCountry();
        this.drivingLicenseExpiryDate = user.getDrivingLicenseExpiryDate() != null
                ? user.getDrivingLicenseExpiryDate().toString()
                : null;
        this.address = user.getAddress();
        this.city = user.getCity();
        this.postalCode = user.getPostalCode();
        this.country = user.getCountry();
        this.roles = user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.joining(", "));
        this.active = user.getActive();
        this.emailVerified = user.getEmailVerified();
        this.lastLoginAt = user.getLastLoginAt() != null ? user.getLastLoginAt().toString() : null;
        this.profileImage = user.getProfileImage();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDrivingLicenseNumber() {
        return drivingLicenseNumber;
    }

    public void setDrivingLicenseNumber(String drivingLicenseNumber) {
        this.drivingLicenseNumber = drivingLicenseNumber;
    }

    public String getDrivingLicenseCountry() {
        return drivingLicenseCountry;
    }

    public void setDrivingLicenseCountry(String drivingLicenseCountry) {
        this.drivingLicenseCountry = drivingLicenseCountry;
    }

    public String getDrivingLicenseExpiryDate() {
        return drivingLicenseExpiryDate;
    }

    public void setDrivingLicenseExpiryDate(String drivingLicenseExpiryDate) {
        this.drivingLicenseExpiryDate = drivingLicenseExpiryDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(String lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public String getDocumentsUploadedAt() {
        return documentsUploadedAt;
    }

    public void setDocumentsUploadedAt(String documentsUploadedAt) {
        this.documentsUploadedAt = documentsUploadedAt;
    }

    public String getIdentityCardUrl() {
        return identityCardUrl;
    }

    public void setIdentityCardUrl(String identityCardUrl) {
        this.identityCardUrl = identityCardUrl;
    }

    public String getPassportUrl() {
        return passportUrl;
    }

    public void setPassportUrl(String passportUrl) {
        this.passportUrl = passportUrl;
    }

    public String getDriverLicensePhotoUrl() {
        return driverLicensePhotoUrl;
    }

    public void setDriverLicensePhotoUrl(String driverLicensePhotoUrl) {
        this.driverLicensePhotoUrl = driverLicensePhotoUrl;
    }

    public String getDocumentVerificationStatus() {
        return documentVerificationStatus;
    }

    public void setDocumentVerificationStatus(String documentVerificationStatus) {
        this.documentVerificationStatus = documentVerificationStatus;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
