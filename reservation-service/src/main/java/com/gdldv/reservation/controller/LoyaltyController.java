package com.gdldv.reservation.controller;

import com.gdldv.reservation.service.LoyaltyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/loyalty")
@RequiredArgsConstructor
@Tag(name = "Loyalty Management", description = "Gestion de la fidélité")
public class LoyaltyController {

    private final LoyaltyService loyaltyService;

    @GetMapping("/config")
    @Operation(summary = "Obtenir la configuration fidélité")
    public ResponseEntity<Map<String, Double>> getLoyaltyConfig() {
        return ResponseEntity.ok(Map.of("discountPercentage", loyaltyService.getCurrentLoyaltyDiscount()));
    }

    @PutMapping("/config")
    @Operation(summary = "Mettre à jour la réduction fidélité")
    public ResponseEntity<Void> updateLoyaltyConfig(@RequestBody Map<String, Double> payload) {
        if (payload.containsKey("discountPercentage")) {
            loyaltyService.setLoyaltyDiscount(payload.get("discountPercentage"));
        }
        return ResponseEntity.ok().build();
    }
}
