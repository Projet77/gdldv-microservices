package com.gdldv.rental.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalDTO {

    private Long id;
    private Long reservationId;
    private Long userId;
    private Long vehicleId;
    private Long employeeId;

    // Dates
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime actualStartDate;
    private LocalDateTime actualEndDate;

    // Lieux
    private String pickupLocation;
    private String returnLocation;

    // Prix
    private BigDecimal basePrice;
    private BigDecimal additionalCharges;
    private BigDecimal totalPrice;
    private BigDecimal deposit;

    // État
    private String status;

    // Kilométrage
    private Integer startKilometers;
    private Integer endKilometers;

    // Carburant
    private String startFuelLevel;
    private String endFuelLevel;

    // Notes
    private String checkoutNotes;
    private String checkinNotes;

    // Informations enrichies (optionnel)
    private String clientName;
    private String vehicleInfo;
    private String employeeName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}