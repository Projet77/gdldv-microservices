package com.gdldv.user.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour le dashboard AGENT
 * Contient les informations pour les opérations de check-in/check-out
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentDashboard {
    // Informations agent
    private Long agentId;
    private String agentName;
    private LocalDateTime shiftStart;
    private LocalDateTime shiftEnd;

    // Statistiques du jour
    private Integer todayCheckOuts;
    private Integer todayCheckIns;
    private Integer pendingCheckOuts;
    private Integer pendingCheckIns;
    private Double todayRevenue;
    private Double averageProcessingTime; // en minutes

    // File d'attente
    private List<PendingCheckOutInfo> pendingCheckOutQueue;
    private List<PendingCheckInInfo> pendingCheckInQueue;

    // Réservations du jour
    private List<TodayReservationInfo> todayReservations;

    // Check-outs/ins complétés
    private List<CompletedTransactionInfo> completedToday;

    // Véhicules disponibles
    private List<AvailableVehicleInfo> availableVehicles;

    // Alertes
    private List<AgentAlert> alerts;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PendingCheckOutInfo {
        private Long reservationId;
        private String confirmationNumber;
        private String clientName;
        private String clientPhone;
        private Long vehicleId;
        private String vehicleName;
        private LocalDateTime scheduledTime;
        private String status; // CONFIRMED, PENDING_PAYMENT
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PendingCheckInInfo {
        private Long reservationId;
        private String confirmationNumber;
        private String clientName;
        private Long vehicleId;
        private String vehicleName;
        private LocalDateTime expectedReturnTime;
        private Boolean isLate;
        private Integer minutesLate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TodayReservationInfo {
        private Long reservationId;
        private String confirmationNumber;
        private String clientName;
        private String vehicleName;
        private LocalDateTime scheduledTime;
        private String type; // CHECK_OUT, CHECK_IN
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CompletedTransactionInfo {
        private Long transactionId;
        private String confirmationNumber;
        private String clientName;
        private String vehicleName;
        private LocalDateTime completedTime;
        private String type; // CHECK_OUT, CHECK_IN
        private Double amount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AvailableVehicleInfo {
        private Long vehicleId;
        private String name;
        private String category;
        private String status; // AVAILABLE, RESERVED
        private LocalDateTime nextReservation;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AgentAlert {
        private String type; // LATE_RETURN, MISSED_CHECKIN, PAYMENT_ISSUE
        private String message;
        private Long reservationId;
        private String priority; // HIGH, MEDIUM, LOW
        private LocalDateTime createdAt;
    }
}
