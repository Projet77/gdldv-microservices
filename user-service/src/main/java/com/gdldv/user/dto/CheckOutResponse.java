package com.gdldv.user.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckOutResponse {
    private Long id;
    private Long reservationId;
    private Long vehicleId;
    private Long mileageAtCheckOut;
    private String fuelLevelAtCheckOut;
    private String vehicleConditionDescription;
    private List<String> photoUrls;
    private LocalDateTime createdAt;
}
