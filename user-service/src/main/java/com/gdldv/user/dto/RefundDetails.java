package com.gdldv.user.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundDetails {
    private Long reservationId;
    private Double originalAmount;
    private RefundPolicy refundPolicy;
    private Double refundPercentage;
    private Double refundAmount;
    private String stripeRefundId;
    private String status;
    private LocalDateTime processedAt;
}
