package com.gdldv.user.service;

import com.gdldv.user.dto.AdminDashboard;
import com.gdldv.user.dto.UrgentTask;
import com.gdldv.user.entity.Reservation;
import com.gdldv.user.entity.ReservationStatus;
import com.gdldv.user.repository.ReservationRepository;
import com.gdldv.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * GDLDV-595: Admin Dashboard Service (Sprint 3)
 * Service pour le tableau de bord administrateur
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    public AdminDashboard getAdminDashboard() {
        log.info("Fetching admin dashboard data");

        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime tomorrow = today.plus(1, ChronoUnit.DAYS);

        // Réservations du jour
        List<Reservation> todayReservations = reservationRepository
                .findByCreatedAtBetween(today, tomorrow);

        // Réservations actives (en cours)
        List<Reservation> activeReservations = reservationRepository
                .findByStatus(ReservationStatus.ACTIVE);

        // Réservations à confirmer (paiement pending)
        List<Reservation> pendingReservations = reservationRepository
                .findByStatus(ReservationStatus.PENDING);

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

        // Vérifier les réservations en attente depuis plus de 24h
        LocalDateTime yesterday = LocalDateTime.now().minus(1, ChronoUnit.DAYS);
        List<Reservation> oldPendingReservations = reservationRepository
                .findByStatusAndCreatedAtBefore(ReservationStatus.PENDING, yesterday);

        oldPendingReservations.forEach(r -> {
            tasks.add(UrgentTask.builder()
                    .type("OLD_PENDING_RESERVATION")
                    .reservationId(r.getId())
                    .description("Réservation en attente depuis >24h: " + r.getConfirmationNumber())
                    .priority("HIGH")
                    .build());
        });

        // Vérifier les locations actives proches de la date de fin
        LocalDateTime soon = LocalDateTime.now().plus(2, ChronoUnit.HOURS);
        List<Reservation> endingSoonReservations = reservationRepository
                .findByStatusAndEndDateBefore(ReservationStatus.ACTIVE, soon);

        endingSoonReservations.forEach(r -> {
            tasks.add(UrgentTask.builder()
                    .type("ENDING_SOON")
                    .reservationId(r.getId())
                    .description("Location se termine bientôt: " + r.getConfirmationNumber())
                    .priority("MEDIUM")
                    .build());
        });

        // Vérifier les check-ins manqués (réservation débutée mais pas active)
        LocalDateTime oneHourAgo = LocalDateTime.now().minus(1, ChronoUnit.HOURS);
        List<Reservation> missedCheckIns = reservationRepository
                .findByStatusAndStartDateBefore(ReservationStatus.CONFIRMED, oneHourAgo);

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
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));

        // TODO: Traiter le refund via RefundService
        log.info("Refund approved for reservation: {}", reservationId);
    }

    public void sendReminderToCustomer(Long reservationId) {
        log.info("Sending reminder to customer for reservation: {}", reservationId);
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));

        // TODO: Envoyer email/SMS reminder au client
        log.info("Reminder sent for reservation: {} to user: {}", reservationId, reservation.getUserId());
    }
}
