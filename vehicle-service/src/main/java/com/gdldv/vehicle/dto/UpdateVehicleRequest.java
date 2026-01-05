package com.gdldv.vehicle.dto;

import java.util.List;

import com.gdldv.vehicle.entity.VehicleStatus;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateVehicleRequest {

    private String brand;
    private String model;
    private String licensePlate;
    private String color;
    private Integer year;

    @Positive(message = "Le kilométrage doit être positif")
    private Long mileage;

    @Positive(message = "Le prix par jour doit être positif")
    private Double dailyPrice;

    private String category;
    private String fuelType;
    private String transmission;
    private String description;
    private VehicleStatus status;

    private Integer seats;

    private Boolean babySeat;

    private List<String> images;
}
