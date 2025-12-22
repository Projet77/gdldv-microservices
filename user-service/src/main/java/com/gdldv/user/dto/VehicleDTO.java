package com.gdldv.user.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {
    private Long id;
    private String make;
    private String model;
    private int year;
    private String licensePlate;
    private String category;
    private double pricePerDay;
    private String status;
    private int mileage;
    private String nextMaintenance;
    private String insuranceStatus;
}
