package com.gdldv.reservation.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleDTO {
    private Long id;
    private String brand;
    private String model;
    private String licensePlate;
    private Long mileage;
    private Double dailyPrice;
    private String category;
    private String status;
    private Boolean isActive;
}
