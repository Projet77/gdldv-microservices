package com.gdldv.reservation.controller;

import com.gdldv.reservation.dto.PricingBreakdown;
import com.gdldv.reservation.dto.PricingRequest;
import com.gdldv.reservation.service.PricingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pricing")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Tag(name = "Pricing", description = "API de calcul de tarification")
public class PricingController {

    private final PricingService pricingService;

    /**
     * POST /api/pricing/calculate
     * Calcule le prix total d'une réservation avec détails (base, options, taxes)
     */
    @PostMapping("/calculate")
    @Operation(summary = "Calculer le prix d'une réservation")
    public ResponseEntity<PricingBreakdown> calculatePricing(@Valid @RequestBody PricingRequest request) {
        log.info("Received pricing request: {}", request);

        try {
            PricingBreakdown breakdown = pricingService.calculatePricing(request);
            return ResponseEntity.ok(breakdown);
        } catch (RuntimeException e) {
            log.error("Error calculating pricing: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
