package com.gdldv.rental.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rentals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Références aux autres microservices
    @Column(nullable = false)
    private Long reservationId; // Référence à reservation-service

    @Column(nullable = false)
    private Long userId; // Référence à user-service

    @Column(nullable = false)
    private Long vehicleId; // Référence à vehicle-service

    @Column(nullable = false)
    private Long employeeId; // Employé qui traite le check-out/check-in

    // Dates de location
    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column
    private LocalDateTime actualStartDate; // Date réelle de prise

    @Column
    private LocalDateTime actualEndDate; // Date réelle de retour

    // Lieux
    @Column(nullable = false, length = 255)
    private String pickupLocation;

    @Column(nullable = false, length = 255)
    private String returnLocation;

    // Prix
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal additionalCharges = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal deposit; // Caution

    // État de la location
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RentalStatus status = RentalStatus.PENDING;

    // Kilométrage
    @Column
    private Integer startKilometers;

    @Column
    private Integer endKilometers;

    // Carburant
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private FuelLevel startFuelLevel;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private FuelLevel endFuelLevel;

    // Notes
    @Column(columnDefinition = "TEXT")
    private String checkoutNotes;

    @Column(columnDefinition = "TEXT")
    private String checkinNotes;

    // Timestamps
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}