package com.gdldv.user.controller;

import com.gdldv.user.dto.SuperAdminDashboard;
import com.gdldv.user.service.SuperAdminDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller pour le dashboard SUPER_ADMIN
 * API pour l'administration système
 */
@RestController
@RequestMapping("/api/super-admin")
@RequiredArgsConstructor
@Slf4j

@Tag(name = "Super Admin Dashboard", description = "API du tableau de bord super administrateur")
public class SuperAdminController {

    private final SuperAdminDashboardService superAdminDashboardService;

    /**
     * Récupérer le tableau de bord super admin
     * GET /api/super-admin/dashboard
     */
    @GetMapping("/dashboard")
    @Operation(summary = "Tableau de bord super admin", description = "Obtenir toutes les informations du dashboard super admin (santé système, audit, sécurité, etc.)")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<SuperAdminDashboard> getDashboard() {
        log.info("Fetching super admin dashboard");

        try {
            SuperAdminDashboard dashboard = superAdminDashboardService.getSuperAdminDashboard();
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            log.error("Error fetching super admin dashboard: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupérer la santé du système
     * GET /api/super-admin/system-health
     */
    @GetMapping("/system-health")
    @Operation(summary = "Santé du système", description = "Obtenir l'état de santé de tous les services")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getSystemHealth() {
        log.info("Fetching system health");

        try {
            SuperAdminDashboard dashboard = superAdminDashboardService.getSuperAdminDashboard();
            return ResponseEntity.ok(dashboard.getSystemHealth());
        } catch (Exception e) {
            log.error("Error fetching system health: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupérer les statistiques utilisateurs
     * GET /api/super-admin/user-statistics
     */
    @GetMapping("/user-statistics")
    @Operation(summary = "Statistiques utilisateurs", description = "Obtenir les statistiques des utilisateurs par rôle")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getUserStatistics() {
        log.info("Fetching user statistics");

        try {
            SuperAdminDashboard dashboard = superAdminDashboardService.getSuperAdminDashboard();
            return ResponseEntity.ok(dashboard.getUserStatistics());
        } catch (Exception e) {
            log.error("Error fetching user statistics: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupérer les statistiques de la base de données
     * GET /api/super-admin/database-statistics
     */
    @GetMapping("/database-statistics")
    @Operation(summary = "Statistiques base de données", description = "Obtenir les statistiques de la base de données (tables, espace disque, etc.)")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getDatabaseStatistics() {
        log.info("Fetching database statistics");

        try {
            SuperAdminDashboard dashboard = superAdminDashboardService.getSuperAdminDashboard();
            return ResponseEntity.ok(dashboard.getDatabaseStatistics());
        } catch (Exception e) {
            log.error("Error fetching database statistics: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupérer les statistiques d'audit
     * GET /api/super-admin/audit-statistics
     */
    @GetMapping("/audit-statistics")
    @Operation(summary = "Statistiques d'audit", description = "Obtenir les logs d'audit et alertes de sécurité")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getAuditStatistics() {
        log.info("Fetching audit statistics");

        try {
            SuperAdminDashboard dashboard = superAdminDashboardService.getSuperAdminDashboard();
            return ResponseEntity.ok(dashboard.getAuditStatistics());
        } catch (Exception e) {
            log.error("Error fetching audit statistics: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupérer les logs d'audit (Paginé)
     * GET /api/super-admin/audit-logs
     */
    @GetMapping("/audit-logs")
    @Operation(summary = "Logs d'audit paginés", description = "Obtenir la liste paginée des logs d'audit")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "timestamp,desc") String sort) {
        log.info("Fetching audit logs page={}, size={}", page, size);

        try {
            // Pour l'instant, on renvoie une liste vide paginée si le service n'a pas la
            // méthode spécifique
            // Ou on extrait depuis les stats si elles sont complètes (ce qui n'est pas
            // idéal pour la pagination réelle)
            // Mais pour fixer le 404, on va exposer l'endpoint.

            // TODO: Implémenter une vraie pagination en DB via le service
            // En attendant, on retourne ce qu'on a dans le
            // dashboard.auditStatistics.recentLogs
            SuperAdminDashboard dashboard = superAdminDashboardService.getSuperAdminDashboard();
            java.util.List<?> logs = dashboard.getAuditStatistics().getRecentLogs();

            // Simulation simple de pagination sur la liste en mémoire
            int start = Math.min(page * size, logs.size());
            int end = Math.min((page + 1) * size, logs.size());
            java.util.List<?> pagedLogs = logs.subList(start, end);

            org.springframework.data.domain.Page<?> pageResult = new org.springframework.data.domain.PageImpl<>(
                    pagedLogs,
                    org.springframework.data.domain.PageRequest.of(page, size),
                    logs.size());

            return ResponseEntity.ok(pageResult);
        } catch (Exception e) {
            log.error("Error fetching audit logs: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupérer la configuration système
     * GET /api/super-admin/system-configuration
     */
    @GetMapping("/system-configuration")
    @Operation(summary = "Configuration système", description = "Obtenir la configuration actuelle du système")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getSystemConfiguration() {
        log.info("Fetching system configuration");

        try {
            SuperAdminDashboard dashboard = superAdminDashboardService.getSuperAdminDashboard();
            return ResponseEntity.ok(dashboard.getSystemConfiguration());
        } catch (Exception e) {
            log.error("Error fetching system configuration: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupérer les métriques de sécurité
     * GET /api/super-admin/security-metrics
     */
    @GetMapping("/security-metrics")
    @Operation(summary = "Métriques de sécurité", description = "Obtenir les métriques de sécurité (tentatives échouées, activités suspectes, etc.)")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getSecurityMetrics() {
        log.info("Fetching security metrics");

        try {
            SuperAdminDashboard dashboard = superAdminDashboardService.getSuperAdminDashboard();
            return ResponseEntity.ok(dashboard.getSecurityMetrics());
        } catch (Exception e) {
            log.error("Error fetching security metrics: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupérer les métriques de performance
     * GET /api/super-admin/performance-metrics
     */
    @GetMapping("/performance-metrics")
    @Operation(summary = "Métriques de performance", description = "Obtenir les métriques de performance système (CPU, mémoire, requêtes, etc.)")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getPerformanceMetrics() {
        log.info("Fetching performance metrics");

        try {
            SuperAdminDashboard dashboard = superAdminDashboardService.getSuperAdminDashboard();
            return ResponseEntity.ok(dashboard.getPerformanceMetrics());
        } catch (Exception e) {
            log.error("Error fetching performance metrics: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
