package com.gdldv.user.entity;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "vehicle_inspections")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleInspection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long checkOutId;

    // État du véhicule (details)
    private Boolean paintIssues;        // Rayures, dents, peinture
    private Boolean bumperDamage;       // Pare-chocs endommagé
    private Boolean windowDamage;       // Vitres cassées/fissurées
    private Boolean tireDamage;         // Pneus endommagés
    private Boolean seatDamage;         // Sièges endommagés

    @Column(length = 1000)
    private String generalNotes;        // Notes générales

    @Enumerated(EnumType.STRING)
    private VehicleConditionGrade grade; // A+, A, B, C

    @ElementCollection
    private List<String> photoUrls;

    private Boolean clientConfirmed;    // Client a confirmé l'état
}
