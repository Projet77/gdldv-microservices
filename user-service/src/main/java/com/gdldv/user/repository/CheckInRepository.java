package com.gdldv.user.repository;

import com.gdldv.user.entity.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, Long> {
    Optional<CheckIn> findByReservationId(Long reservationId);

    // Méthodes pour dashboards
    List<CheckIn> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
