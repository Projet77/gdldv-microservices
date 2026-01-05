package com.gdldv.vehicle.controller;

import com.gdldv.vehicle.dto.AdvancedSearchCriteria;
import com.gdldv.vehicle.dto.CreateVehicleRequest;
import com.gdldv.vehicle.dto.UpdateVehicleRequest;
import com.gdldv.vehicle.dto.VehicleResponse;
import com.gdldv.vehicle.entity.Vehicle;
import com.gdldv.vehicle.entity.VehicleStatus;
import com.gdldv.vehicle.service.AdvancedSearchService;
import com.gdldv.vehicle.service.RecommendationService;
import com.gdldv.vehicle.service.VehicleService;

import java.util.List;
import java.util.stream.Collectors;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Vehicle Management", description = "API de gestion des véhicules")
public class VehicleController {

        private final VehicleService vehicleService;
        private final AdvancedSearchService advancedSearchService;
        private final RecommendationService recommendationService;

        /**
         * POST /api/v1/vehicles - Create a new vehicle
         */
        @PostMapping
        @Operation(summary = "Créer un nouveau véhicule", description = "Ajoute un nouveau véhicule dans le système")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Véhicule créé avec succès"),
                        @ApiResponse(responseCode = "400", description = "Données invalides"),
                        @ApiResponse(responseCode = "409", description = "Un véhicule avec cette immatriculation existe déjà")
        })
        public ResponseEntity<VehicleResponse> createVehicle(
                        @Valid @RequestBody CreateVehicleRequest request) {
                log.info("REST request to create vehicle: {}", request.getLicensePlate());
                VehicleResponse response = vehicleService.createVehicle(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        /**
         * GET /api/v1/vehicles - Get all active vehicles with pagination
         */
        @GetMapping
        @Operation(summary = "Récupérer tous les véhicules actifs", description = "Liste paginée de tous les véhicules actifs")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
        })
        public ResponseEntity<Page<VehicleResponse>> getAllVehicles(
                        @Parameter(description = "Numéro de page (commence à 0)") @RequestParam(defaultValue = "0") int page,
                        @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") int size,
                        @Parameter(description = "Champ de tri") @RequestParam(defaultValue = "createdAt") String sortBy,
                        @Parameter(description = "Direction du tri (ASC ou DESC)") @RequestParam(defaultValue = "DESC") String sortDirection) {

                log.info("REST request to get all vehicles - page: {}, size: {}, sortBy: {}, sortDirection: {}",
                                page, size, sortBy, sortDirection);

                Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC
                                : Sort.Direction.DESC;
                Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

                Page<VehicleResponse> vehicles = vehicleService.getAllVehicles(pageable);
                return ResponseEntity.ok(vehicles);
        }

        /**
         * GET /api/v1/vehicles/{id} - Get vehicle by ID
         */
        @GetMapping("/{id}")
        @Operation(summary = "Récupérer un véhicule par ID", description = "Récupère les détails d'un véhicule spécifique")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Véhicule trouvé"),
                        @ApiResponse(responseCode = "404", description = "Véhicule non trouvé")
        })
        public ResponseEntity<VehicleResponse> getVehicleById(
                        @Parameter(description = "ID du véhicule") @PathVariable Long id) {
                log.info("REST request to get vehicle by ID: {}", id);
                VehicleResponse response = vehicleService.getVehicleById(id);
                return ResponseEntity.ok(response);
        }

        /**
         * GET /api/v1/vehicles/search - Search vehicles with filters
         */
        @GetMapping("/search")
        @Operation(summary = "Rechercher des véhicules", description = "Recherche avancée avec filtres multiples")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Résultats de recherche récupérés avec succès")
        })
        public ResponseEntity<Page<VehicleResponse>> searchVehicles(
                        @Parameter(description = "Marque (recherche partielle)") @RequestParam(required = false) String brand,
                        @Parameter(description = "Modèle (recherche partielle)") @RequestParam(required = false) String model,
                        @Parameter(description = "Catégorie") @RequestParam(required = false) String category,
                        @Parameter(description = "Statut") @RequestParam(required = false) VehicleStatus status,
                        @Parameter(description = "Prix minimum par jour") @RequestParam(required = false) Double minPrice,
                        @Parameter(description = "Prix maximum par jour") @RequestParam(required = false) Double maxPrice,
                        @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
                        @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") int size,
                        @Parameter(description = "Champ de tri") @RequestParam(defaultValue = "createdAt") String sortBy,
                        @Parameter(description = "Direction du tri") @RequestParam(defaultValue = "DESC") String sortDirection) {

                log.info("REST request to search vehicles - brand: {}, model: {}, category: {}, status: {}, minPrice: {}, maxPrice: {}",
                                brand, model, category, status, minPrice, maxPrice);

                Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC
                                : Sort.Direction.DESC;
                Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

                Page<VehicleResponse> results = vehicleService.searchVehicles(brand, model, category, status, minPrice,
                                maxPrice, pageable);
                return ResponseEntity.ok(results);
        }

        /**
         * GET /api/v1/vehicles/status/{status} - Get vehicles by status
         */
        @GetMapping("/status/{status}")
        @Operation(summary = "Récupérer les véhicules par statut", description = "Liste paginée des véhicules filtrés par statut")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
        })
        public ResponseEntity<Page<VehicleResponse>> getVehiclesByStatus(
                        @Parameter(description = "Statut du véhicule") @PathVariable VehicleStatus status,
                        @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
                        @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") int size) {

                log.info("REST request to get vehicles by status: {}", status);
                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

                Page<VehicleResponse> vehicles = vehicleService.getVehiclesByStatus(status, pageable);
                return ResponseEntity.ok(vehicles);
        }

        /**
         * GET /api/v1/vehicles/category/{category} - Get vehicles by category
         */
        @GetMapping("/category/{category}")
        @Operation(summary = "Récupérer les véhicules par catégorie", description = "Liste paginée des véhicules filtrés par catégorie")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
        })
        public ResponseEntity<Page<VehicleResponse>> getVehiclesByCategory(
                        @Parameter(description = "Catégorie du véhicule") @PathVariable String category,
                        @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
                        @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") int size) {

                log.info("REST request to get vehicles by category: {}", category);
                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

                Page<VehicleResponse> vehicles = vehicleService.getVehiclesByCategory(category, pageable);
                return ResponseEntity.ok(vehicles);
        }

        /**
         * PUT /api/v1/vehicles/{id} - Update vehicle
         */
        @PutMapping("/{id}")
        @Operation(summary = "Mettre à jour un véhicule", description = "Modifie les informations d'un véhicule existant")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Véhicule mis à jour avec succès"),
                        @ApiResponse(responseCode = "404", description = "Véhicule non trouvé"),
                        @ApiResponse(responseCode = "409", description = "Conflit avec l'immatriculation")
        })
        public ResponseEntity<VehicleResponse> updateVehicle(
                        @Parameter(description = "ID du véhicule") @PathVariable Long id,
                        @Valid @RequestBody UpdateVehicleRequest request) {
                log.info("REST request to update vehicle with ID: {}", id);
                VehicleResponse response = vehicleService.updateVehicle(id, request);
                return ResponseEntity.ok(response);
        }

        /**
         * DELETE /api/v1/vehicles/{id} - Deactivate vehicle (soft delete)
         */
        @DeleteMapping("/{id}")
        @Operation(summary = "Désactiver un véhicule", description = "Désactive un véhicule (suppression logique)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Véhicule désactivé avec succès"),
                        @ApiResponse(responseCode = "404", description = "Véhicule non trouvé")
        })
        public ResponseEntity<Void> deactivateVehicle(
                        @Parameter(description = "ID du véhicule") @PathVariable Long id) {
                log.info("REST request to deactivate vehicle with ID: {}", id);
                vehicleService.deactivateVehicle(id);
                return ResponseEntity.noContent().build();
        }

        // ==================== GDLDV-520: RECHERCHE AVANCÉE ====================

        /**
         * GDLDV-520: Recherche avancée avec filtres multiples
         */
        @PostMapping("/search/advanced")
        @Operation(summary = "Recherche avancée", description = "Recherche de véhicules avec critères multiples")
        public ResponseEntity<Page<VehicleResponse>> advancedSearch(
                        @RequestBody AdvancedSearchCriteria criteria,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {

                log.info("Recherche avancée avec critères: {}", criteria);
                Pageable pageable = PageRequest.of(page, size);
                Page<Vehicle> results = advancedSearchService.advancedSearch(criteria, pageable);

                return ResponseEntity.ok(results.map(this::mapToResponse));
        }

        /**
         * GDLDV-520: Rechercher les véhicules les mieux notés
         */
        @GetMapping("/search/top-rated")
        @Operation(summary = "Véhicules les mieux notés", description = "Récupère les véhicules avec les meilleures notes")
        public ResponseEntity<Page<VehicleResponse>> searchTopRated(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {

                log.info("Recherche des véhicules les mieux notés");
                Pageable pageable = PageRequest.of(page, size);
                Page<Vehicle> results = advancedSearchService.searchTopRated(pageable);

                return ResponseEntity.ok(results.map(this::mapToResponse));
        }

        /**
         * GDLDV-520: Rechercher les véhicules les plus populaires
         */
        @GetMapping("/search/popular")
        @Operation(summary = "Véhicules les plus populaires", description = "Récupère les véhicules avec le plus d'avis")
        public ResponseEntity<Page<VehicleResponse>> searchPopular(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {

                log.info("Recherche des véhicules les plus populaires");
                Pageable pageable = PageRequest.of(page, size);
                Page<Vehicle> results = advancedSearchService.searchMostPopular(pageable);

                return ResponseEntity.ok(results.map(this::mapToResponse));
        }

        // ==================== GDLDV-525: RECOMMANDATIONS ====================

        /**
         * GDLDV-525: Recommandations personnalisées pour un utilisateur
         */
        @GetMapping("/recommendations")
        @Operation(summary = "Recommandations personnalisées", description = "Obtenir des recommandations basées sur les préférences de l'utilisateur")
        public ResponseEntity<List<VehicleResponse>> getRecommendations(
                        @RequestHeader("X-User-Id") Long userId,
                        @RequestParam(defaultValue = "10") int count) {

                log.info("Génération de recommandations pour l'utilisateur: {}", userId);
                List<Vehicle> recommendations = recommendationService.getPersonalizedRecommendations(userId, count);

                return ResponseEntity.ok(
                                recommendations.stream()
                                                .map(this::mapToResponse)
                                                .collect(Collectors.toList()));
        }

        /**
         * GDLDV-525: Nouveaux véhicules
         */
        @GetMapping("/recommendations/new")
        @Operation(summary = "Nouveaux véhicules", description = "Récupère les véhicules récemment ajoutés")
        public ResponseEntity<List<VehicleResponse>> getNewVehicles(
                        @RequestParam(defaultValue = "10") int count) {

                log.info("Récupération des {} nouveaux véhicules", count);
                List<Vehicle> newVehicles = recommendationService.getNewVehicles(count);

                return ResponseEntity.ok(
                                newVehicles.stream()
                                                .map(this::mapToResponse)
                                                .collect(Collectors.toList()));
        }

        /**
         * GDLDV-525: Véhicules en tendance
         */
        @GetMapping("/recommendations/trending")
        @Operation(summary = "Véhicules en tendance", description = "Récupère les véhicules les plus populaires du moment")
        public ResponseEntity<List<VehicleResponse>> getTrendingVehicles(
                        @RequestParam(defaultValue = "10") int count) {

                log.info("Récupération des {} véhicules en tendance", count);
                List<Vehicle> trending = recommendationService.getTrendingVehicles(count);

                return ResponseEntity.ok(
                                trending.stream()
                                                .map(this::mapToResponse)
                                                .collect(Collectors.toList()));
        }

        /**
         * GDLDV-525: Véhicules similaires
         */
        @GetMapping("/{id}/similar")
        @Operation(summary = "Véhicules similaires", description = "Récupère des véhicules similaires à un véhicule donné")
        public ResponseEntity<List<VehicleResponse>> getSimilarVehicles(
                        @PathVariable Long id,
                        @RequestParam(defaultValue = "5") int count) {

                log.info("Récupération de véhicules similaires au véhicule: {}", id);
                List<Vehicle> similar = recommendationService.getSimilarVehicles(id, count);

                return ResponseEntity.ok(
                                similar.stream()
                                                .map(this::mapToResponse)
                                                .collect(Collectors.toList()));
        }

        // ==================== MAPPER ====================

        private VehicleResponse mapToResponse(Vehicle vehicle) {
                return VehicleResponse.builder()
                                .id(vehicle.getId())
                                .brand(vehicle.getBrand())
                                .model(vehicle.getModel())
                                .licensePlate(vehicle.getLicensePlate())
                                .color(vehicle.getColor())
                                .year(vehicle.getYear())
                                .mileage(vehicle.getMileage())
                                .dailyPrice(vehicle.getDailyPrice())
                                .category(vehicle.getCategory())
                                .fuelType(vehicle.getFuelType())
                                .transmission(vehicle.getTransmission())
                                .description(vehicle.getDescription())
                                .seats(vehicle.getSeats())
                                .babySeat(vehicle.getBabySeat())
                                .images(vehicle.getImages())
                                .status(vehicle.getStatus())
                                .isActive(vehicle.getIsActive())
                                .createdAt(vehicle.getCreatedAt())
                                .updatedAt(vehicle.getUpdatedAt())
                                .build();
        }
}
