package com.gdldv.reservation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String confirmationNumber;  // CONF-XXXXX

    @NotNull(message = "L'ID du véhicule est obligatoire")
    @Column(nullable = false)
    private Long vehicleId;

    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    @Column(nullable = false)
    private Long userId;

    @NotNull(message = "La date de début est obligatoire")
    @Column(nullable = false)
    private LocalDateTime startDate;

    @NotNull(message = "La date de fin est obligatoire")
    @Column(nullable = false)
    private LocalDateTime endDate;

    @Positive
    @Column(nullable = false)
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;   // PENDING, CONFIRMED, ACTIVE, COMPLETED, CANCELLED

    // Options additionnelles
    @ElementCollection
    private List<ReservationOption> options;

    // Stripe payment
    private String stripePaymentIntentId;

    @Column(length = 500)
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = ReservationStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
