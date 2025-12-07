package com.gdldv.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class RegisterRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String firstName;

    @NotBlank
    @Size(min = 3, max = 20)
    private String lastName;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    private String phoneNumber;
    private String address;
    private String city;
    private String postalCode;
    private String country;
    private String drivingLicenseNumber;
    private String drivingLicenseCountry;
    private LocalDate drivingLicenseExpiryDate;

    public RegisterRequest() {
    }

    public RegisterRequest(String firstName, String lastName, String email, String password, String phoneNumber, String address, String city, String postalCode, String country, String drivingLicenseNumber, String drivingLicenseCountry, LocalDate drivingLicenseExpiryDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
        this.drivingLicenseNumber = drivingLicenseNumber;
        this.drivingLicenseCountry = drivingLicenseCountry;
        this.drivingLicenseExpiryDate = drivingLicenseExpiryDate;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public LocalDate getDrivingLicenseExpiryDate() {
        return drivingLicenseExpiryDate;
    }

    public void setDrivingLicenseExpiryDate(LocalDate drivingLicenseExpiryDate) {
        this.drivingLicenseExpiryDate = drivingLicenseExpiryDate;
    }
}
