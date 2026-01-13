package com.gdldv.user.repository;

import com.gdldv.user.entity.CheckOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CheckOutRepository extends JpaRepository<CheckOut, Long> {
    Optional<CheckOut> findByReservationId(Long reservationId);

    // Méthodes pour dashboards
    List<CheckOut> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
