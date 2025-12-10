package com.gdldv.user.entity;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "check_outs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckOut {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long reservationId;

    @NotNull
    private Long vehicleId;

    @NotNull
    private Long userId;

    // État initial du véhicule
    private Long mileageAtCheckOut;     // Kilométrage à la prise en charge
    private String fuelLevelAtCheckOut; // Niveau carburant (Full, 3/4, 1/2, 1/4, Empty)

    // Documentation
    @Column(length = 1000)
    private String vehicleConditionDescription; // État général du véhicule

    @ElementCollection
    private List<String> photoUrls;    // Photos de l'état initial

    // Signature
    @Column(length = 10000)
    private String clientSignature;    // Base64 encoded signature

    // Timestamps
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
