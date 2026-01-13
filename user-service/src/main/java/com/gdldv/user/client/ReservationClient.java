package com.gdldv.user.client;

import com.gdldv.user.entity.Reservation;
import com.gdldv.user.entity.ReservationStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Feign Client pour communiquer avec le Reservation Service
 */
@FeignClient(name = "reservation-service", url = "http://localhost:8002")
public interface ReservationClient {

    /**
     * Récupère toutes les réservations
     */
    @GetMapping("/api/reservations")
    List<Reservation> getAllReservations();

    /**
     * Récupère les réservations par statut
     * @param status Le statut des réservations (PENDING, CONFIRMED, ACTIVE, COMPLETED, CANCELLED)
     */
    @GetMapping("/api/reservations/status/{status}")
    List<Reservation> getReservationsByStatus(@PathVariable("status") ReservationStatus status);

    /**
     * Récupère une réservation par ID
     */
    @GetMapping("/api/reservations/{id}")
    Reservation getReservationById(@PathVariable("id") Long id);
}
