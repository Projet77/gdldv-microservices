package com.gdldv.rental.dto;

import com.gdldv.rental.entity.InspectionType;
import com.gdldv.rental.entity.VehicleCondition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleInspectionDTO {

    private Long id;
    private InspectionType inspectionType;
    private VehicleCondition overallCondition;

    // Checklist
    private Boolean exteriorClean;
    private Boolean interiorClean;
    private Boolean tiresCondition;
    private Boolean lightsWorking;
    private Boolean wipersFunctional;
    private Boolean spareWheelPresent;
    private Boolean documentsPresent;
    private Boolean firstAidKitPresent;
    private Boolean warningTrianglePresent;

    // Dommages
    private String damagesDescription;
    private String photoUrls; // URLs séparées par des virgules

    private String notes;
    private LocalDateTime inspectionDate;
}
