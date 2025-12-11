package com.gdldv.rental.repository;

import com.gdldv.rental.entity.Rental;
import com.gdldv.rental.entity.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    /**
     * Trouver une location par ID de réservation
     */
    Optional<Rental> findByReservationId(Long reservationId);

    /**
     * Trouver toutes les locations d'un utilisateur
     */
    List<Rental> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Trouver toutes les locations d'un véhicule
     */
    List<Rental> findByVehicleIdOrderByStartDateDesc(Long vehicleId);

    /**
     * Trouver les locations actives d'un véhicule
     */
    @Query("SELECT r FROM Rental r WHERE r.vehicleId = :vehicleId AND r.status IN ('CHECKED_OUT', 'ACTIVE')")
    List<Rental> findActiveRentalsByVehicle(@Param("vehicleId") Long vehicleId);

    /**
     * Trouver les locations par statut
     */
    List<Rental> findByStatusOrderByCreatedAtDesc(RentalStatus status);

    /**
     * Trouver les locations en retard
     */
    @Query("SELECT r FROM Rental r WHERE r.endDate < :now AND r.status IN ('CHECKED_OUT', 'ACTIVE')")
    List<Rental> findOverdueRentals(@Param("now") LocalDateTime now);

    /**
     * GDLDV-533: Trouver les locations créées entre deux dates (pour KPI Dashboard)
     */
    List<Rental> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}