package com.gdldv.vehicle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateVehicleRequest {

    @NotBlank(message = "La marque est obligatoire")
    private String brand;

    @NotBlank(message = "Le modèle est obligatoire")
    private String model;

    @NotBlank(message = "L'immatriculation est obligatoire")
    private String licensePlate;

    private String color;

    private Integer year;

    @Positive(message = "Le kilométrage doit être positif")
    private Long mileage;

    @NotNull(message = "Le prix par jour est obligatoire")
    @Positive(message = "Le prix par jour doit être positif")
    private Double dailyPrice;

    private String category; // SUV, Berline, Monospace, etc.

    private String fuelType; // ESSENCE, DIESEL, ELECTRIQUE, HYBRIDE

    private String transmission; // MANUELLE, AUTOMATIQUE

    private String description;
}
