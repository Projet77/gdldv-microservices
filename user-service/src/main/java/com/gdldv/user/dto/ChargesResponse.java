package com.gdldv.user.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChargesResponse {
    private Long checkInId;
    private Double totalCharges;
    private String description;
}
