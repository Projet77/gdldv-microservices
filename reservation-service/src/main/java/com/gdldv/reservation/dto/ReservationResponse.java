package com.gdldv.reservation.dto;

import com.gdldv.reservation.entity.ReservationOption;
import com.gdldv.reservation.entity.ReservationStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponse {

    private Long id;
    private String confirmationNumber;
    private Long vehicleId;
    private Long userId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Double totalPrice;
    private ReservationStatus status;
    private List<ReservationOption> options;
    private String stripePaymentIntentId;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Informations du véhicule (optionnel)
    private VehicleDTO vehicle;
}
