package com.gdldv.vehicle.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GDLDV-510: DTO pour créer un avis
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReviewRequest {

    @NotNull(message = "L'ID du véhicule est obligatoire")
    private Long vehicleId;

    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    private Long userId;

    @NotNull(message = "La note est obligatoire")
    @Min(value = 1, message = "La note doit être entre 1 et 5")
    @Max(value = 5, message = "La note doit être entre 1 et 5")
    private Integer rating;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 200, message = "Le titre ne doit pas dépasser 200 caractères")
    private String title;

    @NotBlank(message = "Le commentaire est obligatoire")
    @Size(min = 10, max = 1000, message = "Le commentaire doit contenir entre 10 et 1000 caractères")
    private String comment;

    // Catégories de notation (optionnelles)
    @Min(value = 1, message = "La note de propreté doit être entre 1 et 5")
    @Max(value = 5, message = "La note de propreté doit être entre 1 et 5")
    private Integer cleanliness;

    @Min(value = 1, message = "La note d'état doit être entre 1 et 5")
    @Max(value = 5, message = "La note d'état doit être entre 1 et 5")
    private Integer condition;

    @Min(value = 1, message = "La note de confort doit être entre 1 et 5")
    @Max(value = 5, message = "La note de confort doit être entre 1 et 5")
    private Integer comfort;

    @Min(value = 1, message = "La note de maniabilité doit être entre 1 et 5")
    @Max(value = 5, message = "La note de maniabilité doit être entre 1 et 5")
    private Integer drivability;

    // Métadonnées (optionnelles)
    private Long rentalDays;
    private Long mileageDriven;
}
