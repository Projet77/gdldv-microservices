package com.gdldv.vehicle.dto;

import com.gdldv.vehicle.entity.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponse {

    private Long id;
    private String brand;
    private String model;
    private String licensePlate;
    private String color;
    private Integer year;
    private Long mileage;
    private Double dailyPrice;
    private String category;
    private String fuelType;
    private String transmission;
    private String description;
    private VehicleStatus status;
    private VehicleStatus status;
    private Integer seats;
    private Boolean babySeat;
    private List<String> images;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
