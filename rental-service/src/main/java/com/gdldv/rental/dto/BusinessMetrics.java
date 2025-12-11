package com.gdldv.rental.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * GDLDV-533: KPI Dashboard - DTO pour les métriques métier
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessMetrics {

    // Période de calcul
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;

    // KPIs financiers
    private Double totalRevenue;
    private Double averageBookingValue;
    private Map<String, Double> revenueByPeriod; // Ex: {"2024-12": 150000, "2024-11": 120000}

    // KPIs opérationnels
    private Integer totalBookings;
    private Integer completedBookings;
    private Integer cancelledBookings;
    private Double cancellationRate; // Pourcentage

    // KPIs flotte
    private Double fleetUtilization; // Pourcentage d'utilisation de la flotte
    private Integer totalVehicles;
    private Integer activeVehicles;
    private List<TopVehicle> topVehicles; // Top 5 véhicules les plus loués

    // KPIs clients
    private Integer totalCustomers;
    private Integer newCustomers;
    private Integer repeatCustomers;
    private Double repeatCustomerRate; // Pourcentage de clients récurrents

    // Tendances
    private Double revenueGrowth; // Croissance du revenu par rapport à la période précédente (%)
    private Double bookingsGrowth; // Croissance des réservations (%)

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopVehicle {
        private Long vehicleId;
        private String brand;
        private String model;
        private Integer bookingCount;
        private Double totalRevenue;
    }
}
