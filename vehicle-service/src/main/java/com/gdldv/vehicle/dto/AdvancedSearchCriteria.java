package com.gdldv.vehicle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GDLDV-520: DTO pour les critères de recherche avancée
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdvancedSearchCriteria {

    // Filtres de base
    private String category;        // SUV, Berline, Monospace, etc.
    private Double minPrice;
    private Double maxPrice;
    private String fuelType;        // ESSENCE, DIESEL, ELECTRIQUE, HYBRIDE
    private String transmission;    // MANUELLE, AUTOMATIQUE

    // Filtres avancés
    private Double minRating;       // Note minimale (1-5)
    private Integer minYear;        // Année minimale
    private Integer maxYear;        // Année maximale
    private Long maxMileage;        // Kilométrage maximal

    // Équipements
    private String[] amenities;     // GPS, Climatisation, Bluetooth, etc.

    // Disponibilité
    private Boolean available;      // Uniquement les véhicules disponibles

    // Tri
    private String sortBy;          // price, rating, year, mileage
    private String sortOrder;       // asc, desc
}
