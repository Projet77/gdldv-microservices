package com.gdldv.user.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckInResponse {
    private Long id;
    private Long reservationId;
    private Long mileageAtCheckIn;
    private String fuelLevelAtCheckIn;
    private String vehicleConditionDescription;
    private Double additionalCharges;
    private List<String> photoUrls;
    private LocalDateTime createdAt;
}
