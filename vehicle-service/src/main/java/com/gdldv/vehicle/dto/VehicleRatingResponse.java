package com.gdldv.vehicle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GDLDV-510: DTO pour la note moyenne d'un véhicule
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleRatingResponse {

    private Double averageRating;
    private Integer reviewCount;
}
