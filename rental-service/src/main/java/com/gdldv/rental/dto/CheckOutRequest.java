package com.gdldv.rental.dto;

import com.gdldv.rental.entity.FuelLevel;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckOutRequest {

    @NotNull(message = "L'ID de réservation est obligatoire")
    private Long reservationId;

    @NotNull(message = "L'ID de l'employé est obligatoire")
    private Long employeeId;

    @NotNull(message = "Le kilométrage de départ est obligatoire")
    private Integer startKilometers;

    @NotNull(message = "Le niveau de carburant est obligatoire")
    private FuelLevel startFuelLevel;

    @NotNull(message = "Le montant de la caution est obligatoire")
    private BigDecimal deposit;

    private String checkoutNotes;

    // Inspection du véhicule
    private VehicleInspectionDTO inspection;
}