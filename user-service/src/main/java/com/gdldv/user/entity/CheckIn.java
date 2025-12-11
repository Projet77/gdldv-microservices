package com.gdldv.user.entity;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "check_ins")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckIn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long reservationId;

    @NotNull
    private Long vehicleId;

    @NotNull
    private Long userId;

    // État final du véhicule
    private Long mileageAtCheckIn;      // Kilométrage au retour
    private String fuelLevelAtCheckIn;  // Niveau carburant

    @Column(length = 1000)
    private String vehicleConditionDescription;

    @ElementCollection
    private List<String> photoUrls;

    // Frais supplémentaires
    private Double additionalCharges;   // Total des frais (km, essence, dommages, retard)

    // Signature
    @Column(length = 10000)
    private String clientSignature;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
