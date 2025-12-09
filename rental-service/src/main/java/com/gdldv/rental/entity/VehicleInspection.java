package com.gdldv.rental.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_inspections")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleInspection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "rental_id", nullable = false)
    private Rental rental;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InspectionType type; // CHECK_OUT ou CHECK_IN

    @Column(nullable = false)
    private Long inspectedBy; // ID de l'employé

    @Column(nullable = false)
    private LocalDateTime inspectionDate;

    // État général
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VehicleCondition overallCondition;

    // Checklist détaillée
    @Column(nullable = false)
    private Boolean exteriorClean = true;

    @Column(nullable = false)
    private Boolean interiorClean = true;

    @Column(nullable = false)
    private Boolean tiresCondition = true;

    @Column(nullable = false)
    private Boolean lightsWorking = true;

    @Column(nullable = false)
    private Boolean wipersFunctional = true;

    @Column(nullable = false)
    private Boolean spareWheelPresent = true;

    @Column(nullable = false)
    private Boolean documentsPresent = true; // Papiers du véhicule

    @Column(nullable = false)
    private Boolean firstAidKitPresent = true;

    @Column(nullable = false)
    private Boolean warningTrianglePresent = true;

    // Dommages constatés
    @Column(columnDefinition = "TEXT")
    private String damagesDescription; // Description des dommages

    @Column(columnDefinition = "TEXT")
    private String photoUrls; // URLs des photos séparées par des virgules

    // Notes supplémentaires
    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}