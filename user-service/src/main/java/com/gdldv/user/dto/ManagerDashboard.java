package com.gdldv.user.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO pour le dashboard MANAGER
 * Vue exécutive avec KPIs et analytics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerDashboard {
    // KPIs du jour
    private Double todayRevenue;
    private Double todayRevenueTarget;
    private Integer todayRentals;
    private Integer todayRentalsTarget;
    private Double fleetUtilizationRate;
    private Double fleetUtilizationTarget;
    private Double customerSatisfaction;
    private Double customerSatisfactionTarget;
    private Double lateReturnRate;
    private Double lateReturnTarget;

    // Comparaisons
    private Double revenueVsYesterday; // pourcentage
    private Double rentalsVsYesterday;
    private Double satisfactionVsLastMonth;

    // Performance mensuelle
    private Double monthlyRevenue;
    private Double monthlyRevenueTarget;
    private Integer monthlyRentals;
    private Integer monthlyRentalsTarget;
    private Double monthlyProgress; // pourcentage du mois écoulé

    // Équipe
    private List<StaffPerformance> staffPerformance;
    private Integer activeStaffToday;
    private Integer totalStaff;

    // Incidents et problèmes
    private List<IncidentInfo> criticalIncidents;
    private List<IncidentInfo> importantIssues;
    private Integer totalIncidentsThisMonth;

    // Tendances (derniers 30 jours)
    private List<DailyTrend> revenueTrend;
    private List<DailyTrend> rentalsTrend;
    private List<DailyTrend> satisfactionTrend;

    // Top performers
    private List<TopVehicle> topVehicles;
    private List<TopClient> topClients;

    // Flotte
    private Integer totalVehicles;
    private Integer availableVehicles;
    private Integer rentedVehicles;
    private Integer maintenanceVehicles;
    private Integer outOfServiceVehicles;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StaffPerformance {
        private Long staffId;
        private String name;
        private String role;
        private LocalDateTime shiftStart;
        private LocalDateTime shiftEnd;
        private Integer transactionsToday;
        private Double satisfactionRating;
        private String status; // ACTIVE, BREAK, OFFLINE
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class IncidentInfo {
        private Long incidentId;
        private String type; // ACCIDENT, DAMAGE, COMPLAINT, DELAY
        private String description;
        private String severity; // CRITICAL, HIGH, MEDIUM, LOW
        private Long reservationId;
        private String clientName;
        private String vehicleName;
        private LocalDateTime reportedAt;
        private String status; // OPEN, INVESTIGATING, RESOLVED
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailyTrend {
        private LocalDateTime date;
        private Double value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TopVehicle {
        private Long vehicleId;
        private String name;
        private Integer rentalsCount;
        private Double revenue;
        private Double averageRating;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TopClient {
        private Long clientId;
        private String name;
        private Integer rentalsCount;
        private Double totalSpent;
        private Double averageRating;
    }
}
