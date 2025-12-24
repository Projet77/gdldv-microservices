package com.gdldv.user.service;

import com.gdldv.user.dto.ManagerDashboard;
import com.gdldv.user.entity.Reservation;
import com.gdldv.user.entity.ReservationStatus;
import com.gdldv.user.entity.User;
import com.gdldv.user.repository.ReservationRepository;
import com.gdldv.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service pour le dashboard MANAGER
 * Vue exécutive et KPIs pour la supervision
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ManagerDashboardService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    public ManagerDashboard getManagerDashboard() {
        log.info("Fetching manager dashboard");

        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime tomorrow = today.plus(1, ChronoUnit.DAYS);
        LocalDateTime yesterday = today.minus(1, ChronoUnit.DAYS);
        LocalDateTime monthStart = today.withDayOfMonth(1);
        LocalDateTime lastMonthStart = monthStart.minus(1, ChronoUnit.MONTHS);

        // KPIs du jour
        List<Reservation> todayReservations = reservationRepository.findByCreatedAtBetween(today, tomorrow);
        double todayRevenue = todayReservations.stream()
                .filter(r -> ReservationStatus.COMPLETED.equals(r.getStatus()) ||
                           ReservationStatus.CONFIRMED.equals(r.getStatus()))
                .mapToDouble(Reservation::getTotalPrice)
                .sum();

        List<Reservation> yesterdayReservations = reservationRepository.findByCreatedAtBetween(yesterday, today);
        double yesterdayRevenue = yesterdayReservations.stream()
                .filter(r -> ReservationStatus.COMPLETED.equals(r.getStatus()) ||
                           ReservationStatus.CONFIRMED.equals(r.getStatus()))
                .mapToDouble(Reservation::getTotalPrice)
                .sum();

        // Comparaisons
        double revenueVsYesterday = yesterdayRevenue > 0 ?
                ((todayRevenue - yesterdayRevenue) / yesterdayRevenue) * 100 : 0.0;
        double rentalsVsYesterday = yesterdayReservations.size() > 0 ?
                ((double)(todayReservations.size() - yesterdayReservations.size()) / yesterdayReservations.size()) * 100 : 0.0;

        // Performance mensuelle
        List<Reservation> monthReservations = reservationRepository.findByCreatedAtBetween(monthStart, LocalDateTime.now());
        double monthlyRevenue = monthReservations.stream()
                .filter(r -> ReservationStatus.COMPLETED.equals(r.getStatus()) ||
                           ReservationStatus.CONFIRMED.equals(r.getStatus()))
                .mapToDouble(Reservation::getTotalPrice)
                .sum();

        // Objectifs (hardcodés pour l'instant - TODO: récupérer depuis config)
        double todayRevenueTarget = 500000.0;
        int todayRentalsTarget = 15;
        double fleetUtilizationTarget = 70.0;
        double customerSatisfactionTarget = 4.5;
        double lateReturnTarget = 2.0;
        double monthlyRevenueTarget = 15000000.0;
        int monthlyRentalsTarget = 300;

        // Calcul taux d'utilisation (TODO: récupérer depuis vehicle-service)
        double fleetUtilizationRate = 64.0;

        // Satisfaction client (TODO: récupérer depuis reviews)
        double customerSatisfaction = 4.6;

        // Taux de retard
        List<Reservation> lateReturns = reservationRepository
                .findByStatusAndEndDateBefore(ReservationStatus.ACTIVE, LocalDateTime.now());
        List<Reservation> allActive = reservationRepository.findByStatus(ReservationStatus.ACTIVE);
        double lateReturnRate = allActive.size() > 0 ?
                ((double) lateReturns.size() / allActive.size()) * 100 : 0.0;

        // Équipe (TODO: récupérer vraiment les agents)
        List<ManagerDashboard.StaffPerformance> staffPerformance = getStaffPerformance();

        // Incidents
        List<ManagerDashboard.IncidentInfo> criticalIncidents = getCriticalIncidents();
        List<ManagerDashboard.IncidentInfo> importantIssues = getImportantIssues();

        // Tendances (30 derniers jours)
        List<ManagerDashboard.DailyTrend> revenueTrend = calculateRevenueTrend();
        List<ManagerDashboard.DailyTrend> rentalsTrend = calculateRentalsTrend();
        List<ManagerDashboard.DailyTrend> satisfactionTrend = calculateSatisfactionTrend();

        // Top performers
        List<ManagerDashboard.TopVehicle> topVehicles = getTopVehicles();
        List<ManagerDashboard.TopClient> topClients = getTopClients();

        // Flotte (TODO: récupérer depuis vehicle-service)
        int totalVehicles = 50;
        int availableVehicles = 32;
        int rentedVehicles = 12;
        int maintenanceVehicles = 4;
        int outOfServiceVehicles = 2;

        return ManagerDashboard.builder()
                .todayRevenue(todayRevenue)
                .todayRevenueTarget(todayRevenueTarget)
                .todayRentals(todayReservations.size())
                .todayRentalsTarget(todayRentalsTarget)
                .fleetUtilizationRate(fleetUtilizationRate)
                .fleetUtilizationTarget(fleetUtilizationTarget)
                .customerSatisfaction(customerSatisfaction)
                .customerSatisfactionTarget(customerSatisfactionTarget)
                .lateReturnRate(lateReturnRate)
                .lateReturnTarget(lateReturnTarget)
                .revenueVsYesterday(revenueVsYesterday)
                .rentalsVsYesterday(rentalsVsYesterday)
                .satisfactionVsLastMonth(0.2) // TODO: calculer
                .monthlyRevenue(monthlyRevenue)
                .monthlyRevenueTarget(monthlyRevenueTarget)
                .monthlyRentals(monthReservations.size())
                .monthlyRentalsTarget(monthlyRentalsTarget)
                .monthlyProgress(((double) LocalDateTime.now().getDayOfMonth() / LocalDateTime.now().getMonth().length(false)) * 100)
                .staffPerformance(staffPerformance)
                .activeStaffToday(staffPerformance.size())
                .totalStaff(15) // TODO: compter réellement
                .criticalIncidents(criticalIncidents)
                .importantIssues(importantIssues)
                .totalIncidentsThisMonth(criticalIncidents.size() + importantIssues.size())
                .revenueTrend(revenueTrend)
                .rentalsTrend(rentalsTrend)
                .satisfactionTrend(satisfactionTrend)
                .topVehicles(topVehicles)
                .topClients(topClients)
                .totalVehicles(totalVehicles)
                .availableVehicles(availableVehicles)
                .rentedVehicles(rentedVehicles)
                .maintenanceVehicles(maintenanceVehicles)
                .outOfServiceVehicles(outOfServiceVehicles)
                .build();
    }

    private List<ManagerDashboard.StaffPerformance> getStaffPerformance() {
        // TODO: Récupérer vraiment les agents et leurs performances
        List<ManagerDashboard.StaffPerformance> staff = new ArrayList<>();

        staff.add(ManagerDashboard.StaffPerformance.builder()
                .staffId(1L)
                .name("Ahmed Hassan")
                .role("AGENT")
                .shiftStart(LocalDateTime.now().withHour(8).withMinute(0))
                .shiftEnd(LocalDateTime.now().withHour(16).withMinute(0))
                .transactionsToday(5)
                .satisfactionRating(4.8)
                .status("ACTIVE")
                .build());

        staff.add(ManagerDashboard.StaffPerformance.builder()
                .staffId(2L)
                .name("Fatou Diallo")
                .role("AGENT")
                .shiftStart(LocalDateTime.now().withHour(10).withMinute(0))
                .shiftEnd(LocalDateTime.now().withHour(18).withMinute(0))
                .transactionsToday(4)
                .satisfactionRating(4.5)
                .status("ACTIVE")
                .build());

        return staff;
    }

    private List<ManagerDashboard.IncidentInfo> getCriticalIncidents() {
        // TODO: Récupérer vraiment les incidents critiques
        return new ArrayList<>();
    }

    private List<ManagerDashboard.IncidentInfo> getImportantIssues() {
        // TODO: Récupérer vraiment les incidents importants
        return new ArrayList<>();
    }

    private List<ManagerDashboard.DailyTrend> calculateRevenueTrend() {
        List<ManagerDashboard.DailyTrend> trends = new ArrayList<>();
        LocalDateTime end = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);

        for (int i = 29; i >= 0; i--) {
            LocalDateTime date = end.minus(i, ChronoUnit.DAYS);
            LocalDateTime nextDay = date.plus(1, ChronoUnit.DAYS);

            List<Reservation> dayReservations = reservationRepository.findByCreatedAtBetween(date, nextDay);
            double revenue = dayReservations.stream()
                    .filter(r -> ReservationStatus.COMPLETED.equals(r.getStatus()) ||
                               ReservationStatus.CONFIRMED.equals(r.getStatus()))
                    .mapToDouble(Reservation::getTotalPrice)
                    .sum();

            trends.add(ManagerDashboard.DailyTrend.builder()
                    .date(date)
                    .value(revenue)
                    .build());
        }

        return trends;
    }

    private List<ManagerDashboard.DailyTrend> calculateRentalsTrend() {
        List<ManagerDashboard.DailyTrend> trends = new ArrayList<>();
        LocalDateTime end = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);

        for (int i = 29; i >= 0; i--) {
            LocalDateTime date = end.minus(i, ChronoUnit.DAYS);
            LocalDateTime nextDay = date.plus(1, ChronoUnit.DAYS);

            List<Reservation> dayReservations = reservationRepository.findByCreatedAtBetween(date, nextDay);

            trends.add(ManagerDashboard.DailyTrend.builder()
                    .date(date)
                    .value((double) dayReservations.size())
                    .build());
        }

        return trends;
    }

    private List<ManagerDashboard.DailyTrend> calculateSatisfactionTrend() {
        // TODO: Récupérer depuis reviews
        List<ManagerDashboard.DailyTrend> trends = new ArrayList<>();
        LocalDateTime end = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);

        for (int i = 29; i >= 0; i--) {
            LocalDateTime date = end.minus(i, ChronoUnit.DAYS);
            trends.add(ManagerDashboard.DailyTrend.builder()
                    .date(date)
                    .value(4.5 + (Math.random() * 0.5)) // Mock data
                    .build());
        }

        return trends;
    }

    private List<ManagerDashboard.TopVehicle> getTopVehicles() {
        // TODO: Récupérer depuis vehicle-service + reservations
        return new ArrayList<>();
    }

    private List<ManagerDashboard.TopClient> getTopClients() {
        // Grouper par userId et calculer
        Map<Long, List<Reservation>> byUser = reservationRepository.findAll().stream()
                .filter(r -> ReservationStatus.COMPLETED.equals(r.getStatus()))
                .collect(Collectors.groupingBy(Reservation::getUserId));

        return byUser.entrySet().stream()
                .map(entry -> {
                    Long userId = entry.getKey();
                    List<Reservation> userReservations = entry.getValue();

                    double totalSpent = userReservations.stream()
                            .mapToDouble(Reservation::getTotalPrice)
                            .sum();

                    return ManagerDashboard.TopClient.builder()
                            .clientId(userId)
                            .name("Client #" + userId) // TODO: récupérer nom
                            .rentalsCount(userReservations.size())
                            .totalSpent(totalSpent)
                            .averageRating(4.5) // TODO: calculer
                            .build();
                })
                .sorted((a, b) -> Double.compare(b.getTotalSpent(), a.getTotalSpent()))
                .limit(5)
                .collect(Collectors.toList());
    }
}
