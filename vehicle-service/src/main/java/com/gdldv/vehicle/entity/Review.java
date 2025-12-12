package com.gdldv.vehicle.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * GDLDV-510: Entité pour les avis et notations des véhicules
 */
@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "L'ID du véhicule est obligatoire")
    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;

    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull(message = "La note est obligatoire")
    @Min(value = 1, message = "La note doit être entre 1 et 5")
    @Max(value = 5, message = "La note doit être entre 1 et 5")
    @Column(nullable = false)
    private Integer rating;  // Note globale 1-5 étoiles

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 200, message = "Le titre ne doit pas dépasser 200 caractères")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Le commentaire est obligatoire")
    @Size(min = 10, max = 1000, message = "Le commentaire doit contenir entre 10 et 1000 caractères")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment;

    // Catégories de notation détaillées
    @Min(value = 1, message = "La note de propreté doit être entre 1 et 5")
    @Max(value = 5, message = "La note de propreté doit être entre 1 et 5")
    private Integer cleanliness;    // Propreté

    @Min(value = 1, message = "La note d'état doit être entre 1 et 5")
    @Max(value = 5, message = "La note d'état doit être entre 1 et 5")
    private Integer condition;      // État général du véhicule

    @Min(value = 1, message = "La note de confort doit être entre 1 et 5")
    @Max(value = 5, message = "La note de confort doit être entre 1 et 5")
    private Integer comfort;        // Confort

    @Min(value = 1, message = "La note de maniabilité doit être entre 1 et 5")
    @Max(value = 5, message = "La note de maniabilité doit être entre 1 et 5")
    private Integer drivability;    // Maniabilité / conduite

    // Métadonnées de la location
    @Column(name = "rental_days")
    private Long rentalDays;        // Durée de la location pour ce client

    @Column(name = "mileage_driven")
    private Long mileageDriven;     // Kilométrage parcouru pendant la location

    // Compteurs d'utilité
    @Column(name = "helpful_count", nullable = false)
    private Integer helpfulCount;   // Nombre de "utile"

    @Column(name = "not_helpful_count", nullable = false)
    private Integer notHelpfulCount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (helpfulCount == null) {
            helpfulCount = 0;
        }
        if (notHelpfulCount == null) {
            notHelpfulCount = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
