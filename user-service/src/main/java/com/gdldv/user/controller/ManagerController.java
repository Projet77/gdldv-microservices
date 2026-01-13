package com.gdldv.user.controller;

import com.gdldv.user.dto.ManagerDashboard;
import com.gdldv.user.service.ManagerDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller pour le dashboard MANAGER
 * API pour les managers (KPIs, équipe, incidents)
 */
@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
@Slf4j

@Tag(name = "Manager Dashboard", description = "API du tableau de bord manager")
public class ManagerController {

    private final ManagerDashboardService managerDashboardService;

    /**
     * Récupérer le tableau de bord manager
     * GET /api/manager/dashboard
     */
    @GetMapping("/dashboard")
    @Operation(summary = "Tableau de bord manager", description = "Obtenir toutes les informations du dashboard manager (KPIs, équipe, tendances, etc.)")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<ManagerDashboard> getDashboard() {
        log.info("Fetching manager dashboard");

        try {
            ManagerDashboard dashboard = managerDashboardService.getManagerDashboard();
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            log.error("Error fetching manager dashboard: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupérer les KPIs du jour
     * GET /api/manager/kpis
     */
    @GetMapping("/kpis")
    @Operation(summary = "KPIs du jour", description = "Obtenir les indicateurs clés de performance du jour")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getKPIs() {
        log.info("Fetching manager KPIs");

        try {
            ManagerDashboard dashboard = managerDashboardService.getManagerDashboard();

            return ResponseEntity.ok(Map.of(
                    "todayRevenue", dashboard.getTodayRevenue(),
                    "todayRevenueTarget", dashboard.getTodayRevenueTarget(),
                    "todayRentals", dashboard.getTodayRentals(),
                    "todayRentalsTarget", dashboard.getTodayRentalsTarget(),
                    "fleetUtilizationRate", dashboard.getFleetUtilizationRate(),
                    "fleetUtilizationTarget", dashboard.getFleetUtilizationTarget(),
                    "customerSatisfaction", dashboard.getCustomerSatisfaction(),
                    "customerSatisfactionTarget", dashboard.getCustomerSatisfactionTarget(),
                    "lateReturnRate", dashboard.getLateReturnRate(),
                    "lateReturnTarget", dashboard.getLateReturnTarget()));
        } catch (Exception e) {
            log.error("Error fetching KPIs: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupérer la performance de l'équipe
     * GET /api/manager/staff-performance
     */
    @GetMapping("/staff-performance")
    @Operation(summary = "Performance de l'équipe", description = "Obtenir les statistiques de performance du staff")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getStaffPerformance() {
        log.info("Fetching staff performance");

        try {
            ManagerDashboard dashboard = managerDashboardService.getManagerDashboard();
            return ResponseEntity.ok(dashboard.getStaffPerformance());
        } catch (Exception e) {
            log.error("Error fetching staff performance: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupérer les incidents critiques
     * GET /api/manager/critical-incidents
     */
    @GetMapping("/critical-incidents")
    @Operation(summary = "Incidents critiques", description = "Obtenir la liste des incidents critiques")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getCriticalIncidents() {
        log.info("Fetching critical incidents");

        try {
            ManagerDashboard dashboard = managerDashboardService.getManagerDashboard();
            return ResponseEntity.ok(dashboard.getCriticalIncidents());
        } catch (Exception e) {
            log.error("Error fetching critical incidents: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupérer les tendances
     * GET /api/manager/trends
     */
    @GetMapping("/trends")
    @Operation(summary = "Tendances", description = "Obtenir les tendances de revenus, locations et satisfaction (30 derniers jours)")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getTrends() {
        log.info("Fetching trends");

        try {
            ManagerDashboard dashboard = managerDashboardService.getManagerDashboard();

            return ResponseEntity.ok(Map.of(
                    "revenueTrend", dashboard.getRevenueTrend(),
                    "rentalsTrend", dashboard.getRentalsTrend(),
                    "satisfactionTrend", dashboard.getSatisfactionTrend()));
        } catch (Exception e) {
            log.error("Error fetching trends: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupérer les top performers
     * GET /api/manager/top-performers
     */
    @GetMapping("/top-performers")
    @Operation(summary = "Top performers", description = "Obtenir les meilleurs véhicules et clients")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getTopPerformers() {
        log.info("Fetching top performers");

        try {
            ManagerDashboard dashboard = managerDashboardService.getManagerDashboard();

            return ResponseEntity.ok(Map.of(
                    "topVehicles", dashboard.getTopVehicles(),
                    "topClients", dashboard.getTopClients()));
        } catch (Exception e) {
            log.error("Error fetching top performers: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupérer l'état de la flotte
     * GET /api/manager/fleet-status
     */
    @GetMapping("/fleet-status")
    @Operation(summary = "État de la flotte", description = "Obtenir le statut de la flotte de véhicules")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getFleetStatus() {
        log.info("Fetching fleet status");

        try {
            ManagerDashboard dashboard = managerDashboardService.getManagerDashboard();

            return ResponseEntity.ok(Map.of(
                    "totalVehicles", dashboard.getTotalVehicles(),
                    "availableVehicles", dashboard.getAvailableVehicles(),
                    "rentedVehicles", dashboard.getRentedVehicles(),
                    "maintenanceVehicles", dashboard.getMaintenanceVehicles(),
                    "outOfServiceVehicles", dashboard.getOutOfServiceVehicles()));
        } catch (Exception e) {
            log.error("Error fetching fleet status: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
