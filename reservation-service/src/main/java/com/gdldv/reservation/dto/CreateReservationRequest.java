package com.gdldv.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gdldv.reservation.entity.ReservationOption;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReservationRequest {

    @NotNull(message = "L'ID du véhicule est obligatoire")
    private Long vehicleId;

    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    private Long userId;

    @NotNull(message = "La date de début est obligatoire")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;

    @NotNull(message = "La date de fin est obligatoire")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;

    private List<ReservationOption> options;  // GPS, Siège bébé, Assurance

    private String notes;
}
