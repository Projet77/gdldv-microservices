package com.gdldv.reservation.dto;

import com.gdldv.reservation.entity.ReservationOption;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingRequest {

    @NotNull(message = "L'ID du véhicule est obligatoire")
    private Long vehicleId;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDateTime startDate;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDateTime endDate;

    private List<ReservationOption> options;  // Options supplémentaires
}
