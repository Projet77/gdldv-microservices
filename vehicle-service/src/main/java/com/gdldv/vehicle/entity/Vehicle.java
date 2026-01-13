package com.gdldv.vehicle.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La marque est obligatoire")
    @Column(nullable = false)
    private String brand;

    @NotBlank(message = "Le modèle est obligatoire")
    @Column(nullable = false)
    private String model;

    @NotBlank(message = "L'immatriculation est obligatoire")
    @Column(unique = true, nullable = false)
    private String licensePlate;

    private String color;

    private Integer year;

    @Positive(message = "Le kilométrage doit être positif")
    private Long mileage;

    @Positive(message = "Le prix par jour doit être positif")
    @Column(nullable = false)
    private Double dailyPrice;

    private String category; // SUV, Berline, Monospace, etc.

    private String fuelType; // ESSENCE, DIESEL, ELECTRIQUE, HYBRIDE

    private String transmission; // MANUELLE, AUTOMATIQUE

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status;

    private Integer seats;

    private Boolean babySeat;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "vehicle_images", joinColumns = @JoinColumn(name = "vehicle_id"))
    @Column(name = "image_url", columnDefinition = "LONGTEXT")
    @Builder.Default
    private List<String> images = new ArrayList<>();

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.isActive == null) {
            this.isActive = true;
        }
        if (this.status == null) {
            this.status = VehicleStatus.AVAILABLE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
