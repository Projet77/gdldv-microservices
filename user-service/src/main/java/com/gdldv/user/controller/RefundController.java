package com.gdldv.user.controller;

import com.gdldv.user.dto.RefundApprovalRequest;
import com.gdldv.user.dto.RefundDetails;
import com.gdldv.user.dto.RefundPolicy;
import com.gdldv.user.service.RefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * GDLDV-590: Refund Controller (Sprint 3)
 * API pour la gestion des remboursements
 */
@RestController
@RequestMapping("/api/refunds")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Tag(name = "Refunds", description = "API de gestion des remboursements")
public class RefundController {

    private final RefundService refundService;

    /**
     * Obtenir la politique de refund pour une réservation
     * GET /api/refunds/policy/{reservationId}
     */
    @GetMapping("/policy/{reservationId}")
    @Operation(summary = "Obtenir la politique de refund",
            description = "Calculer le pourcentage de remboursement basé sur le temps restant")
    public ResponseEntity<RefundPolicy> getRefundPolicy(@PathVariable Long reservationId) {
        log.info("Getting refund policy for reservation: {}", reservationId);

        try {
            RefundPolicy policy = refundService.getRefundPolicy(reservationId);
            return ResponseEntity.ok(policy);
        } catch (IllegalArgumentException e) {
            log.error("Error getting refund policy: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Traiter un remboursement
     * POST /api/refunds/process/{reservationId}
     */
    @PostMapping("/process/{reservationId}")
    @Operation(summary = "Traiter un remboursement",
            description = "Calculer et traiter le remboursement via Stripe")
    public ResponseEntity<RefundDetails> processRefund(@PathVariable Long reservationId) {
        log.info("Processing refund for reservation: {}", reservationId);

        try {
            RefundDetails details = refundService.processRefund(reservationId);
            return ResponseEntity.ok(details);
        } catch (IllegalArgumentException e) {
            log.error("Error processing refund: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error processing refund: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Demander un refund (création d'une demande d'approbation)
     * POST /api/refunds/request/{reservationId}?reason=...
     */
    @PostMapping("/request/{reservationId}")
    @Operation(summary = "Demander un remboursement",
            description = "Créer une demande de remboursement en attente d'approbation admin")
    public ResponseEntity<RefundApprovalRequest> requestRefund(
            @PathVariable Long reservationId,
            @RequestParam String reason) {

        log.info("Creating refund request for reservation: {} with reason: {}", reservationId, reason);

        try {
            RefundApprovalRequest request = refundService.createApprovalRequest(reservationId, reason);
            return ResponseEntity.ok(request);
        } catch (IllegalArgumentException e) {
            log.error("Error creating refund request: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
