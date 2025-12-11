package com.gdldv.user.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundApprovalRequest {
    private Long reservationId;
    private String reason;
    private LocalDateTime requestedAt;
    private String status;
    private String adminComments;
    private LocalDateTime approvedAt;
}
