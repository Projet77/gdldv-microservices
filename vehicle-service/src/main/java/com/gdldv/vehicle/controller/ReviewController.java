package com.gdldv.vehicle.controller;

import com.gdldv.vehicle.dto.CreateReviewRequest;
import com.gdldv.vehicle.dto.ReviewResponse;
import com.gdldv.vehicle.dto.VehicleRatingResponse;
import com.gdldv.vehicle.dto.VehicleResponse;
import com.gdldv.vehicle.entity.Review;
import com.gdldv.vehicle.entity.Vehicle;
import com.gdldv.vehicle.service.FavoriteService;
import com.gdldv.vehicle.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * GDLDV-510: Contrôleur pour la gestion des avis et notations
 */
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ReviewController {

    private final ReviewService reviewService;
    private final FavoriteService favoriteService;

    /**
     * GDLDV-510: Créer un nouvel avis
     */
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody CreateReviewRequest request) {
        log.info("Requête de création d'avis reçue pour le véhicule: {}", request.getVehicleId());

        Review review = reviewService.createReview(request);

        return new ResponseEntity<>(mapToResponse(review), HttpStatus.CREATED);
    }

    /**
     * GDLDV-510: Récupérer les avis d'un véhicule
     */
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<Page<ReviewResponse>> getVehicleReviews(
            @PathVariable Long vehicleId,
            @RequestParam(defaultValue = "recent") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Récupération des avis pour le véhicule: {} | Tri: {} | Page: {}", vehicleId, sortBy, page);

        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviews = reviewService.getVehicleReviewsSorted(vehicleId, sortBy, pageable);

        return ResponseEntity.ok(reviews.map(this::mapToResponse));
    }

    /**
     * GDLDV-510: Récupérer la note moyenne d'un véhicule
     */
    @GetMapping("/vehicle/{vehicleId}/rating")
    public ResponseEntity<VehicleRatingResponse> getVehicleRating(@PathVariable Long vehicleId) {
        log.info("Récupération de la note moyenne pour le véhicule: {}", vehicleId);

        Double avgRating = reviewService.getAverageRating(vehicleId);
        Integer count = reviewService.getReviewCount(vehicleId);

        VehicleRatingResponse response = VehicleRatingResponse.builder()
                .averageRating(avgRating)
                .reviewCount(count)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * GDLDV-510: Marquer un avis comme utile
     */
    @PostMapping("/{reviewId}/helpful")
    public ResponseEntity<Void> markAsHelpful(@PathVariable Long reviewId) {
        log.info("Marquage de l'avis {} comme utile", reviewId);

        reviewService.markAsHelpful(reviewId);

        return ResponseEntity.ok().build();
    }

    /**
     * GDLDV-510: Marquer un avis comme non utile
     */
    @PostMapping("/{reviewId}/not-helpful")
    public ResponseEntity<Void> markAsNotHelpful(@PathVariable Long reviewId) {
        log.info("Marquage de l'avis {} comme non utile", reviewId);

        reviewService.markAsNotHelpful(reviewId);

        return ResponseEntity.ok().build();
    }

    // ==================== GDLDV-515: FAVORIS ====================

    /**
     * GDLDV-515: Ajouter un véhicule aux favoris
     */
    @PostMapping("/vehicles/{vehicleId}/favorite")
    public ResponseEntity<Void> addFavorite(
            @PathVariable Long vehicleId,
            @RequestHeader("X-User-Id") Long userId) {

        log.info("Ajout du véhicule {} aux favoris de l'utilisateur: {}", vehicleId, userId);
        favoriteService.addFavorite(userId, vehicleId);
        return ResponseEntity.ok().build();
    }

    /**
     * GDLDV-515: Retirer un véhicule des favoris
     */
    @DeleteMapping("/vehicles/{vehicleId}/favorite")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable Long vehicleId,
            @RequestHeader("X-User-Id") Long userId) {

        log.info("Retrait du véhicule {} des favoris de l'utilisateur: {}", vehicleId, userId);
        favoriteService.removeFavorite(userId, vehicleId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GDLDV-515: Récupérer mes favoris
     */
    @GetMapping("/favorites")
    public ResponseEntity<Page<VehicleResponse>> getFavorites(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Récupération des favoris pour l'utilisateur: {}", userId);
        Pageable pageable = PageRequest.of(page, size);
        Page<Vehicle> favorites = favoriteService.getUserFavorites(userId, pageable);
        return ResponseEntity.ok(favorites.map(this::mapVehicleToResponse));
    }

    /**
     * GDLDV-515: Vérifier si un véhicule est en favoris
     */
    @GetMapping("/vehicles/{vehicleId}/is-favorite")
    public ResponseEntity<Boolean> isFavorite(
            @PathVariable Long vehicleId,
            @RequestHeader("X-User-Id") Long userId) {

        log.info("Vérification si le véhicule {} est en favori pour l'utilisateur: {}", vehicleId, userId);
        Boolean isFav = favoriteService.isFavorite(userId, vehicleId);
        return ResponseEntity.ok(isFav);
    }

    /**
     * GDLDV-515: Récupérer le nombre de favoris pour un véhicule
     */
    @GetMapping("/vehicles/{vehicleId}/favorite-count")
    public ResponseEntity<Integer> getFavoriteCount(@PathVariable Long vehicleId) {
        log.info("Récupération du nombre de favoris pour le véhicule: {}", vehicleId);
        Integer count = favoriteService.getFavoriteCount(vehicleId);
        return ResponseEntity.ok(count);
    }

    // ==================== MAPPERS ====================

    /**
     * Mapper Review vers ReviewResponse
     */
    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .vehicleId(review.getVehicleId())
                .userId(review.getUserId())
                .rating(review.getRating())
                .title(review.getTitle())
                .comment(review.getComment())
                .cleanliness(review.getCleanliness())
                .condition(review.getCondition())
                .comfort(review.getComfort())
                .drivability(review.getDrivability())
                .rentalDays(review.getRentalDays())
                .mileageDriven(review.getMileageDriven())
                .helpfulCount(review.getHelpfulCount())
                .notHelpfulCount(review.getNotHelpfulCount())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    /**
     * Mapper Vehicle vers VehicleResponse
     */
    private VehicleResponse mapVehicleToResponse(Vehicle vehicle) {
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
                .status(vehicle.getStatus())
                .isActive(vehicle.getIsActive())
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }
}
