package com.gdldv.vehicle.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @Positive(message = "Le kilométrage doit être positif")
    private Long mileage;

    @Positive(message = "Le prix par jour doit être positif")
    @Column(nullable = false)
    private Double dailyPrice;

    private String category; // SUV, Berline, Monospace, etc.

    @Column(columnDefinition = "VARCHAR(255) DEFAULT 'AVAILABLE'")
    private String status; // AVAILABLE, RENTED, MAINTENANCE
}
