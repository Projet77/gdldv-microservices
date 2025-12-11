package com.gdldv.vehicle.service;

import com.gdldv.vehicle.dto.CreateReviewRequest;
import com.gdldv.vehicle.entity.Review;
import com.gdldv.vehicle.entity.Vehicle;
import com.gdldv.vehicle.repository.ReviewRepository;
import com.gdldv.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * GDLDV-510: Service pour la gestion des avis et notations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final VehicleRepository vehicleRepository;

    /**
     * Créer un nouvel avis
     */
    @Transactional
    public Review createReview(CreateReviewRequest request) {
        log.info("Création d'un avis pour le véhicule: {} par l'utilisateur: {}",
                request.getVehicleId(), request.getUserId());

        // Vérifier que le véhicule existe
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Véhicule non trouvé: " + request.getVehicleId()));

        // Vérifier si l'utilisateur a déjà laissé un avis pour ce véhicule
        if (reviewRepository.existsByUserIdAndVehicleId(request.getUserId(), request.getVehicleId())) {
            throw new IllegalStateException("Vous avez déjà laissé un avis pour ce véhicule");
        }

        Review review = Review.builder()
                .vehicleId(request.getVehicleId())
                .userId(request.getUserId())
                .rating(request.getRating())
                .title(request.getTitle())
                .comment(request.getComment())
                .cleanliness(request.getCleanliness())
                .condition(request.getCondition())
                .comfort(request.getComfort())
                .drivability(request.getDrivability())
                .rentalDays(request.getRentalDays())
                .mileageDriven(request.getMileageDriven())
                .build();

        Review savedReview = reviewRepository.save(review);
        log.info("Avis créé avec succès: ID={}", savedReview.getId());

        // Mettre à jour la note moyenne du véhicule (optionnel)
        updateVehicleAverageRating(request.getVehicleId());

        return savedReview;
    }

    /**
     * Récupérer tous les avis d'un véhicule avec pagination
     */
    @Transactional(readOnly = true)
    public Page<Review> getVehicleReviews(Long vehicleId, Pageable pageable) {
        log.info("Récupération des avis pour le véhicule: {}", vehicleId);
        return reviewRepository.findByVehicleId(vehicleId, pageable);
    }

    /**
     * Récupérer les avis d'un véhicule avec tri personnalisé
     */
    @Transactional(readOnly = true)
    public Page<Review> getVehicleReviewsSorted(Long vehicleId, String sortBy, Pageable pageable) {
        log.info("Récupération des avis pour le véhicule: {} avec tri: {}", vehicleId, sortBy);

        return switch (sortBy.toLowerCase()) {
            case "helpful" -> reviewRepository.findByVehicleIdOrderByHelpfulCount(vehicleId, pageable);
            case "recent" -> reviewRepository.findByVehicleIdOrderByCreatedAtDesc(vehicleId, pageable);
            case "rating" -> reviewRepository.findByVehicleIdOrderByRatingDesc(vehicleId, pageable);
            default -> reviewRepository.findByVehicleId(vehicleId, pageable);
        };
    }

    /**
     * Calculer la note moyenne d'un véhicule
     */
    @Transactional(readOnly = true)
    public Double getAverageRating(Long vehicleId) {
        log.info("Calcul de la note moyenne pour le véhicule: {}", vehicleId);
        Double avgRating = reviewRepository.getAverageRatingByVehicleId(vehicleId);
        return avgRating != null ? avgRating : 0.0;
    }

    /**
     * Compter le nombre d'avis pour un véhicule
     */
    @Transactional(readOnly = true)
    public Integer getReviewCount(Long vehicleId) {
        log.info("Comptage des avis pour le véhicule: {}", vehicleId);
        Integer count = reviewRepository.countByVehicleId(vehicleId);
        return count != null ? count : 0;
    }

    /**
     * Marquer un avis comme utile
     */
    @Transactional
    public void markAsHelpful(Long reviewId) {
        log.info("Marquage de l'avis comme utile: {}", reviewId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Avis non trouvé: " + reviewId));

        review.setHelpfulCount(review.getHelpfulCount() + 1);
        reviewRepository.save(review);

        log.info("Avis {} marqué comme utile. Nouveau compteur: {}", reviewId, review.getHelpfulCount());
    }

    /**
     * Marquer un avis comme non utile
     */
    @Transactional
    public void markAsNotHelpful(Long reviewId) {
        log.info("Marquage de l'avis comme non utile: {}", reviewId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Avis non trouvé: " + reviewId));

        review.setNotHelpfulCount(review.getNotHelpfulCount() + 1);
        reviewRepository.save(review);

        log.info("Avis {} marqué comme non utile. Nouveau compteur: {}", reviewId, review.getNotHelpfulCount());
    }

    /**
     * Mettre à jour la note moyenne du véhicule (méthode privée)
     * Peut être utilisée pour sauvegarder la moyenne dans la table vehicles si nécessaire
     */
    private void updateVehicleAverageRating(Long vehicleId) {
        Double avgRating = getAverageRating(vehicleId);
        log.info("Note moyenne mise à jour pour le véhicule {}: {}/5", vehicleId, avgRating);
        // TODO: Optionnel - sauvegarder la moyenne dans la table vehicles si nécessaire
    }
}
