package com.gdldv.user.controller;

import com.gdldv.user.dto.AgentDashboard;
import com.gdldv.user.service.AgentDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller pour le dashboard AGENT
 * API pour les agents de location (check-in/check-out)
 */
@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
@Slf4j

@Tag(name = "Agent Dashboard", description = "API du tableau de bord agent")
public class AgentController {

    private final AgentDashboardService agentDashboardService;

    /**
     * Récupérer le tableau de bord agent
     * GET /api/agent/dashboard
     */
    @GetMapping("/dashboard")
    @Operation(summary = "Tableau de bord agent", description = "Obtenir toutes les informations du dashboard agent (check-in, check-out, alertes, etc.)")
    @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<AgentDashboard> getDashboard(@RequestParam Long agentId) {
        log.info("Fetching agent dashboard for agent: {}", agentId);

        try {
            AgentDashboard dashboard = agentDashboardService.getAgentDashboard(agentId);
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            log.error("Error fetching agent dashboard: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupérer la file d'attente check-out
     * GET /api/agent/pending-checkouts
     */
    @GetMapping("/pending-checkouts")
    @Operation(summary = "File d'attente check-out", description = "Obtenir les check-out en attente")
    @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<?> getPendingCheckouts(@RequestParam Long agentId) {
        log.info("Fetching pending checkouts for agent: {}", agentId);

        try {
            AgentDashboard dashboard = agentDashboardService.getAgentDashboard(agentId);
            return ResponseEntity.ok(dashboard.getPendingCheckOutQueue());
        } catch (Exception e) {
            log.error("Error fetching pending checkouts: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupérer la file d'attente check-in
     * GET /api/agent/pending-checkins
     */
    @GetMapping("/pending-checkins")
    @Operation(summary = "File d'attente check-in", description = "Obtenir les check-in en attente")
    @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<?> getPendingCheckins(@RequestParam Long agentId) {
        log.info("Fetching pending checkins for agent: {}", agentId);

        try {
            AgentDashboard dashboard = agentDashboardService.getAgentDashboard(agentId);
            return ResponseEntity.ok(dashboard.getPendingCheckInQueue());
        } catch (Exception e) {
            log.error("Error fetching pending checkins: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupérer les alertes
     * GET /api/agent/alerts
     */
    @GetMapping("/alerts")
    @Operation(summary = "Alertes agent", description = "Obtenir les alertes (retards, check-in manqués, etc.)")
    @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<?> getAlerts(@RequestParam Long agentId) {
        log.info("Fetching alerts for agent: {}", agentId);

        try {
            AgentDashboard dashboard = agentDashboardService.getAgentDashboard(agentId);
            return ResponseEntity.ok(dashboard.getAlerts());
        } catch (Exception e) {
            log.error("Error fetching alerts: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupérer les réservations du jour
     * GET /api/agent/today-reservations
     */
    @GetMapping("/today-reservations")
    @Operation(summary = "Réservations du jour", description = "Obtenir toutes les réservations prévues aujourd'hui")
    @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<?> getTodayReservations(@RequestParam Long agentId) {
        log.info("Fetching today reservations for agent: {}", agentId);

        try {
            AgentDashboard dashboard = agentDashboardService.getAgentDashboard(agentId);
            return ResponseEntity.ok(dashboard.getTodayReservations());
        } catch (Exception e) {
            log.error("Error fetching today reservations: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupérer les transactions complétées
     * GET /api/agent/completed-today
     */
    @GetMapping("/completed-today")
    @Operation(summary = "Transactions complétées", description = "Obtenir les check-in/out complétés aujourd'hui")
    @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<?> getCompletedToday(@RequestParam Long agentId) {
        log.info("Fetching completed transactions for agent: {}", agentId);

        try {
            AgentDashboard dashboard = agentDashboardService.getAgentDashboard(agentId);
            return ResponseEntity.ok(dashboard.getCompletedToday());
        } catch (Exception e) {
            log.error("Error fetching completed transactions: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
