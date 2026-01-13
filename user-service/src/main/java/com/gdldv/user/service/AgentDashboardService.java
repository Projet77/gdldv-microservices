package com.gdldv.user.service;

import com.gdldv.user.dto.AgentDashboard;
import com.gdldv.user.entity.CheckIn;
import com.gdldv.user.entity.CheckOut;
import com.gdldv.user.entity.Reservation;
import com.gdldv.user.entity.ReservationStatus;
import com.gdldv.user.repository.CheckInRepository;
import com.gdldv.user.repository.CheckOutRepository;
import com.gdldv.user.repository.ReservationRepository;
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
 * Service pour le dashboard AGENT
 * Gère les opérations de check-in/check-out
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AgentDashboardService {

    private final ReservationRepository reservationRepository;
    private final CheckOutRepository checkOutRepository;
    private final CheckInRepository checkInRepository;
    private final UserRepository userRepository;

    public AgentDashboard getAgentDashboard(Long agentId) {
        log.info("Fetching agent dashboard for agent: {}", agentId);

        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime tomorrow = today.plus(1, ChronoUnit.DAYS);

        // Statistiques du jour
        List<CheckOut> todayCheckOuts = checkOutRepository.findByCreatedAtBetween(today, tomorrow);
        List<CheckIn> todayCheckIns = checkInRepository.findByCreatedAtBetween(today, tomorrow);

        // Réservations à traiter aujourd'hui
        List<Reservation> todayReservations = reservationRepository.findByStartDateBetween(today, tomorrow);
        List<Reservation> todayReturns = reservationRepository.findByEndDateBetween(today, tomorrow);

        // Pending check-outs (réservations confirmées mais pas encore prises en charge)
        List<Reservation> pendingCheckOuts = reservationRepository.findByStatus(ReservationStatus.CONFIRMED);

        // Pending check-ins (réservations actives à restituer aujourd'hui ou en retard)
        List<Reservation> pendingCheckIns = reservationRepository.findByStatusAndEndDateBefore(
                ReservationStatus.ACTIVE,
                LocalDateTime.now().plus(2, ChronoUnit.HOURS)
        );

        // Calcul revenus
        double todayRevenue = todayCheckOuts.stream()
                .mapToDouble(co -> {
                    // TODO: récupérer le montant depuis la réservation
                    return 0.0;
                })
                .sum();

        // Alertes
        List<AgentDashboard.AgentAlert> alerts = generateAlerts(pendingCheckIns);

        return AgentDashboard.builder()
                .agentId(agentId)
                .agentName("Agent #" + agentId) // TODO: récupérer le nom depuis user
                .shiftStart(today.withHour(8))
                .shiftEnd(today.withHour(18))
                .todayCheckOuts(todayCheckOuts.size())
                .todayCheckIns(todayCheckIns.size())
                .pendingCheckOuts(pendingCheckOuts.size())
                .pendingCheckIns(pendingCheckIns.size())
                .todayRevenue(todayRevenue)
                .averageProcessingTime(15.0) // TODO: calculer réellement
                .pendingCheckOutQueue(toPendingCheckOutInfos(pendingCheckOuts))
                .pendingCheckInQueue(toPendingCheckInInfos(pendingCheckIns))
                .todayReservations(toTodayReservationInfos(todayReservations, todayReturns))
                .completedToday(toCompletedTransactionInfos(todayCheckOuts, todayCheckIns))
                .availableVehicles(new ArrayList<>()) // TODO: récupérer depuis vehicle-service
                .alerts(alerts)
                .build();
    }

    private List<AgentDashboard.PendingCheckOutInfo> toPendingCheckOutInfos(List<Reservation> reservations) {
        return reservations.stream()
                .limit(10)
                .map(r -> AgentDashboard.PendingCheckOutInfo.builder()
                        .reservationId(r.getId())
                        .confirmationNumber(r.getConfirmationNumber())
                        .clientName("Client #" + r.getUserId()) // TODO: récupérer nom depuis user
                        .clientPhone("+221 XX XXX XXXX")
                        .vehicleId(r.getVehicleId())
                        .vehicleName("Véhicule #" + r.getVehicleId())
                        .scheduledTime(r.getStartDate())
                        .status(r.getStatus().name())
                        .build())
                .collect(Collectors.toList());
    }

    private List<AgentDashboard.PendingCheckInInfo> toPendingCheckInInfos(List<Reservation> reservations) {
        return reservations.stream()
                .map(r -> {
                    LocalDateTime now = LocalDateTime.now();
                    boolean isLate = r.getEndDate().isBefore(now);
                    int minutesLate = isLate ? (int) ChronoUnit.MINUTES.between(r.getEndDate(), now) : 0;

                    return AgentDashboard.PendingCheckInInfo.builder()
                            .reservationId(r.getId())
                            .confirmationNumber(r.getConfirmationNumber())
                            .clientName("Client #" + r.getUserId())
                            .vehicleId(r.getVehicleId())
                            .vehicleName("Véhicule #" + r.getVehicleId())
                            .expectedReturnTime(r.getEndDate())
                            .isLate(isLate)
                            .minutesLate(minutesLate)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<AgentDashboard.TodayReservationInfo> toTodayReservationInfos(
            List<Reservation> checkOuts,
            List<Reservation> checkIns) {

        List<AgentDashboard.TodayReservationInfo> infos = new ArrayList<>();

        // Check-outs
        checkOuts.forEach(r -> infos.add(AgentDashboard.TodayReservationInfo.builder()
                .reservationId(r.getId())
                .confirmationNumber(r.getConfirmationNumber())
                .clientName("Client #" + r.getUserId())
                .vehicleName("Véhicule #" + r.getVehicleId())
                .scheduledTime(r.getStartDate())
                .type("CHECK_OUT")
                .status(r.getStatus().name())
                .build()));

        // Check-ins
        checkIns.forEach(r -> infos.add(AgentDashboard.TodayReservationInfo.builder()
                .reservationId(r.getId())
                .confirmationNumber(r.getConfirmationNumber())
                .clientName("Client #" + r.getUserId())
                .vehicleName("Véhicule #" + r.getVehicleId())
                .scheduledTime(r.getEndDate())
                .type("CHECK_IN")
                .status(r.getStatus().name())
                .build()));

        return infos.stream()
                .sorted((a, b) -> a.getScheduledTime().compareTo(b.getScheduledTime()))
                .collect(Collectors.toList());
    }

    private List<AgentDashboard.CompletedTransactionInfo> toCompletedTransactionInfos(
            List<CheckOut> checkOuts,
            List<CheckIn> checkIns) {

        List<AgentDashboard.CompletedTransactionInfo> infos = new ArrayList<>();

        checkOuts.forEach(co -> infos.add(AgentDashboard.CompletedTransactionInfo.builder()
                .transactionId(co.getId())
                .confirmationNumber("CO-" + co.getId())
                .clientName("Client #" + co.getReservationId()) // TODO: améliorer
                .vehicleName("Véhicule")
                .completedTime(co.getCreatedAt())
                .type("CHECK_OUT")
                .amount(0.0) // TODO: récupérer montant
                .build()));

        checkIns.forEach(ci -> infos.add(AgentDashboard.CompletedTransactionInfo.builder()
                .transactionId(ci.getId())
                .confirmationNumber("CI-" + ci.getId())
                .clientName("Client")
                .vehicleName("Véhicule")
                .completedTime(ci.getCreatedAt())
                .type("CHECK_IN")
                .amount(ci.getAdditionalCharges())
                .build()));

        return infos.stream()
                .sorted((a, b) -> b.getCompletedTime().compareTo(a.getCompletedTime()))
                .limit(10)
                .collect(Collectors.toList());
    }

    private List<AgentDashboard.AgentAlert> generateAlerts(List<Reservation> pendingCheckIns) {
        List<AgentDashboard.AgentAlert> alerts = new ArrayList<>();

        // Alertes de retard
        pendingCheckIns.stream()
                .filter(r -> r.getEndDate().isBefore(LocalDateTime.now()))
                .forEach(r -> alerts.add(AgentDashboard.AgentAlert.builder()
                        .type("LATE_RETURN")
                        .message("Retard de restitution: " + r.getConfirmationNumber())
                        .reservationId(r.getId())
                        .priority("HIGH")
                        .createdAt(LocalDateTime.now())
                        .build()));

        // Alertes de check-in manqué
        LocalDateTime oneHourAgo = LocalDateTime.now().minus(1, ChronoUnit.HOURS);
        List<Reservation> missedCheckIns = reservationRepository
                .findByStatusAndStartDateBefore(ReservationStatus.CONFIRMED, oneHourAgo);

        missedCheckIns.forEach(r -> alerts.add(AgentDashboard.AgentAlert.builder()
                .type("MISSED_CHECKIN")
                .message("Prise en charge non effectuée: " + r.getConfirmationNumber())
                .reservationId(r.getId())
                .priority("MEDIUM")
                .createdAt(LocalDateTime.now())
                .build()));

        return alerts;
    }
}
