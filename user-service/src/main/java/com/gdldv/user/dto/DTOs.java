package com.gdldv.user.dto;

import com.gdldv.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// ========== AUTHENTICATION DTOs ==========

@Data
@NoArgsConstructor
@AllArgsConstructor
class RegisterRequest {
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50)
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50)
    private String lastName;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String password;

    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$")
    private String phoneNumber;

    @NotBlank(message = "Le numéro de permis est obligatoire")
    private String drivingLicenseNumber;

    private String drivingLicenseCountry;
    private LocalDateTime drivingLicenseExpiryDate;
    private String address;
    private String city;
    private String postalCode;
    private String country;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class LoginRequest {
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private User.UserRole role;

    public AuthResponse(String token, Long id, String email, String firstName, String lastName, User.UserRole role) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }
}

// ========== USER PROFILE DTOs ==========

@Data
@NoArgsConstructor
@AllArgsConstructor
class UserProfileResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String drivingLicenseNumber;
    private String drivingLicenseCountry;
    private LocalDateTime drivingLicenseExpiryDate;
    private String address;
    private String city;
    private String postalCode;
    private String country;
    private User.UserRole role;
    private Boolean active;
    private Boolean emailVerified;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    // Constructeur depuis User entity
    public UserProfileResponse(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.drivingLicenseNumber = user.getDrivingLicenseNumber();
        this.drivingLicenseCountry = user.getDrivingLicenseCountry();
        this.drivingLicenseExpiryDate = user.getDrivingLicenseExpiryDate();
        this.address = user.getAddress();
        this.city = user.getCity();
        this.postalCode = user.getPostalCode();
        this.country = user.getCountry();
        this.role = user.getRole();
        this.active = user.getActive();
        this.emailVerified = user.getEmailVerified();
        this.createdAt = user.getCreatedAt();
        this.lastLoginAt = user.getLastLoginAt();
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class UpdateProfileRequest {
    @Size(min = 2, max = 50)
    private String firstName;

    @Size(min = 2, max = 50)
    private String lastName;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$")
    private String phoneNumber;

    private String address;
    private String city;
    private String postalCode;
    private String country;
}

// ========== RESPONSE WRAPPER ==========

@Data
@NoArgsConstructor
@AllArgsConstructor
class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}