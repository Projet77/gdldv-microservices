package com.gdldv.user.controller;

import com.gdldv.user.dto.AdminDashboard;
import com.gdldv.user.service.AdminDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * GDLDV-595: Admin Controller (Sprint 3)
 * API pour le tableau de bord administrateur
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Tag(name = "Admin", description = "API d'administration")
public class AdminController {

    private final AdminDashboardService adminDashboardService;

    /**
     * Récupérer le tableau de bord admin
     * GET /api/admin/dashboard
     */
    @GetMapping("/dashboard")
    @Operation(summary = "Tableau de bord administrateur",
            description = "Obtenir les statistiques et tâches urgentes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<AdminDashboard> getDashboard() {
        log.info("Fetching admin dashboard");

        try {
            AdminDashboard dashboard = adminDashboardService.getAdminDashboard();
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            log.error("Error fetching admin dashboard: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Approuver un refund
     * POST /api/admin/approve-refund/{reservationId}
     */
    @PostMapping("/approve-refund/{reservationId}")
    @Operation(summary = "Approuver un remboursement",
            description = "Approuver manuellement un remboursement en attente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> approveRefund(@PathVariable Long reservationId) {
        log.info("Approving refund for reservation: {}", reservationId);

        try {
            adminDashboardService.approveRefund(reservationId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Error approving refund: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error approving refund: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Envoyer un rappel au client
     * POST /api/admin/send-reminder/{reservationId}
     */
    @PostMapping("/send-reminder/{reservationId}")
    @Operation(summary = "Envoyer un rappel au client",
            description = "Envoyer un email/SMS de rappel au client pour une réservation")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<Void> sendReminderToCustomer(@PathVariable Long reservationId) {
        log.info("Sending reminder to customer for reservation: {}", reservationId);

        try {
            adminDashboardService.sendReminderToCustomer(reservationId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Error sending reminder: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error sending reminder: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
