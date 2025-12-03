package com.gdldv.reservation.repository;

import com.gdldv.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserId(Long userId);

    List<Reservation> findByVehicleId(Long vehicleId);

    List<Reservation> findByStatus(String status);

    List<Reservation> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

    List<Reservation> findByVehicleIdAndStartDateBetween(Long vehicleId, LocalDate startDate, LocalDate endDate);
}
