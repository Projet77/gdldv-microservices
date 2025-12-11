package com.gdldv.reservation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "L'ID du véhicule est obligatoire")
    @Column(nullable = false)
    private Long vehicleId;

    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    @Column(nullable = false)
    private Long userId;

    @NotNull(message = "La date de début est obligatoire")
    @Future(message = "La date de début doit être dans le futur")
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull(message = "La date de fin est obligatoire")
    @Future(message = "La date de fin doit être dans le futur")
    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.PENDING;

    @Column(unique = true)
    private String stripePaymentIntentId;

    @Column(length = 500)
    private String notes;
}
