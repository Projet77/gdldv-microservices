package com.gdldv.rental.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * GDLDV-548: DTO pour les notifications par email
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailNotification {

    private String to;
    private String cc;
    private String subject;
    private String templateName; // NOM du template (BOOKING_CONFIRMATION, RENTAL_REMINDER, etc.)
    private Map<String, Object> templateVariables; // Variables pour le template
    private String priority; // HIGH, NORMAL, LOW
    private LocalDateTime scheduledFor; // Pour les notifications planifiées
    private boolean sendImmediately;

    // Statut d'envoi
    private String status; // PENDING, SENT, FAILED
    private String errorMessage;
    private LocalDateTime sentAt;
}
