package com.gdldv.user.repository;

import com.gdldv.user.entity.Reservation;
import com.gdldv.user.entity.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);
    Page<Reservation> findByUserId(Long userId, Pageable pageable);
    Page<Reservation> findByUserIdAndStatus(Long userId, ReservationStatus status, Pageable pageable);
}
