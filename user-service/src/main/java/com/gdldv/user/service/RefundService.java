package com.gdldv.user.service;

import com.gdldv.user.dto.RefundApprovalRequest;
import com.gdldv.user.dto.RefundDetails;
import com.gdldv.user.dto.RefundPolicy;
import com.gdldv.user.entity.Reservation;
import com.gdldv.user.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * GDLDV-590: Refund Management Service (Sprint 3)
 * Service pour gérer les remboursements avec politique de refund
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RefundService {

    private final ReservationRepository reservationRepository;
    private final PaymentService paymentService;

    /**
     * Obtenir la politique de refund basée sur le temps restant avant le début
     */
    public RefundPolicy getRefundPolicy(Long reservationId) {
        log.info("Getting refund policy for reservation: {}", reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));

        long hoursUntilStart = ChronoUnit.HOURS.between(
                LocalDateTime.now(),
                reservation.getStartDate()
        );

        RefundPolicy policy;
        if (hoursUntilStart > 48) {
            policy = RefundPolicy.FULL_REFUND;      // 100%
        } else if (hoursUntilStart > 24) {
            policy = RefundPolicy.PARTIAL_REFUND;   // 75%
        } else if (hoursUntilStart > 0) {
            policy = RefundPolicy.LOW_REFUND;       // 50%
        } else {
            policy = RefundPolicy.NO_REFUND;        // 0%
        }

        log.info("Refund policy for reservation {}: {} (hours until start: {})",
                reservationId, policy, hoursUntilStart);

        return policy;
    }

    /**
     * Traiter un remboursement
     */
    @Transactional
    public RefundDetails processRefund(Long reservationId) {
        log.info("Processing refund for reservation: {}", reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));

        RefundPolicy policy = getRefundPolicy(reservationId);

        double originalPrice = reservation.getTotalPrice();
        double refundAmount = calculateRefundAmount(originalPrice, policy);

        String refundId = null;

        // Traiter refund Stripe seulement si montant > 0
        if (refundAmount > 0 && reservation.getStripePaymentIntentId() != null) {
            refundId = paymentService.refundPayment(
                    reservation.getStripePaymentIntentId(),
                    refundAmount
            );
        }

        // Mettre à jour le statut de la réservation
        // reservation.setStatus(ReservationStatus.CANCELLED);
        // reservationRepository.save(reservation);

        RefundDetails details = RefundDetails.builder()
                .reservationId(reservationId)
                .originalAmount(originalPrice)
                .refundPolicy(policy)
                .refundPercentage(getRefundPercentage(policy))
                .refundAmount(refundAmount)
                .stripeRefundId(refundId)
                .status("COMPLETED")
                .processedAt(LocalDateTime.now())
                .build();

        log.info("Refund processed: {} FCFA ({}%)", refundAmount, getRefundPercentage(policy));

        return details;
    }

    /**
     * Calculer le montant du refund
     */
    private double calculateRefundAmount(double originalPrice, RefundPolicy policy) {
        switch (policy) {
            case FULL_REFUND:
                return originalPrice;
            case PARTIAL_REFUND:
                return originalPrice * 0.75;
            case LOW_REFUND:
                return originalPrice * 0.50;
            case NO_REFUND:
            default:
                return 0;
        }
    }

    /**
     * Obtenir le pourcentage de refund
     */
    private Double getRefundPercentage(RefundPolicy policy) {
        switch (policy) {
            case FULL_REFUND:
                return 100.0;
            case PARTIAL_REFUND:
                return 75.0;
            case LOW_REFUND:
                return 50.0;
            case NO_REFUND:
            default:
                return 0.0;
        }
    }

    /**
     * Créer une demande d'approbation de refund
     */
    public RefundApprovalRequest createApprovalRequest(Long reservationId, String reason) {
        log.info("Creating refund approval request for reservation: {}", reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));

        return RefundApprovalRequest.builder()
                .reservationId(reservationId)
                .reason(reason)
                .requestedAt(LocalDateTime.now())
                .status("PENDING")
                .build();
    }
}
