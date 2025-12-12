package com.gdldv.vehicle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * GDLDV-510: DTO pour la réponse d'un avis
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {

    private Long id;
    private Long vehicleId;
    private Long userId;
    private Integer rating;
    private String title;
    private String comment;

    // Catégories de notation
    private Integer cleanliness;
    private Integer condition;
    private Integer comfort;
    private Integer drivability;

    // Métadonnées
    private Long rentalDays;
    private Long mileageDriven;

    // Compteurs
    private Integer helpfulCount;
    private Integer notHelpfulCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
