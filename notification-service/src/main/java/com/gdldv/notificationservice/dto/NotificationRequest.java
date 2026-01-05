package com.gdldv.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequest {
    private String recipient;
    private String subject;
    private String message;
    private String type; // EMAIL, SMS, PUSH
}
