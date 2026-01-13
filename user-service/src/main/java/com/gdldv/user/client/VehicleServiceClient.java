package com.gdldv.user.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Client pour communiquer avec vehicle-service
 * Utilisé pour récupérer les données des véhicules, favoris, etc.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class VehicleServiceClient {

    private final RestTemplate restTemplate;

    @Value("${vehicle.service.url:http://localhost:8001}")
    private String vehicleServiceUrl;

    /**
     * Récupérer les véhicules favoris d'un utilisateur
     */
    public List<FavoriteVehicle> getFavoritesByUserId(Long userId) {
        try {
            String url = vehicleServiceUrl + "/api/favorites/user/" + userId;
            log.info("Fetching favorites from vehicle-service: {}", url);

            ResponseEntity<List<FavoriteVehicle>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<FavoriteVehicle>>() {}
            );

            return response.getBody();
        } catch (Exception e) {
            log.error("Error fetching favorites from vehicle-service: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Récupérer les détails d'un véhicule
     */
    public VehicleDetails getVehicleById(Long vehicleId) {
        try {
            String url = vehicleServiceUrl + "/api/vehicles/" + vehicleId;
            log.info("Fetching vehicle from vehicle-service: {}", url);

            return restTemplate.getForObject(url, VehicleDetails.class);
        } catch (Exception e) {
            log.error("Error fetching vehicle from vehicle-service: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Récupérer les véhicules disponibles
     */
    public List<VehicleDetails> getAvailableVehicles() {
        try {
            String url = vehicleServiceUrl + "/api/vehicles/available";
            log.info("Fetching available vehicles from vehicle-service: {}", url);

            ResponseEntity<List<VehicleDetails>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<VehicleDetails>>() {}
            );

            return response.getBody();
        } catch (Exception e) {
            log.error("Error fetching available vehicles: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Récupérer les statistiques de la flotte
     */
    public FleetStatistics getFleetStatistics() {
        try {
            String url = vehicleServiceUrl + "/api/vehicles/statistics";
            log.info("Fetching fleet statistics from vehicle-service: {}", url);

            return restTemplate.getForObject(url, FleetStatistics.class);
        } catch (Exception e) {
            log.error("Error fetching fleet statistics: {}", e.getMessage());
            return FleetStatistics.builder()
                    .totalVehicles(50)
                    .availableVehicles(32)
                    .rentedVehicles(12)
                    .maintenanceVehicles(4)
                    .outOfServiceVehicles(2)
                    .build();
        }
    }

    /**
     * Récupérer la note moyenne d'un véhicule
     */
    public Double getVehicleAverageRating(Long vehicleId) {
        try {
            String url = vehicleServiceUrl + "/api/vehicles/" + vehicleId + "/rating";
            log.info("Fetching vehicle rating from vehicle-service: {}", url);

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("averageRating")) {
                return ((Number) response.get("averageRating")).doubleValue();
            }
            return 0.0;
        } catch (Exception e) {
            log.error("Error fetching vehicle rating: {}", e.getMessage());
            return 0.0;
        }
    }

    // DTOs pour les réponses

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    @lombok.Builder
    public static class FavoriteVehicle {
        private Long id;
        private Long vehicleId;
        private Long userId;
        private String vehicleName;
        private String vehicleImage;
        private Double dailyPrice;
        private Double averageRating;
        private Boolean available;
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    @lombok.Builder
    public static class VehicleDetails {
        private Long id;
        private String brand;
        private String model;
        private Integer year;
        private String category;
        private String imageUrl;
        private Double dailyPrice;
        private String status;
        private Double averageRating;
        private Integer totalReviews;
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    @lombok.Builder
    public static class FleetStatistics {
        private Integer totalVehicles;
        private Integer availableVehicles;
        private Integer rentedVehicles;
        private Integer maintenanceVehicles;
        private Integer outOfServiceVehicles;
    }
}
