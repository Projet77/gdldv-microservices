package com.gdldv.rental.dto;

import com.gdldv.rental.entity.FuelLevel;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckInRequest {

    @NotNull(message = "L'ID de location est obligatoire")
    private Long rentalId;

    @NotNull(message = "L'ID de l'employé est obligatoire")
    private Long employeeId;

    @NotNull(message = "Le kilométrage final est obligatoire")
    private Integer endKilometers;

    @NotNull(message = "Le niveau de carburant est obligatoire")
    private FuelLevel endFuelLevel;

    private String checkinNotes;

    // Inspection du véhicule au retour
    private VehicleInspectionDTO inspection;
}
