package com.gdldv.vehicle.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "vehicles")
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

    public Vehicle() {
    }

    public Vehicle(Long id, String brand, String model, String licensePlate, Long mileage, Double dailyPrice, String category, String status) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.licensePlate = licensePlate;
        this.mileage = mileage;
        this.dailyPrice = dailyPrice;
        this.category = category;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public Long getMileage() {
        return mileage;
    }

    public void setMileage(Long mileage) {
        this.mileage = mileage;
    }

    public Double getDailyPrice() {
        return dailyPrice;
    }

    public void setDailyPrice(Double dailyPrice) {
        this.dailyPrice = dailyPrice;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
