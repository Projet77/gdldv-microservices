// ========== RentalController.java ==========
package com.gdldv.rental.controller;

import com.gdldv.rental.dto.*;
import com.gdldv.rental.entity.Rental;
import com.gdldv.rental.service.ContractService;
import com.gdldv.rental.service.InspectionService;
import com.gdldv.rental.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
@Tag(name = "Locations", description = "API de gestion des locations (Check-out/Check-in)")
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RentalController {

    private static final Logger logger = LoggerFactory.getLogger(RentalController.class);

    @Autowired
    private RentalService rentalService;

    @Autowired
    private ContractService contractService;

    @Autowired
    private InspectionService inspectionService;

    /**
     * GDLDV-468: Créer un dossier de location (Check-out)
     * POST /api/rentals/checkout
     */
    @PostMapping("/checkout")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    @Operation(summary = "Check-out - Créer un dossier de location",
            description = "Enregistrer le début d'une location avec inspection du véhicule (GDLDV-468, GDLDV-474, GDLDV-479)")
    public ResponseEntity<?> checkOut(@Valid @RequestBody CheckOutRequest request) {
        try {
            logger.info("Demande de check-out pour la réservation ID: {}", request.getReservationId());

            RentalDTO rental = rentalService.checkOut(request);

            logger.info("Check-out effectué avec succès: Location ID={}", rental.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Check-out effectué avec succès", rental));

        } catch (RuntimeException e) {
            logger.error("Erreur lors du check-out: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage()));
        } catch (Exception e) {
            logger.error("Erreur inattendue lors du check-out", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Une erreur est survenue lors du check-out"));
        }
    }

    /**
     * GDLDV-485: Clôturer la location (Check-in)
     * POST /api/rentals/checkin
     */
    @PostMapping("/checkin")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    @Operation(summary = "Check-in - Clôturer une location",
            description = "Enregistrer le retour d'un véhicule avec inspection et calcul des frais (GDLDV-485, GDLDV-490, GDLDV-494)")
    public ResponseEntity<?> checkIn(@Valid @RequestBody CheckInRequest request) {
        try {
            logger.info("Demande de check-in pour la location ID: {}", request.getRentalId());

            RentalDTO rental = rentalService.checkIn(request);

            logger.info("Check-in effectué avec succès: Location ID={}", rental.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Check-in effectué avec succès", rental));

        } catch (RuntimeException e) {
            logger.error("Erreur lors du check-in: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage()));
        } catch (Exception e) {
            logger.error("Erreur inattendue lors du check-in", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Une erreur est survenue lors du check-in"));
        }
    }

    /**
     * GDLDV-494: Calculer les frais supplémentaires
     * GET /api/rentals/{id}/additional-charges
     */
    @GetMapping("/{id}/additional-charges")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    @Operation(summary = "Calculer les frais supplémentaires",
            description = "Calculer retards, carburant, kilométrage excédentaire (GDLDV-494)")
    public ResponseEntity<?> getAdditionalCharges(@PathVariable Long id) {
        try {
            logger.info("Calcul des frais supplémentaires pour la location ID: {}", id);

            Rental rental = rentalService.getRentalEntityById(id);
            AdditionalChargesDTO charges = rentalService.calculateAdditionalCharges(rental);

            return ResponseEntity.ok(new ApiResponse<>(true, "Frais calculés avec succès", charges));

        } catch (RuntimeException e) {
            logger.error("Erreur lors du calcul des frais: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage()));
        } catch (Exception e) {
            logger.error("Erreur inattendue lors du calcul des frais", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Une erreur est survenue"));
        }
    }

    /**
     * Récupérer une location par ID
     * GET /api/rentals/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Récupérer une location par ID")
    public ResponseEntity<?> getRentalById(@PathVariable Long id) {
        try {
            RentalDTO rental = rentalService.getRentalById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Location récupérée avec succès", rental));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    /**
     * Récupérer les locations d'un utilisateur
     * GET /api/rentals/user/{userId}
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Récupérer les locations d'un utilisateur")
    public ResponseEntity<?> getUserRentals(@PathVariable Long userId) {
        try {
            List<RentalDTO> rentals = rentalService.getUserRentals(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Locations récupérées avec succès", rentals));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Une erreur est survenue"));
        }
    }

    /**
     * Récupérer les locations en retard
     * GET /api/rentals/overdue
     */
    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    @Operation(summary = "Récupérer les locations en retard")
    public ResponseEntity<?> getOverdueRentals() {
        try {
            List<RentalDTO> rentals = rentalService.getOverdueRentals();
            return ResponseEntity.ok(new ApiResponse<>(true, "Locations en retard récupérées", rentals));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Une erreur est survenue"));
        }
    }

    /**
     * GDLDV-474: Récupérer le contrat d'une location
     * GET /api/rentals/{id}/contract
     */
    @GetMapping("/{id}/contract")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Récupérer le contrat de location",
            description = "Récupérer le contrat généré pour une location (GDLDV-474)")
    public ResponseEntity<?> getContract(@PathVariable Long id) {
        try {
            // Récupérer le contrat par rental ID
            // Pour simplifier, on utilise l'ID de location
            RentalContractDTO contract = contractService.getContractById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Contrat récupéré avec succès", contract));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    /**
     * GDLDV-474: Signer le contrat électroniquement
     * POST /api/rentals/contract/sign
     */
    @PostMapping("/contract/sign")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Signer le contrat électroniquement",
            description = "Signer le contrat de location avec signature électronique (GDLDV-474)")
    public ResponseEntity<?> signContract(@Valid @RequestBody SignContractRequest request,
                                          HttpServletRequest httpRequest) {
        try {
            String ipAddress = httpRequest.getRemoteAddr();
            RentalContractDTO contract = contractService.signContract(request, ipAddress);
            return ResponseEntity.ok(new ApiResponse<>(true, "Contrat signé avec succès", contract));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    /**
     * GDLDV-479 / GDLDV-490: Récupérer les inspections d'une location
     * GET /api/rentals/{id}/inspections
     */
    @GetMapping("/{id}/inspections")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Récupérer les inspections du véhicule",
            description = "Récupérer les inspections check-out et check-in (GDLDV-479, GDLDV-490)")
    public ResponseEntity<?> getInspections(@PathVariable Long id) {
        try {
            List<VehicleInspectionDTO> inspections = inspectionService.getInspectionsByRental(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Inspections récupérées avec succès", inspections));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Une erreur est survenue"));
        }
    }

    /**
     * Comparer les inspections pour détecter les dommages
     * GET /api/rentals/{id}/damages
     */
    @GetMapping("/{id}/damages")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    @Operation(summary = "Comparer les inspections et détecter les dommages")
    public ResponseEntity<?> compareDamages(@PathVariable Long id) {
        try {
            List<String> damages = inspectionService.compareInspections(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Comparaison effectuée", damages));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Une erreur est survenue"));
        }
    }


}