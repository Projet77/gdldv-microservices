package com.gdldv.user.service;

import com.gdldv.user.client.VehicleServiceClient;
import com.gdldv.user.dto.ClientDashboard;
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
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour le dashboard CLIENT
 * Gère les données du tableau de bord client
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ClientDashboardService {

    // Note: Cache annotations seraient ajoutées ici si Redis est configuré
    // @Cacheable(value = "clientDashboard", key = "#userId")

    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final VehicleServiceClient vehicleServiceClient;

    public ClientDashboard getClientDashboard(Long userId) {
        log.info("Fetching client dashboard for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // Statistiques de base
        List<Reservation> allReservations = reservationRepository.findByUserId(userId);
        List<Reservation> activeReservations = reservationRepository.findByUserIdAndStatus(userId, ReservationStatus.ACTIVE);

        // Calcul des statistiques
        double totalSpent = allReservations.stream()
                .filter(r -> ReservationStatus.COMPLETED.equals(r.getStatus()))
                .mapToDouble(Reservation::getTotalPrice)
                .sum();

        long completedCount = allReservations.stream()
                .filter(r -> ReservationStatus.COMPLETED.equals(r.getStatus()))
                .count();

        double averageSpent = completedCount > 0 ? totalSpent / completedCount : 0.0;

        // Réservations actives
        List<ClientDashboard.ActiveRentalInfo> currentRentals = activeReservations.stream()
                .map(this::toActiveRentalInfo)
                .collect(Collectors.toList());

        // Historique récent (5 dernières locations complétées)
        List<ClientDashboard.RentalHistoryInfo> recentHistory = allReservations.stream()
                .filter(r -> ReservationStatus.COMPLETED.equals(r.getStatus()))
                .sorted((r1, r2) -> r2.getEndDate().compareTo(r1.getEndDate()))
                .limit(5)
                .map(this::toRentalHistoryInfo)
                .collect(Collectors.toList());

        // Véhicules favoris (appel au vehicle-service)
        List<ClientDashboard.FavoriteVehicleInfo> favorites = getFavoriteVehicles(userId);

        // Paiements récents
        List<ClientDashboard.PaymentInfo> recentPayments = getRecentPayments(allReservations);

        // Totaux mensuels et annuels
        LocalDateTime nowMonthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime nowYearStart = LocalDateTime.now().withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);

        double monthlyTotal = allReservations.stream()
                .filter(r -> r.getCreatedAt().isAfter(nowMonthStart))
                .filter(r -> ReservationStatus.COMPLETED.equals(r.getStatus()) || ReservationStatus.CONFIRMED.equals(r.getStatus()))
                .mapToDouble(Reservation::getTotalPrice)
                .sum();

        double yearlyTotal = allReservations.stream()
                .filter(r -> r.getCreatedAt().isAfter(nowYearStart))
                .filter(r -> ReservationStatus.COMPLETED.equals(r.getStatus()) || ReservationStatus.CONFIRMED.equals(r.getStatus()))
                .mapToDouble(Reservation::getTotalPrice)
                .sum();

        // Badge de membership
        String membershipBadge = determineMembershipBadge(completedCount);

        return ClientDashboard.builder()
                .userId(userId)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .memberSince(user.getCreatedAt())
                .membershipBadge(membershipBadge)
                .averageRating(4.5) // TODO: calculer depuis les avis
                .activeRentals(activeReservations.size())
                .totalRentals((int) completedCount)
                .totalSpent(totalSpent)
                .averageSpentPerRental(averageSpent)
                .averageDuration(calculateAverageDuration(allReservations))
                .totalKilometers(0) // TODO: calculer depuis les inspections
                .favoriteCategory("SUV") // TODO: calculer depuis l'historique
                .currentRentals(currentRentals)
                .recentHistory(recentHistory)
                .favorites(favorites)
                .recentPayments(recentPayments)
                .monthlyTotal(monthlyTotal)
                .yearlyTotal(yearlyTotal)
                .build();
    }

    private ClientDashboard.ActiveRentalInfo toActiveRentalInfo(Reservation reservation) {
        long daysRemaining = ChronoUnit.DAYS.between(LocalDateTime.now(), reservation.getEndDate());
        long hoursRemaining = ChronoUnit.HOURS.between(LocalDateTime.now(), reservation.getEndDate()) % 24;

        return ClientDashboard.ActiveRentalInfo.builder()
                .reservationId(reservation.getId())
                .confirmationNumber(reservation.getConfirmationNumber())
                .vehicleId(reservation.getVehicleId())
                .vehicleName("Véhicule #" + reservation.getVehicleId()) // TODO: récupérer depuis vehicle-service
                .vehicleImage("/images/default-vehicle.jpg")
                .startDate(reservation.getStartDate())
                .endDate(reservation.getEndDate())
                .pickupLocation("Agence principale") // TODO: récupérer depuis reservation
                .returnLocation("Agence principale")
                .dailyPrice(reservation.getTotalPrice() / ChronoUnit.DAYS.between(reservation.getStartDate(), reservation.getEndDate()))
                .totalPrice(reservation.getTotalPrice())
                .status(reservation.getStatus().name())
                .daysRemaining((int) daysRemaining)
                .hoursRemaining((int) hoursRemaining)
                .build();
    }

    private ClientDashboard.RentalHistoryInfo toRentalHistoryInfo(Reservation reservation) {
        return ClientDashboard.RentalHistoryInfo.builder()
                .reservationId(reservation.getId())
                .confirmationNumber(reservation.getConfirmationNumber())
                .vehicleName("Véhicule #" + reservation.getVehicleId())
                .startDate(reservation.getStartDate())
                .endDate(reservation.getEndDate())
                .totalPrice(reservation.getTotalPrice())
                .status(reservation.getStatus().name())
                .rating(0.0) // TODO: récupérer depuis reviews
                .review("")
                .build();
    }

    private List<ClientDashboard.FavoriteVehicleInfo> getFavoriteVehicles(Long userId) {
        try {
            List<VehicleServiceClient.FavoriteVehicle> favorites =
                vehicleServiceClient.getFavoritesByUserId(userId);

            return favorites.stream()
                    .map(fav -> ClientDashboard.FavoriteVehicleInfo.builder()
                            .vehicleId(fav.getVehicleId())
                            .name(fav.getVehicleName())
                            .image(fav.getVehicleImage())
                            .dailyPrice(fav.getDailyPrice())
                            .averageRating(fav.getAverageRating())
                            .available(fav.getAvailable())
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching favorites from vehicle-service: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<ClientDashboard.PaymentInfo> getRecentPayments(List<Reservation> reservations) {
        return reservations.stream()
                .filter(r -> ReservationStatus.COMPLETED.equals(r.getStatus()) || ReservationStatus.CONFIRMED.equals(r.getStatus()))
                .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
                .limit(10)
                .map(r -> ClientDashboard.PaymentInfo.builder()
                        .paymentId(r.getId())
                        .paymentDate(r.getCreatedAt())
                        .description("Location " + r.getConfirmationNumber())
                        .amount(r.getTotalPrice())
                        .status("PAID")
                        .confirmationNumber(r.getConfirmationNumber())
                        .build())
                .collect(Collectors.toList());
    }

    private String determineMembershipBadge(long completedCount) {
        if (completedCount >= 20) return "VIP";
        if (completedCount >= 5) return "Régulier";
        return "Nouveau";
    }

    private Double calculateAverageDuration(List<Reservation> reservations) {
        List<Reservation> completed = reservations.stream()
                .filter(r -> ReservationStatus.COMPLETED.equals(r.getStatus()))
                .collect(Collectors.toList());

        if (completed.isEmpty()) return 0.0;

        double totalDays = completed.stream()
                .mapToDouble(r -> ChronoUnit.DAYS.between(r.getStartDate(), r.getEndDate()))
                .sum();

        return totalDays / completed.size();
    }
}
