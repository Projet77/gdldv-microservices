package com.gdldv.user.controller;

import com.gdldv.user.dto.ClientDashboard;
import com.gdldv.user.service.ClientDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller pour le dashboard CLIENT
 * API pour les clients finaux
 */
@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
@Slf4j

@Tag(name = "Client Dashboard", description = "API du tableau de bord client")
public class ClientController {

    private final ClientDashboardService clientDashboardService;

    /**
     * Récupérer le tableau de bord client
     * GET /api/client/dashboard
     */
    @GetMapping("/dashboard")
    @Operation(summary = "Tableau de bord client", description = "Obtenir toutes les informations du dashboard client (réservations, favoris, historique, etc.)")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<ClientDashboard> getDashboard(@RequestParam Long userId) {
        log.info("Fetching client dashboard for user: {}", userId);

        try {
            ClientDashboard dashboard = clientDashboardService.getClientDashboard(userId);
            return ResponseEntity.ok(dashboard);
        } catch (IllegalArgumentException e) {
            log.error("User not found: {}", userId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error fetching client dashboard: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupérer les réservations actives du client
     * GET /api/client/active-rentals
     */
    @GetMapping("/active-rentals")
    @Operation(summary = "Réservations actives", description = "Obtenir les réservations en cours du client")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<?> getActiveRentals(@RequestParam Long userId) {
        log.info("Fetching active rentals for user: {}", userId);

        try {
            ClientDashboard dashboard = clientDashboardService.getClientDashboard(userId);
            return ResponseEntity.ok(dashboard.getCurrentRentals());
        } catch (Exception e) {
            log.error("Error fetching active rentals: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupérer l'historique des locations
     * GET /api/client/rental-history
     */
    @GetMapping("/rental-history")
    @Operation(summary = "Historique des locations", description = "Obtenir l'historique complet des locations du client")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<?> getRentalHistory(@RequestParam Long userId) {
        log.info("Fetching rental history for user: {}", userId);

        try {
            ClientDashboard dashboard = clientDashboardService.getClientDashboard(userId);
            return ResponseEntity.ok(dashboard.getRecentHistory());
        } catch (Exception e) {
            log.error("Error fetching rental history: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupérer les véhicules favoris
     * GET /api/client/favorites
     */
    @GetMapping("/favorites")
    @Operation(summary = "Véhicules favoris", description = "Obtenir la liste des véhicules favoris du client")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<?> getFavorites(@RequestParam Long userId) {
        log.info("Fetching favorites for user: {}", userId);

        try {
            ClientDashboard dashboard = clientDashboardService.getClientDashboard(userId);
            return ResponseEntity.ok(dashboard.getFavorites());
        } catch (Exception e) {
            log.error("Error fetching favorites: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupérer les statistiques personnelles
     * GET /api/client/statistics
     */
    @GetMapping("/statistics")
    @Operation(summary = "Statistiques personnelles", description = "Obtenir les statistiques du client (total dépensé, nombre de locations, etc.)")
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    public ResponseEntity<?> getStatistics(@RequestParam Long userId) {
        log.info("Fetching statistics for user: {}", userId);

        try {
            ClientDashboard dashboard = clientDashboardService.getClientDashboard(userId);

            // Retourner uniquement les statistiques
            return ResponseEntity.ok(Map.of(
                    "totalRentals", dashboard.getTotalRentals(),
                    "totalSpent", dashboard.getTotalSpent(),
                    "averageSpentPerRental", dashboard.getAverageSpentPerRental(),
                    "averageDuration", dashboard.getAverageDuration(),
                    "favoriteCategory", dashboard.getFavoriteCategory(),
                    "membershipBadge", dashboard.getMembershipBadge()));
        } catch (Exception e) {
            log.error("Error fetching statistics: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
