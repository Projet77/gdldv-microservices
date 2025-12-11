package com.gdldv.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdvancedCheckInRequest {
    @NotNull
    private Long reservationId;

    @NotNull
    private Long vehicleId;

    @NotNull
    private Long mileage;

    @NotNull
    private String fuelLevel;

    private String conditionDescription;
    private List<String> photoUrls;

    // Damage details
    private Boolean paintDamage;
    private Boolean bumperDamage;
    private Boolean windowDamage;
    private Boolean tireDamage;
    private Boolean seatDamage;
    private String generalNotes;
    private Double estimatedRepairCost;
}
