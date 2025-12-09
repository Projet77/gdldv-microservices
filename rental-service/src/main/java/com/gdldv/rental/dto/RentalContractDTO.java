package com.gdldv.rental.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalContractDTO {

    private Long id;
    private String contractNumber;
    private Long rentalId;

    // Informations client (depuis user-service)
    private String clientName;
    private String clientEmail;
    private String clientPhone;
    private String drivingLicenseNumber;

    // Informations véhicule (depuis vehicle-service)
    private String vehicleBrand;
    private String vehicleModel;
    private String vehiclePlate;

    // Dates et lieux
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String pickupLocation;
    private String returnLocation;

    // Prix
    private BigDecimal basePrice;
    private BigDecimal deposit;
    private BigDecimal totalPrice;

    // Contrat
    private String contractContent;
    private String termsAndConditions;
    private Boolean isSigned;
    private LocalDateTime signedAt;
    private String pdfUrl;

    private LocalDateTime createdAt;
}
