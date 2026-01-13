package com.gdldv.user.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UrgentTask {
    private String type;
    private Long reservationId;
    private String description;
    private String priority; // HIGH, MEDIUM, LOW
}
