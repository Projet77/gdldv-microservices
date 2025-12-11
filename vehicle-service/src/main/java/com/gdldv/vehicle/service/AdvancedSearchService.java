package com.gdldv.vehicle.service;

import com.gdldv.vehicle.dto.AdvancedSearchCriteria;
import com.gdldv.vehicle.entity.Vehicle;
import com.gdldv.vehicle.entity.VehicleStatus;
import com.gdldv.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GDLDV-520: Service de recherche avancée avec filtres multiples
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdvancedSearchService {

    private final VehicleRepository vehicleRepository;
    private final ReviewService reviewService;

    /**
     * Recherche avancée avec multiples critères
     */
    @Transactional(readOnly = true)
    public Page<Vehicle> advancedSearch(AdvancedSearchCriteria criteria, Pageable pageable) {
        log.info("Recherche avancée avec critères: {}", criteria);

        // Récupérer tous les véhicules actifs
        List<Vehicle> vehicles = vehicleRepository.findByIsActiveTrue(pageable).getContent();

        // Appliquer les filtres
        List<Vehicle> filteredVehicles = vehicles.stream()
                .filter(v -> matchesCriteria(v, criteria))
                .collect(Collectors.toList());

        // Appliquer le tri
        List<Vehicle> sortedVehicles = applySorting(filteredVehicles, criteria);

        // Créer une page avec les résultats
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedVehicles.size());
        List<Vehicle> pageContent = sortedVehicles.subList(start, end);

        return new PageImpl<>(pageContent, pageable, sortedVehicles.size());
    }

    /**
     * Rechercher les véhicules les mieux notés
     */
    @Transactional(readOnly = true)
    public Page<Vehicle> searchTopRated(Pageable pageable) {
        log.info("Recherche des véhicules les mieux notés");

        List<Vehicle> vehicles = vehicleRepository.findByIsActiveTrue(pageable).getContent();

        List<Vehicle> sortedByRating = vehicles.stream()
                .filter(v -> reviewService.getReviewCount(v.getId()) > 0) // Au moins un avis
                .sorted((v1, v2) -> {
                    Double r1 = reviewService.getAverageRating(v1.getId());
                    Double r2 = reviewService.getAverageRating(v2.getId());
                    return Double.compare(r2 != null ? r2 : 0.0, r1 != null ? r1 : 0.0);
                })
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedByRating.size());
        List<Vehicle> pageContent = sortedByRating.subList(start, end);

        return new PageImpl<>(pageContent, pageable, sortedByRating.size());
    }

    /**
     * Rechercher les véhicules les plus populaires (plus d'avis)
     */
    @Transactional(readOnly = true)
    public Page<Vehicle> searchMostPopular(Pageable pageable) {
        log.info("Recherche des véhicules les plus populaires");

        List<Vehicle> vehicles = vehicleRepository.findByIsActiveTrue(pageable).getContent();

        List<Vehicle> sortedByPopularity = vehicles.stream()
                .filter(v -> reviewService.getReviewCount(v.getId()) > 0) // Au moins un avis
                .sorted((v1, v2) -> {
                    Integer c1 = reviewService.getReviewCount(v1.getId());
                    Integer c2 = reviewService.getReviewCount(v2.getId());
                    return Integer.compare(c2, c1);
                })
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedByPopularity.size());
        List<Vehicle> pageContent = sortedByPopularity.subList(start, end);

        return new PageImpl<>(pageContent, pageable, sortedByPopularity.size());
    }

    /**
     * Vérifier si un véhicule correspond aux critères
     */
    private boolean matchesCriteria(Vehicle vehicle, AdvancedSearchCriteria criteria) {
        // Filtre par catégorie
        if (criteria.getCategory() != null && !criteria.getCategory().isBlank()) {
            if (!criteria.getCategory().equalsIgnoreCase(vehicle.getCategory())) {
                return false;
            }
        }

        // Filtre par prix
        if (criteria.getMinPrice() != null && vehicle.getDailyPrice() < criteria.getMinPrice()) {
            return false;
        }
        if (criteria.getMaxPrice() != null && vehicle.getDailyPrice() > criteria.getMaxPrice()) {
            return false;
        }

        // Filtre par type de carburant
        if (criteria.getFuelType() != null && !criteria.getFuelType().isBlank()) {
            if (!criteria.getFuelType().equalsIgnoreCase(vehicle.getFuelType())) {
                return false;
            }
        }

        // Filtre par transmission
        if (criteria.getTransmission() != null && !criteria.getTransmission().isBlank()) {
            if (!criteria.getTransmission().equalsIgnoreCase(vehicle.getTransmission())) {
                return false;
            }
        }

        // Filtre par note minimale
        if (criteria.getMinRating() != null) {
            Double avgRating = reviewService.getAverageRating(vehicle.getId());
            if (avgRating == null || avgRating < criteria.getMinRating()) {
                return false;
            }
        }

        // Filtre par année
        if (criteria.getMinYear() != null && vehicle.getYear() != null && vehicle.getYear() < criteria.getMinYear()) {
            return false;
        }
        if (criteria.getMaxYear() != null && vehicle.getYear() != null && vehicle.getYear() > criteria.getMaxYear()) {
            return false;
        }

        // Filtre par kilométrage
        if (criteria.getMaxMileage() != null && vehicle.getMileage() != null && vehicle.getMileage() > criteria.getMaxMileage()) {
            return false;
        }

        // Filtre par disponibilité
        if (criteria.getAvailable() != null && criteria.getAvailable()) {
            if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
                return false;
            }
        }

        return true;
    }

    /**
     * Appliquer le tri sur les résultats
     */
    private List<Vehicle> applySorting(List<Vehicle> vehicles, AdvancedSearchCriteria criteria) {
        if (criteria.getSortBy() == null || criteria.getSortBy().isBlank()) {
            return vehicles; // Pas de tri
        }

        Comparator<Vehicle> comparator = switch (criteria.getSortBy().toLowerCase()) {
            case "price" -> Comparator.comparing(Vehicle::getDailyPrice);
            case "year" -> Comparator.comparing(Vehicle::getYear, Comparator.nullsLast(Comparator.naturalOrder()));
            case "mileage" -> Comparator.comparing(Vehicle::getMileage, Comparator.nullsLast(Comparator.naturalOrder()));
            case "rating" -> (v1, v2) -> {
                Double r1 = reviewService.getAverageRating(v1.getId());
                Double r2 = reviewService.getAverageRating(v2.getId());
                return Double.compare(r2 != null ? r2 : 0.0, r1 != null ? r1 : 0.0);
            };
            default -> Comparator.comparing(Vehicle::getId);
        };

        // Ordre de tri
        if ("desc".equalsIgnoreCase(criteria.getSortOrder())) {
            comparator = comparator.reversed();
        }

        return vehicles.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }
}
