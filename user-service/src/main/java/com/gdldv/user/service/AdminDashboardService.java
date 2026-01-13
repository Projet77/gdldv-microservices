package com.gdldv.user.service;

import com.gdldv.user.client.ReservationClient;
import com.gdldv.user.dto.AdminDashboard;
import com.gdldv.user.dto.UrgentTask;
import com.gdldv.user.entity.Reservation;
import com.gdldv.user.entity.ReservationStatus;
import com.gdldv.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GDLDV-595: Admin Dashboard Service (Sprint 3)
 * Service pour le tableau de bord administrateur
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardService {

    private final ReservationClient reservationClient;
    private final UserRepository userRepository;

    public AdminDashboard getAdminDashboard() {
        log.info("Fetching admin dashboard data via Feign Client");

        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime tomorrow = today.plus(1, ChronoUnit.DAYS);

        // Récupérer toutes les réservations via Feign Client
        List<Reservation> allReservations = reservationClient.getAllReservations();

        // Réservations créées aujourd'hui (filtrer en Java)
        List<Reservation> todayReservations = allReservations.stream()
                .filter(r -> r.getCreatedAt() != null &&
                           r.getCreatedAt().isAfter(today) &&
                           r.getCreatedAt().isBefore(tomorrow))
                .collect(Collectors.toList());

        // Réservations actives (en cours) via Feign Client
        List<Reservation> activeReservations = reservationClient
                .getReservationsByStatus(ReservationStatus.ACTIVE);

        // Réservations à confirmer (paiement pending) via Feign Client
        List<Reservation> pendingReservations = reservationClient
                .getReservationsByStatus(ReservationStatus.PENDING);

        // Nombre total d'utilisateurs
        long totalUsers = userRepository.count();

        // Revenus du jour
        double todayRevenue = calculateTodayRevenue(todayReservations);

        // Tâches urgentes
        List<UrgentTask> urgentTasks = findUrgentTasks();

        AdminDashboard dashboard = AdminDashboard.builder()
                .todayReservations(todayReservations.size())
                .activeRentals(activeReservations.size())
                .pendingPayments(pendingReservations.size())
                .totalUsers(totalUsers)
                .todayRevenue(todayRevenue)
                .urgentTasks(urgentTasks)
                .build();

        log.info("Dashboard stats - Today: {}, Active: {}, Pending: {}, Users: {}",
                todayReservations.size(), activeReservations.size(),
                pendingReservations.size(), totalUsers);

        return dashboard;
    }

    private double calculateTodayRevenue(List<Reservation> reservations) {
        return reservations.stream()
                .filter(r -> ReservationStatus.COMPLETED.equals(r.getStatus()) ||
                           ReservationStatus.CONFIRMED.equals(r.getStatus()))
                .mapToDouble(Reservation::getTotalPrice)
                .sum();
    }

    private List<UrgentTask> findUrgentTasks() {
        List<UrgentTask> tasks = new ArrayList<>();

        LocalDateTime yesterday = LocalDateTime.now().minus(1, ChronoUnit.DAYS);
        LocalDateTime soon = LocalDateTime.now().plus(2, ChronoUnit.HOURS);
        LocalDateTime oneHourAgo = LocalDateTime.now().minus(1, ChronoUnit.HOURS);

        // Récupérer les réservations via Feign Client
        List<Reservation> pendingReservations = reservationClient
                .getReservationsByStatus(ReservationStatus.PENDING);
        List<Reservation> activeReservations = reservationClient
                .getReservationsByStatus(ReservationStatus.ACTIVE);
        List<Reservation> confirmedReservations = reservationClient
                .getReservationsByStatus(ReservationStatus.CONFIRMED);

        // Vérifier les réservations en attente depuis plus de 24h
        List<Reservation> oldPendingReservations = pendingReservations.stream()
                .filter(r -> r.getCreatedAt() != null && r.getCreatedAt().isBefore(yesterday))
                .collect(Collectors.toList());

        oldPendingReservations.forEach(r -> {
            tasks.add(UrgentTask.builder()
                    .type("OLD_PENDING_RESERVATION")
                    .reservationId(r.getId())
                    .description("Réservation en attente depuis >24h: " + r.getConfirmationNumber())
                    .priority("HIGH")
                    .build());
        });

        // Vérifier les locations actives proches de la date de fin
        List<Reservation> endingSoonReservations = activeReservations.stream()
                .filter(r -> r.getEndDate() != null && r.getEndDate().isBefore(soon))
                .collect(Collectors.toList());

        endingSoonReservations.forEach(r -> {
            tasks.add(UrgentTask.builder()
                    .type("ENDING_SOON")
                    .reservationId(r.getId())
                    .description("Location se termine bientôt: " + r.getConfirmationNumber())
                    .priority("MEDIUM")
                    .build());
        });

        // Vérifier les check-ins manqués (réservation débutée mais pas active)
        List<Reservation> missedCheckIns = confirmedReservations.stream()
                .filter(r -> r.getStartDate() != null && r.getStartDate().isBefore(oneHourAgo))
                .collect(Collectors.toList());

        missedCheckIns.forEach(r -> {
            tasks.add(UrgentTask.builder()
                    .type("MISSED_CHECKIN")
                    .reservationId(r.getId())
                    .description("Prise en charge non effectuée: " + r.getConfirmationNumber())
                    .priority("HIGH")
                    .build());
        });

        log.info("Found {} urgent tasks", tasks.size());
        return tasks;
    }

    public void approveRefund(Long reservationId) {
        log.info("Approving refund for reservation: {}", reservationId);
        try {
            Reservation reservation = reservationClient.getReservationById(reservationId);
            if (reservation == null) {
                throw new IllegalArgumentException("Reservation not found: " + reservationId);
            }
            // TODO: Traiter le refund via RefundService
            log.info("Refund approved for reservation: {}", reservationId);
        } catch (Exception e) {
            log.error("Error approving refund: {}", e.getMessage());
            throw new IllegalArgumentException("Reservation not found: " + reservationId);
        }
    }

    public void sendReminderToCustomer(Long reservationId) {
        log.info("Sending reminder to customer for reservation: {}", reservationId);
        try {
            Reservation reservation = reservationClient.getReservationById(reservationId);
            if (reservation == null) {
                throw new IllegalArgumentException("Reservation not found: " + reservationId);
            }
            // TODO: Envoyer email/SMS reminder au client
            log.info("Reminder sent for reservation: {} to user: {}", reservationId, reservation.getUserId());
        } catch (Exception e) {
            log.error("Error sending reminder: {}", e.getMessage());
            throw new IllegalArgumentException("Reservation not found: " + reservationId);
        }
    }
}
