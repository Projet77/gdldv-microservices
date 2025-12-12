package com.gdldv.reservation.controller;

import com.gdldv.reservation.dto.CreateReservationRequest;
import com.gdldv.reservation.dto.ReservationResponse;
import com.gdldv.reservation.entity.Reservation;
import com.gdldv.reservation.entity.ReservationStatus;
import com.gdldv.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
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
    public ResponseEntity<List<Reservation>> getReservationsByStatus(@PathVariable ReservationStatus status) {
        return ResponseEntity.ok(reservationService.getReservationsByStatus(status));
    }

    @GetMapping("/confirmation/{confirmationNumber}")
    @Operation(summary = "Récupérer une réservation par numéro de confirmation")
    public ResponseEntity<Reservation> getReservationByConfirmationNumber(@PathVariable String confirmationNumber) {
        return reservationService.getReservationByConfirmationNumber(confirmationNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/check-availability")
    @Operation(summary = "Vérifier la disponibilité d'un véhicule")
    public ResponseEntity<Boolean> checkAvailability(
            @RequestParam Long vehicleId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        boolean available = reservationService.isVehicleAvailable(vehicleId, startDate, endDate);
        return ResponseEntity.ok(available);
    }

    @PostMapping
    @Operation(summary = "Créer une nouvelle réservation")
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody CreateReservationRequest request) {
        log.info("Received reservation request: {}", request);
        try {
            ReservationResponse createdReservation = reservationService.createReservation(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReservation);
        } catch (RuntimeException e) {
            log.error("Error creating reservation: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une réservation")
    public ResponseEntity<ReservationResponse> updateReservation(@PathVariable Long id, @Valid @RequestBody Reservation reservation) {
        try {
            ReservationResponse updatedReservation = reservationService.updateReservation(id, reservation);
            return ResponseEntity.ok(updatedReservation);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/confirm")
    @Operation(summary = "Confirmer une réservation")
    public ResponseEntity<Void> confirmReservation(@PathVariable Long id) {
        try {
            reservationService.confirmReservation(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error confirming reservation {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/complete")
    @Operation(summary = "Compléter une réservation")
    public ResponseEntity<Void> completeReservation(@PathVariable Long id) {
        try {
            reservationService.completeReservation(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error completing reservation {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Annuler une réservation")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id) {
        try {
            reservationService.cancelReservation(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error cancelling reservation {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une réservation")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        try {
            reservationService.deleteReservation(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting reservation {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
