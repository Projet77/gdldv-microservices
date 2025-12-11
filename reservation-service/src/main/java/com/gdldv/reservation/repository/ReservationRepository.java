package com.gdldv.reservation.repository;

import com.gdldv.reservation.entity.Reservation;
import com.gdldv.reservation.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserId(Long userId);

    List<Reservation> findByVehicleId(Long vehicleId);

    List<Reservation> findByStatus(ReservationStatus status);

    Optional<Reservation> findByStripePaymentIntentId(String stripePaymentIntentId);

    List<Reservation> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

    List<Reservation> findByVehicleIdAndStartDateBetween(Long vehicleId, LocalDate startDate, LocalDate endDate);
}
