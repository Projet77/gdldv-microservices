package com.gdldv.user.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour le dashboard CLIENT
 * Contient toutes les informations nécessaires pour l'affichage du dashboard client
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDashboard {
    // Informations utilisateur
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime memberSince;
    private String membershipBadge; // Nouveau, Régulier, VIP
    private Double averageRating;

    // Statistiques personnelles
    private Integer activeRentals;
    private Integer totalRentals;
    private Double totalSpent;
    private Double averageSpentPerRental;
    private Double averageDuration; // en jours
    private Integer totalKilometers;
    private String favoriteCategory; // SUV, Sedan, etc.

    // Réservations en cours
    private List<ActiveRentalInfo> currentRentals;

    // Historique récent
    private List<RentalHistoryInfo> recentHistory;

    // Véhicules favoris
    private List<FavoriteVehicleInfo> favorites;

    // Paiements récents
    private List<PaymentInfo> recentPayments;

    // Totaux financiers
    private Double monthlyTotal;
    private Double yearlyTotal;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ActiveRentalInfo {
        private Long reservationId;
        private String confirmationNumber;
        private Long vehicleId;
        private String vehicleName;
        private String vehicleImage;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private String pickupLocation;
        private String returnLocation;
        private Double dailyPrice;
        private Double totalPrice;
        private String status;
        private Integer daysRemaining;
        private Integer hoursRemaining;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RentalHistoryInfo {
        private Long reservationId;
        private String confirmationNumber;
        private String vehicleName;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Double totalPrice;
        private String status; // COMPLETED, CANCELLED
        private Double rating;
        private String review;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FavoriteVehicleInfo {
        private Long vehicleId;
        private String name;
        private String image;
        private Double dailyPrice;
        private Double averageRating;
        private Boolean available;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentInfo {
        private Long paymentId;
        private LocalDateTime paymentDate;
        private String description;
        private Double amount;
        private String status; // PAID, PENDING, REFUNDED
        private String confirmationNumber;
    }
}
