package com.gdldv.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InspectionRequest {
    @NotNull
    private Long checkOutId;

    private Boolean paintIssues;
    private Boolean bumperDamage;
    private Boolean windowDamage;
    private Boolean tireDamage;
    private Boolean seatDamage;

    private String generalNotes;
    private List<String> photoUrls;
}
