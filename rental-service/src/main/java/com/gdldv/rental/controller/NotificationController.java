package com.gdldv.rental.controller;

import com.gdldv.rental.dto.ApiResponse;
import com.gdldv.rental.dto.EmailNotification;
import com.gdldv.rental.entity.Rental;
import com.gdldv.rental.repository.RentalRepository;
import com.gdldv.rental.service.FinancialReportService;
import com.gdldv.rental.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * GDLDV-548: Contrôleur pour la gestion des notifications
 * Endpoints pour envoyer des emails et gérer les notifications
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notifications", description = "Gestion des notifications par email")
public class NotificationController {

    private final NotificationService notificationService;
    private final RentalRepository rentalRepository;
    private final FinancialReportService financialReportService;

    /**
     * GDLDV-548: Envoyer une notification personnalisée
     */
    @PostMapping("/send")
    @Operation(summary = "Envoyer une notification", description = "Envoie une notification email personnalisée")
    public ResponseEntity<ApiResponse<EmailNotification>> sendNotification(@RequestBody EmailNotification notification) {
        log.info("Envoi d'une notification personnalisée à: {}", notification.getTo());

        EmailNotification result = notificationService.sendEmail(notification);

        if ("SENT".equals(result.getStatus())) {
            return ResponseEntity.ok(ApiResponse.<EmailNotification>builder()
                    .success(true)
                    .message("Notification envoyée avec succès")
                    .data(result)
                    .build());
        } else {
            return ResponseEntity.internalServerError().body(ApiResponse.<EmailNotification>builder()
                    .success(false)
                    .message("Erreur lors de l'envoi: " + result.getErrorMessage())
                    .data(result)
                    .build());
        }
    }

    /**
     * GDLDV-548: Envoyer une confirmation de réservation
     */
    @PostMapping("/booking-confirmation/{rentalId}")
    @Operation(summary = "Confirmation de réservation", description = "Envoie la confirmation de réservation au client")
    public ResponseEntity<ApiResponse<String>> sendBookingConfirmation(@PathVariable Long rentalId) {
        log.info("Envoi de la confirmation pour la réservation #{}", rentalId);

        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Location non trouvée"));

        notificationService.sendBookingConfirmation(rental.getUserId(), rental);

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Confirmation de réservation envoyée")
                .data("Email envoyé à l'utilisateur #" + rental.getUserId())
                .build());
    }

    /**
     * GDLDV-548: Envoyer un rappel de début de location
     */
    @PostMapping("/rental-start-reminder/{rentalId}")
    @Operation(summary = "Rappel de début", description = "Envoie un rappel de début de location")
    public ResponseEntity<ApiResponse<String>> sendRentalStartReminder(@PathVariable Long rentalId) {
        log.info("Envoi du rappel de début pour la location #{}", rentalId);

        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Location non trouvée"));

        notificationService.sendRentalStartReminder(rental.getUserId(), rental);

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Rappel de début envoyé")
                .data("Email envoyé à l'utilisateur #" + rental.getUserId())
                .build());
    }

    /**
     * GDLDV-548: Envoyer un rappel de fin de location
     */
    @PostMapping("/rental-end-reminder/{rentalId}")
    @Operation(summary = "Rappel de fin", description = "Envoie un rappel de fin de location")
    public ResponseEntity<ApiResponse<String>> sendRentalEndReminder(@PathVariable Long rentalId) {
        log.info("Envoi du rappel de fin pour la location #{}", rentalId);

        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Location non trouvée"));

        notificationService.sendRentalEndReminder(rental.getUserId(), rental);

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Rappel de fin envoyé")
                .data("Email envoyé à l'utilisateur #" + rental.getUserId())
                .build());
    }

    /**
     * GDLDV-548: Envoyer une alerte de retard
     */
    @PostMapping("/overdue-alert/{rentalId}")
    @Operation(summary = "Alerte de retard", description = "Envoie une alerte pour une location en retard")
    public ResponseEntity<ApiResponse<String>> sendOverdueAlert(@PathVariable Long rentalId) {
        log.info("Envoi de l'alerte de retard pour la location #{}", rentalId);

        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Location non trouvée"));

        notificationService.sendOverdueAlert(rental.getUserId(), rental);

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Alerte de retard envoyée")
                .data("Email envoyé au client et à l'administrateur")
                .build());
    }

    /**
     * GDLDV-548: Traiter toutes les alertes de retard automatiquement
     */
    @PostMapping("/process-overdue-alerts")
    @Operation(summary = "Traiter les retards", description = "Envoie des alertes pour toutes les locations en retard")
    public ResponseEntity<ApiResponse<Integer>> processOverdueAlerts() {
        log.info("Traitement des alertes de retard");

        LocalDateTime now = LocalDateTime.now();
        List<Rental> overdueRentals = rentalRepository.findOverdueRentals(now);

        int alertsSent = 0;
        for (Rental rental : overdueRentals) {
            try {
                notificationService.sendOverdueAlert(rental.getUserId(), rental);
                alertsSent++;
            } catch (Exception e) {
                log.error("Erreur lors de l'envoi de l'alerte pour la location #{}: {}",
                        rental.getId(), e.getMessage());
            }
        }

        return ResponseEntity.ok(ApiResponse.<Integer>builder()
                .success(true)
                .message(String.format("%d alertes de retard envoyées", alertsSent))
                .data(alertsSent)
                .build());
    }

    /**
     * GDLDV-548: Envoyer le rapport financier mensuel par email
     */
    @PostMapping("/monthly-report")
    @Operation(summary = "Rapport mensuel", description = "Génère et envoie le rapport financier mensuel par email")
    public ResponseEntity<ApiResponse<String>> sendMonthlyReport() {
        log.info("Envoi du rapport financier mensuel");

        try {
            // Générer le rapport du mois précédent
            YearMonth lastMonth = YearMonth.now().minusMonths(1);
            LocalDateTime startOfMonth = lastMonth.atDay(1).atStartOfDay();
            LocalDateTime endOfMonth = lastMonth.atEndOfMonth().atTime(23, 59, 59);

            var report = financialReportService.generateFinancialReport(startOfMonth, endOfMonth, "MONTHLY");
            byte[] pdfBytes = financialReportService.generatePdfReport(report);

            String reportMonth = lastMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
            notificationService.sendMonthlyFinancialReport(pdfBytes, reportMonth);

            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("Rapport financier mensuel envoyé")
                    .data("Rapport pour " + reportMonth + " envoyé à l'administrateur")
                    .build());

        } catch (Exception e) {
            log.error("Erreur lors de l'envoi du rapport mensuel: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(ApiResponse.<String>builder()
                    .success(false)
                    .message("Erreur lors de l'envoi: " + e.getMessage())
                    .build());
        }
    }

    /**
     * GDLDV-548: Envoyer les rappels pour les locations qui commencent demain
     */
    @PostMapping("/send-tomorrow-reminders")
    @Operation(summary = "Rappels de demain", description = "Envoie les rappels pour les locations qui commencent demain")
    public ResponseEntity<ApiResponse<Integer>> sendTomorrowReminders() {
        log.info("Envoi des rappels pour les locations de demain");

        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfTomorrow = tomorrow.withHour(23).withMinute(59).withSecond(59);

        List<Rental> tomorrowRentals = rentalRepository.findByCreatedAtBetween(tomorrow, endOfTomorrow);

        int remindersSent = 0;
        for (Rental rental : tomorrowRentals) {
            try {
                notificationService.sendRentalStartReminder(rental.getUserId(), rental);
                remindersSent++;
            } catch (Exception e) {
                log.error("Erreur lors de l'envoi du rappel pour la location #{}: {}",
                        rental.getId(), e.getMessage());
            }
        }

        return ResponseEntity.ok(ApiResponse.<Integer>builder()
                .success(true)
                .message(String.format("%d rappels envoyés", remindersSent))
                .data(remindersSent)
                .build());
    }
}
