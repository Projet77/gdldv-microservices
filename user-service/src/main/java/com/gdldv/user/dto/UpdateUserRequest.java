package com.gdldv.user.dto;

import java.time.LocalDate;
import java.util.Set;

public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private String city;
    private String postalCode;
    private String country;
    private String drivingLicenseNumber;
    private String drivingLicenseCountry;
    private LocalDate drivingLicenseExpiryDate;
    private Boolean active;
    private Boolean emailVerified;
    private Set<String> roles;

    public UpdateUserRequest() {}

    // Getters and Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getDrivingLicenseNumber() { return drivingLicenseNumber; }
    public void setDrivingLicenseNumber(String drivingLicenseNumber) { this.drivingLicenseNumber = drivingLicenseNumber; }

    public String getDrivingLicenseCountry() { return drivingLicenseCountry; }
    public void setDrivingLicenseCountry(String drivingLicenseCountry) { this.drivingLicenseCountry = drivingLicenseCountry; }

    public LocalDate getDrivingLicenseExpiryDate() { return drivingLicenseExpiryDate; }
    public void setDrivingLicenseExpiryDate(LocalDate drivingLicenseExpiryDate) { this.drivingLicenseExpiryDate = drivingLicenseExpiryDate; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public Boolean getEmailVerified() { return emailVerified; }
    public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}
