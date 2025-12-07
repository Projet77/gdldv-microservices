package com.gdldv.reservation.controller;

import com.gdldv.reservation.entity.Reservation;
import com.gdldv.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservation", description = "API de gestion des réservations")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    @Operation(summary = "Récupérer toutes les réservations")
    public ResponseEntity<List<Reservation>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une réservation par ID")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Récupérer les réservations d'un utilisateur")
    public ResponseEntity<List<Reservation>> getReservationsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(reservationService.getReservationsByUserId(userId));
    }

    @GetMapping("/vehicle/{vehicleId}")
    @Operation(summary = "Récupérer les réservations d'un véhicule")
    public ResponseEntity<List<Reservation>> getReservationsByVehicleId(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(reservationService.getReservationsByVehicleId(vehicleId));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Récupérer les réservations par statut")
    public ResponseEntity<List<Reservation>> getReservationsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(reservationService.getReservationsByStatus(status));
    }

    @GetMapping("/check-availability")
    @Operation(summary = "Vérifier la disponibilité d'un véhicule")
    public ResponseEntity<Boolean> checkAvailability(
            @RequestParam Long vehicleId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        boolean available = reservationService.isVehicleAvailable(vehicleId, startDate, endDate);
        return ResponseEntity.ok(available);
    }

    @PostMapping
    @Operation(summary = "Créer une nouvelle réservation")
    public ResponseEntity<Reservation> createReservation(@Valid @RequestBody Reservation reservation) {
        Reservation createdReservation = reservationService.createReservation(reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReservation);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une réservation")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @Valid @RequestBody Reservation reservation) {
        try {
            Reservation updatedReservation = reservationService.updateReservation(id, reservation);
            return ResponseEntity.ok(updatedReservation);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une réservation")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/confirm")
    @Operation(summary = "Confirmer une réservation")
    public ResponseEntity<Void> confirmReservation(@PathVariable Long id) {
        reservationService.confirmReservation(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/complete")
    @Operation(summary = "Clôturer une réservation")
    public ResponseEntity<Void> completeReservation(@PathVariable Long id) {
        reservationService.completeReservation(id);
        return ResponseEntity.ok().build();
    }
}
