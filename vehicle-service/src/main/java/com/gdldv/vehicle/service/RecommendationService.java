package com.gdldv.vehicle.service;

import com.gdldv.vehicle.entity.Vehicle;
import com.gdldv.vehicle.entity.VehicleStatus;
import com.gdldv.vehicle.repository.FavoriteRepository;
import com.gdldv.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * GDLDV-525: Service de recommandations personnalisées
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final VehicleRepository vehicleRepository;
    private final FavoriteRepository favoriteRepository;
    private final ReviewService reviewService;

    /**
     * Obtenir des recommandations personnalisées pour un utilisateur
     * Basé sur ses favoris et préférences
     */
    @Transactional(readOnly = true)
    public List<Vehicle> getPersonalizedRecommendations(Long userId, int count) {
        log.info("Génération de recommandations personnalisées pour l'utilisateur: {}", userId);

        // Récupérer les favoris de l'utilisateur
        List<Vehicle> userFavorites = favoriteRepository.findVehiclesByUserId(userId, Pageable.unpaged()).getContent();

        if (userFavorites.isEmpty()) {
            log.info("Aucun favori trouvé, retour des véhicules les mieux notés");
            return getTopRatedVehicles(count);
        }

        // Analyser les catégories préférées de l'utilisateur
        String favoriteCategory = analyzeFavoriteCategory(userFavorites);
        log.info("Catégorie préférée de l'utilisateur: {}", favoriteCategory);

        // Trouver des véhicules similaires (même catégorie, non favoris, bien notés)
        List<Vehicle> allVehicles = vehicleRepository.findAll();

        List<Vehicle> recommendations = allVehicles.stream()
                .filter(v -> v.getIsActive() && v.getStatus() == VehicleStatus.AVAILABLE)
                .filter(v -> !userFavorites.contains(v)) // Exclure les favoris existants
                .filter(v -> favoriteCategory.equalsIgnoreCase(v.getCategory())) // Même catégorie
                .sorted((v1, v2) -> {
                    Double r1 = reviewService.getAverageRating(v1.getId());
                    Double r2 = reviewService.getAverageRating(v2.getId());
                    return Double.compare(r2 != null ? r2 : 0.0, r1 != null ? r1 : 0.0);
                })
                .limit(count)
                .collect(Collectors.toList());

        log.info("Recommandations générées: {} véhicules", recommendations.size());
        return recommendations;
    }

    /**
     * Obtenir les véhicules les plus récents
     */
    @Transactional(readOnly = true)
    public List<Vehicle> getNewVehicles(int count) {
        log.info("Récupération des {} véhicules les plus récents", count);

        return vehicleRepository.findAll().stream()
                .filter(v -> v.getIsActive() && v.getStatus() == VehicleStatus.AVAILABLE)
                .sorted((v1, v2) -> v2.getCreatedAt().compareTo(v1.getCreatedAt()))
                .limit(count)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les véhicules les mieux notés
     */
    @Transactional(readOnly = true)
    public List<Vehicle> getTopRatedVehicles(int count) {
        log.info("Récupération des {} véhicules les mieux notés", count);

        return vehicleRepository.findAll().stream()
                .filter(v -> v.getIsActive() && v.getStatus() == VehicleStatus.AVAILABLE)
                .filter(v -> reviewService.getReviewCount(v.getId()) > 0) // Au moins un avis
                .sorted((v1, v2) -> {
                    Double r1 = reviewService.getAverageRating(v1.getId());
                    Double r2 = reviewService.getAverageRating(v2.getId());
                    return Double.compare(r2 != null ? r2 : 0.0, r1 != null ? r1 : 0.0);
                })
                .limit(count)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les véhicules en tendance (basé sur le nombre d'avis récents)
     */
    @Transactional(readOnly = true)
    public List<Vehicle> getTrendingVehicles(int count) {
        log.info("Récupération des {} véhicules en tendance", count);

        return vehicleRepository.findAll().stream()
                .filter(v -> v.getIsActive() && v.getStatus() == VehicleStatus.AVAILABLE)
                .filter(v -> reviewService.getReviewCount(v.getId()) > 0) // Au moins un avis
                .sorted((v1, v2) -> {
                    Integer c1 = reviewService.getReviewCount(v1.getId());
                    Integer c2 = reviewService.getReviewCount(v2.getId());
                    return Integer.compare(c2, c1);
                })
                .limit(count)
                .collect(Collectors.toList());
    }

    /**
     * Analyser la catégorie préférée d'un utilisateur basée sur ses favoris
     */
    private String analyzeFavoriteCategory(List<Vehicle> userFavorites) {
        if (userFavorites.isEmpty()) {
            return "Standard"; // Catégorie par défaut
        }

        // Compter les occurrences de chaque catégorie
        Map<String, Long> categoryCount = userFavorites.stream()
                .filter(v -> v.getCategory() != null)
                .collect(Collectors.groupingBy(Vehicle::getCategory, Collectors.counting()));

        // Retourner la catégorie la plus fréquente
        return categoryCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Standard");
    }

    /**
     * Obtenir des recommandations basées sur un véhicule spécifique (véhicules similaires)
     */
    @Transactional(readOnly = true)
    public List<Vehicle> getSimilarVehicles(Long vehicleId, int count) {
        log.info("Récupération de véhicules similaires au véhicule: {}", vehicleId);

        Vehicle referenceVehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Véhicule non trouvé: " + vehicleId));

        return vehicleRepository.findAll().stream()
                .filter(v -> v.getIsActive() && v.getStatus() == VehicleStatus.AVAILABLE)
                .filter(v -> !v.getId().equals(vehicleId)) // Exclure le véhicule de référence
                .filter(v -> referenceVehicle.getCategory().equalsIgnoreCase(v.getCategory())) // Même catégorie
                .filter(v -> Math.abs(v.getDailyPrice() - referenceVehicle.getDailyPrice()) <= 10000) // Prix similaire (±10000 FCFA)
                .sorted(Comparator.comparing(Vehicle::getDailyPrice))
                .limit(count)
                .collect(Collectors.toList());
    }
}
