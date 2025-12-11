package com.gdldv.user.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckInReportDTO {
    private Long checkInId;
    private Long reservationId;
    private Long vehicleId;
    private Long mileageDriven;
    private Double fuelUsed;
    private DamageReport damageReport;
    private Double additionalCharges;
    private List<String> photos;
    private String inspectionGrade;
    private LocalDateTime checkInTime;
}
