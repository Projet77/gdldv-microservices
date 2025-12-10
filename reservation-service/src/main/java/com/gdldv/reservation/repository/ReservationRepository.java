package com.gdldv.reservation.repository;

import com.gdldv.reservation.entity.Reservation;
import com.gdldv.reservation.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByConfirmationNumber(String confirmationNumber);

    List<Reservation> findByUserId(Long userId);

    List<Reservation> findByVehicleId(Long vehicleId);

    List<Reservation> findByStatus(ReservationStatus status);

    // Trouver les réservations qui chevauchent les dates
    @Query("SELECT r FROM Reservation r WHERE r.vehicleId = :vehicleId " +
           "AND r.status != 'CANCELLED' " +
           "AND ((r.startDate <= :endDate AND r.endDate >= :startDate))")
    List<Reservation> findConflictingReservations(
        @Param("vehicleId") Long vehicleId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);

    List<Reservation> findByStartDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Reservation> findByVehicleIdAndStartDateBetween(Long vehicleId, LocalDateTime startDate, LocalDateTime endDate);
}
