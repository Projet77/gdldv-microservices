package com.gdldv.reservation.controller;

import com.gdldv.reservation.dto.ApiResponse;
import com.gdldv.reservation.entity.DiscountRule;
import com.gdldv.reservation.repository.DiscountRuleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discounts")
@Tag(name = "Discount Rules", description = "API de gestion des règles de réduction (Admin uniquement)")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Slf4j
public class DiscountController {

    private final DiscountRuleRepository discountRuleRepository;

    /**
     * Get all discount rules
     * GET /api/discounts
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get all discount rules", description = "Retrieve all discount rules (Admin only)")
    public ResponseEntity<ApiResponse<List<DiscountRule>>> getAllDiscountRules() {
        try {
            log.info("Fetching all discount rules");
            List<DiscountRule> rules = discountRuleRepository.findAll();
            return ResponseEntity.ok(new ApiResponse<>(true, "Règles récupérées avec succès", rules));
        } catch (Exception e) {
            log.error("Error fetching discount rules", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération des règles", null));
        }
    }

    /**
     * Get active discount rules only
     * GET /api/discounts/active
     */
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get active discount rules", description = "Retrieve only active discount rules")
    public ResponseEntity<ApiResponse<List<DiscountRule>>> getActiveDiscountRules() {
        try {
            log.info("Fetching active discount rules");
            List<DiscountRule> rules = discountRuleRepository.findByIsActiveTrue();
            return ResponseEntity.ok(new ApiResponse<>(true, "Règles actives récupérées avec succès", rules));
        } catch (Exception e) {
            log.error("Error fetching active discount rules", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération des règles actives", null));
        }
    }

    /**
     * Create a new discount rule
     * POST /api/discounts
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Create discount rule", description = "Create a new discount rule (Admin only)")
    public ResponseEntity<ApiResponse<DiscountRule>> createDiscountRule(@Valid @RequestBody DiscountRule rule) {
        try {
            log.info("Creating new discount rule: {}", rule.getRuleName());

            if (rule.getIsActive() == null) {
                rule.setIsActive(true);
            }

            DiscountRule savedRule = discountRuleRepository.save(rule);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Règle créée avec succès", savedRule));
        } catch (Exception e) {
            log.error("Error creating discount rule", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la création de la règle", null));
        }
    }

    /**
     * Update a discount rule
     * PUT /api/discounts/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Update discount rule", description = "Update an existing discount rule (Admin only)")
    public ResponseEntity<ApiResponse<DiscountRule>> updateDiscountRule(
            @PathVariable Long id,
            @Valid @RequestBody DiscountRule updatedRule) {
        try {
            log.info("Updating discount rule: ID={}", id);

            DiscountRule existingRule = discountRuleRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Règle non trouvée avec l'ID: " + id));

            existingRule.setRuleName(updatedRule.getRuleName());
            existingRule.setMinCompletedRentals(updatedRule.getMinCompletedRentals());
            existingRule.setDiscountPercentage(updatedRule.getDiscountPercentage());
            if (updatedRule.getIsActive() != null) {
                existingRule.setIsActive(updatedRule.getIsActive());
            }

            DiscountRule savedRule = discountRuleRepository.save(existingRule);
            return ResponseEntity.ok(new ApiResponse<>(true, "Règle mise à jour avec succès", savedRule));
        } catch (RuntimeException e) {
            log.error("Discount rule not found: ID={}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error updating discount rule", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la mise à jour de la règle", null));
        }
    }

    /**
     * Toggle discount rule active status
     * PATCH /api/discounts/{id}/toggle
     */
    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Toggle discount rule", description = "Enable or disable a discount rule (Admin only)")
    public ResponseEntity<ApiResponse<DiscountRule>> toggleDiscountRule(@PathVariable Long id) {
        try {
            log.info("Toggling discount rule: ID={}", id);

            DiscountRule rule = discountRuleRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Règle non trouvée avec l'ID: " + id));

            rule.setIsActive(!rule.getIsActive());
            DiscountRule savedRule = discountRuleRepository.save(rule);

            String status = savedRule.getIsActive() ? "activée" : "désactivée";
            return ResponseEntity.ok(new ApiResponse<>(true, "Règle " + status + " avec succès", savedRule));
        } catch (RuntimeException e) {
            log.error("Discount rule not found: ID={}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error toggling discount rule", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors du changement de statut", null));
        }
    }

    /**
     * Delete a discount rule
     * DELETE /api/discounts/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Delete discount rule", description = "Delete a discount rule (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteDiscountRule(@PathVariable Long id) {
        try {
            log.info("Deleting discount rule: ID={}", id);

            if (!discountRuleRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Règle non trouvée", null));
            }

            discountRuleRepository.deleteById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Règle supprimée avec succès", null));
        } catch (Exception e) {
            log.error("Error deleting discount rule", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la suppression de la règle", null));
        }
    }
}
