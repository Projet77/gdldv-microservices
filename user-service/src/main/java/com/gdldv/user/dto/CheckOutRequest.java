package com.gdldv.user.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckOutRequest {
    @NotNull
    private Long reservationId;

    @NotNull
    private Long vehicleId;

    @Positive
    private Long mileage;

    @NotBlank
    private String fuelLevel;  // Full, 3/4, 1/2, 1/4, Empty

    @NotBlank
    private String vehicleCondition;

    private List<String> photoUrls;

    @NotBlank
    private String clientSignature;  // Base64
}
