package com.gdldv.reservation.service;

import com.gdldv.reservation.client.VehicleClient;
import com.gdldv.reservation.dto.VehicleDTO;
import com.gdldv.reservation.entity.Reservation;
import com.gdldv.reservation.entity.ReservationStatus;
import com.gdldv.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final VehicleClient vehicleClient;
    private final PaymentService paymentService;

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    public List<Reservation> getReservationsByUserId(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    public List<Reservation> getReservationsByVehicleId(Long vehicleId) {
        return reservationRepository.findByVehicleId(vehicleId);
    }

    public List<Reservation> getReservationsByStatus(ReservationStatus status) {
        return reservationRepository.findByStatus(status);
    }

    public Optional<Reservation> getReservationByPaymentIntentId(String paymentIntentId) {
        return reservationRepository.findByStripePaymentIntentId(paymentIntentId);
    }

    @Transactional
    public Reservation createReservation(Reservation reservation) {
        // Récupérer les informations du véhicule via Feign Client
        VehicleDTO vehicle = vehicleClient.getVehicleById(reservation.getVehicleId());

        // Calculer le prix total
        long days = ChronoUnit.DAYS.between(reservation.getStartDate(), reservation.getEndDate());
        double totalPrice = days * vehicle.getDailyPrice();
        reservation.setTotalPrice(totalPrice);

        // Définir le statut par défaut
        if (reservation.getStatus() == null) {
            reservation.setStatus(ReservationStatus.PENDING);
        }

        return reservationRepository.save(reservation);
    }

    @Transactional
    public String initializePayment(Long reservationId, String customerEmail) {
        log.info("Initializing payment for reservation: {}", reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + reservationId));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new RuntimeException("Reservation is not in PENDING status");
        }

        // Créer une session de paiement Stripe
        String clientSecret = paymentService.createCheckoutSession(
                reservationId,
                reservation.getTotalPrice(),
                customerEmail
        );

        log.info("Payment intent created for reservation: {}", reservationId);
        return clientSecret;
    }

    @Transactional
    public void confirmPayment(String paymentIntentId) {
        log.info("Confirming payment for intent: {}", paymentIntentId);

        Reservation reservation = reservationRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new RuntimeException("Reservation not found for payment intent: " + paymentIntentId));

        boolean paymentSucceeded = paymentService.confirmPayment(paymentIntentId);

        if (paymentSucceeded) {
            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservationRepository.save(reservation);
            log.info("Reservation {} confirmed after successful payment", reservation.getId());
        } else {
            log.warn("Payment confirmation failed for reservation {}", reservation.getId());
        }
    }

    @Transactional
    public void updatePaymentIntentId(Long reservationId, String paymentIntentId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + reservationId));

        reservation.setStripePaymentIntentId(paymentIntentId);
        reservationRepository.save(reservation);
        log.info("Updated payment intent ID for reservation {}", reservationId);
    }

    @Transactional
    public Reservation updateReservation(Long id, Reservation reservationDetails) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + id));

        reservation.setVehicleId(reservationDetails.getVehicleId());
        reservation.setUserId(reservationDetails.getUserId());
        reservation.setStartDate(reservationDetails.getStartDate());
        reservation.setEndDate(reservationDetails.getEndDate());
        reservation.setStatus(reservationDetails.getStatus());
        reservation.setNotes(reservationDetails.getNotes());

        // Recalculer le prix total
        VehicleDTO vehicle = vehicleClient.getVehicleById(reservation.getVehicleId());
        long days = ChronoUnit.DAYS.between(reservation.getStartDate(), reservation.getEndDate());
        reservation.setTotalPrice(days * vehicle.getDailyPrice());

        return reservationRepository.save(reservation);
    }

    @Transactional
    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    @Transactional
    public void cancelReservation(Long reservationId) {
        log.info("Cancelling reservation: {}", reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + reservationId));

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new RuntimeException("Reservation is already cancelled");
        }

        if (reservation.getStatus() == ReservationStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel a completed reservation");
        }

        // Calculer le remboursement si la réservation a été payée
        if (reservation.getStatus() == ReservationStatus.CONFIRMED ||
            reservation.getStatus() == ReservationStatus.ACTIVE) {

            if (reservation.getStripePaymentIntentId() != null) {
                double refundAmount = calculateRefundAmount(reservation);

                if (refundAmount > 0) {
                    log.info("Processing refund of {} XOF for reservation {}", refundAmount, reservationId);
                    paymentService.refundPayment(reservation.getStripePaymentIntentId(), refundAmount);
                }
            }
        }

        // Marquer la réservation comme annulée
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        log.info("Reservation {} cancelled successfully", reservationId);
    }

    private double calculateRefundAmount(Reservation reservation) {
        LocalDate today = LocalDate.now();
        long daysUntilStart = ChronoUnit.DAYS.between(today, reservation.getStartDate());

        double refundPercentage;

        if (daysUntilStart > 2) {
            // Plus de 48h avant le début : remboursement à 100%
            refundPercentage = 1.0;
            log.info("Refund policy: >48h - 100% refund");
        } else if (daysUntilStart >= 1) {
            // Entre 24h et 48h avant le début : remboursement à 75%
            refundPercentage = 0.75;
            log.info("Refund policy: 24-48h - 75% refund");
        } else {
            // Moins de 24h avant le début : remboursement à 50%
            refundPercentage = 0.5;
            log.info("Refund policy: <24h - 50% refund");
        }

        return reservation.getTotalPrice() * refundPercentage;
    }

    @Transactional
    public void confirmReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + id));
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);
    }

    @Transactional
    public void completeReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + id));
        reservation.setStatus(ReservationStatus.COMPLETED);
        reservationRepository.save(reservation);
    }

    public boolean isVehicleAvailable(Long vehicleId, LocalDate startDate, LocalDate endDate) {
        List<Reservation> existingReservations = reservationRepository
                .findByVehicleIdAndStartDateBetween(vehicleId, startDate, endDate);
        return existingReservations.isEmpty();
    }
}
