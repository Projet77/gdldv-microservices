package com.gdldv.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {
    private Long id;
    private String brand;
    private String model;
    private String licensePlate;
    private Long mileage;
    private Double dailyPrice;
    private String category;
    private String status;
}
