package com.gdldv.rental.service;

import com.gdldv.rental.client.VehicleClient;
import com.gdldv.rental.dto.BusinessMetrics;
import com.gdldv.rental.entity.Rental;
import com.gdldv.rental.entity.RentalStatus;
import com.gdldv.rental.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * GDLDV-533: Service de calcul des KPIs métier
 * Calcule les métriques pour le dashboard analytics
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessMetricsService {

    private final RentalRepository rentalRepository;
    private final VehicleClient vehicleClient;

    /**
     * Récupérer les métriques du jour
     */
    @Transactional(readOnly = true)
    public BusinessMetrics getTodayMetrics() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        log.info("Calcul des métriques pour aujourd'hui: {} - {}", startOfDay, endOfDay);
        return calculateMetrics(startOfDay, endOfDay);
    }

    /**
     * Récupérer les métriques du mois en cours
     */
    @Transactional(readOnly = true)
    public BusinessMetrics getMonthMetrics() {
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(23, 59, 59);

        log.info("Calcul des métriques pour le mois: {} - {}", startOfMonth, endOfMonth);
        return calculateMetrics(startOfMonth, endOfMonth);
    }

    /**
     * Récupérer les métriques de l'année en cours
     */
    @Transactional(readOnly = true)
    public BusinessMetrics getYearMetrics() {
        LocalDateTime startOfYear = LocalDateTime.now().withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfYear = LocalDateTime.now().withMonth(12).withDayOfMonth(31).withHour(23).withMinute(59).withSecond(59);

        log.info("Calcul des métriques pour l'année: {} - {}", startOfYear, endOfYear);
        return calculateMetrics(startOfYear, endOfYear);
    }

    /**
     * Récupérer les métriques pour une période personnalisée
     */
    @Transactional(readOnly = true)
    public BusinessMetrics getCustomPeriodMetrics(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Calcul des métriques pour la période: {} - {}", startDate, endDate);
        return calculateMetrics(startDate, endDate);
    }

    /**
     * Calculer toutes les métriques pour une période donnée
     */
    private BusinessMetrics calculateMetrics(LocalDateTime startDate, LocalDateTime endDate) {
        // Récupérer toutes les locations de la période
        List<Rental> rentals = rentalRepository.findByCreatedAtBetween(startDate, endDate);

        // Calculer les KPIs financiers
        double totalRevenue = rentals.stream()
                .filter(r -> r.getStatus() == RentalStatus.COMPLETED)
                .mapToDouble(r -> r.getTotalPrice().doubleValue() != null ? r.getTotalPrice().doubleValue() : 0.0)
                .sum();

        int totalBookings = rentals.size();
        double averageBookingValue = totalBookings > 0 ? totalRevenue / totalBookings : 0.0;

        // Calculer les KPIs opérationnels
        long completedBookings = rentals.stream()
                .filter(r -> r.getStatus() == RentalStatus.COMPLETED)
                .count();

        long cancelledBookings = rentals.stream()
                .filter(r -> r.getStatus() == RentalStatus.CANCELLED)
                .count();

        double cancellationRate = totalBookings > 0 ? (cancelledBookings * 100.0 / totalBookings) : 0.0;

        // Calculer les top véhicules
        List<BusinessMetrics.TopVehicle> topVehicles = calculateTopVehicles(rentals);

        // Calculer les KPIs clients
        Set<Long> allCustomers = rentals.stream()
                .map(Rental::getUserId)
                .collect(Collectors.toSet());

        Set<Long> repeatCustomers = findRepeatCustomers(rentals);
        double repeatCustomerRate = allCustomers.size() > 0
                ? (repeatCustomers.size() * 100.0 / allCustomers.size())
                : 0.0;

        // Calculer l'utilisation de la flotte
        double fleetUtilization = calculateFleetUtilization(rentals, startDate, endDate);

        // Revenu par période (par mois)
        Map<String, Double> revenueByPeriod = calculateRevenueByPeriod(rentals);

        // Calculer les tendances (comparaison avec période précédente)
        double revenueGrowth = calculateGrowth(startDate, endDate, true);
        double bookingsGrowth = calculateGrowth(startDate, endDate, false);

        return BusinessMetrics.builder()
                .periodStart(startDate)
                .periodEnd(endDate)
                .totalRevenue(totalRevenue)
                .averageBookingValue(averageBookingValue)
                .revenueByPeriod(revenueByPeriod)
                .totalBookings(totalBookings)
                .completedBookings((int) completedBookings)
                .cancelledBookings((int) cancelledBookings)
                .cancellationRate(Math.round(cancellationRate * 100.0) / 100.0)
                .fleetUtilization(Math.round(fleetUtilization * 100.0) / 100.0)
                .topVehicles(topVehicles)
                .totalCustomers(allCustomers.size())
                .repeatCustomers(repeatCustomers.size())
                .repeatCustomerRate(Math.round(repeatCustomerRate * 100.0) / 100.0)
                .revenueGrowth(Math.round(revenueGrowth * 100.0) / 100.0)
                .bookingsGrowth(Math.round(bookingsGrowth * 100.0) / 100.0)
                .build();
    }

    /**
     * Calculer le top 5 des véhicules les plus loués
     */
    private List<BusinessMetrics.TopVehicle> calculateTopVehicles(List<Rental> rentals) {
        Map<Long, List<Rental>> rentalsByVehicle = rentals.stream()
                .filter(r -> r.getVehicleId() != null)
                .collect(Collectors.groupingBy(Rental::getVehicleId));

        return rentalsByVehicle.entrySet().stream()
                .map(entry -> {
                    Long vehicleId = entry.getKey();
                    List<Rental> vehicleRentals = entry.getValue();

                    int bookingCount = vehicleRentals.size();
                    double totalRevenue = vehicleRentals.stream()
                            .filter(r -> r.getStatus() == RentalStatus.COMPLETED)
                            .mapToDouble(r -> r.getTotalPrice().doubleValue() != null ? r.getTotalPrice().doubleValue() : 0.0)
                            .sum();

                    // Récupérer les infos du véhicule (brand, model) depuis le premier rental
                    Rental firstRental = vehicleRentals.get(0);

                    return BusinessMetrics.TopVehicle.builder()
                            .vehicleId(vehicleId)
                            .brand("Unknown") // TODO: Récupérer via VehicleClient si nécessaire
                            .model("Unknown")
                            .bookingCount(bookingCount)
                            .totalRevenue(totalRevenue)
                            .build();
                })
                .sorted(Comparator.comparingInt(BusinessMetrics.TopVehicle::getBookingCount).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    /**
     * Trouver les clients récurrents (qui ont fait plus d'une location)
     */
    private Set<Long> findRepeatCustomers(List<Rental> rentals) {
        Map<Long, Long> customerBookingCount = rentals.stream()
                .collect(Collectors.groupingBy(Rental::getUserId, Collectors.counting()));

        return customerBookingCount.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    /**
     * Calculer le taux d'utilisation de la flotte
     * Formule: (Nombre de jours de location / (Nombre de véhicules * Nombre de jours dans la période)) * 100
     */
    private double calculateFleetUtilization(List<Rental> rentals, LocalDateTime startDate, LocalDateTime endDate) {
        // Compter le nombre de véhicules uniques loués
        long uniqueVehicles = rentals.stream()
                .map(Rental::getVehicleId)
                .distinct()
                .count();

        if (uniqueVehicles == 0) {
            return 0.0;
        }

        // Calculer le nombre total de jours de location
        long totalRentalDays = rentals.stream()
                .filter(r -> r.getStartDate() != null && r.getEndDate() != null)
                .mapToLong(r -> java.time.Duration.between(r.getStartDate(), r.getEndDate()).toDays())
                .sum();

        // Calculer le nombre de jours dans la période
        long daysInPeriod = java.time.Duration.between(startDate, endDate).toDays();
        if (daysInPeriod == 0) {
            daysInPeriod = 1;
        }

        // Taux d'utilisation = (jours de location) / (véhicules * jours période) * 100
        double utilization = (totalRentalDays * 100.0) / (uniqueVehicles * daysInPeriod);

        return Math.min(utilization, 100.0); // Plafonner à 100%
    }

    /**
     * Calculer le revenu par mois
     */
    private Map<String, Double> calculateRevenueByPeriod(List<Rental> rentals) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        return rentals.stream()
                .filter(r -> r.getStatus() == RentalStatus.COMPLETED)
                .filter(r -> r.getCreatedAt() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getCreatedAt().format(formatter),
                        Collectors.summingDouble(r -> r.getTotalPrice().doubleValue() != null ? r.getTotalPrice().doubleValue() : 0.0)
                ))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /**
     * Calculer la croissance par rapport à la période précédente
     */
    private double calculateGrowth(LocalDateTime startDate, LocalDateTime endDate, boolean isRevenue) {
        long periodDuration = java.time.Duration.between(startDate, endDate).toDays();
        LocalDateTime prevStartDate = startDate.minusDays(periodDuration);
        LocalDateTime prevEndDate = startDate.minusDays(1);

        List<Rental> currentRentals = rentalRepository.findByCreatedAtBetween(startDate, endDate);
        List<Rental> previousRentals = rentalRepository.findByCreatedAtBetween(prevStartDate, prevEndDate);

        double currentValue;
        double previousValue;

        if (isRevenue) {
            currentValue = currentRentals.stream()
                    .filter(r -> r.getStatus() == RentalStatus.COMPLETED)
                    .mapToDouble(r -> r.getTotalPrice().doubleValue() != null ? r.getTotalPrice().doubleValue() : 0.0)
                    .sum();

            previousValue = previousRentals.stream()
                    .filter(r -> r.getStatus() == RentalStatus.COMPLETED)
                    .mapToDouble(r -> r.getTotalPrice().doubleValue() != null ? r.getTotalPrice().doubleValue() : 0.0)
                    .sum();
        } else {
            currentValue = currentRentals.size();
            previousValue = previousRentals.size();
        }

        if (previousValue == 0) {
            return currentValue > 0 ? 100.0 : 0.0;
        }

        return ((currentValue - previousValue) / previousValue) * 100.0;
    }
}
