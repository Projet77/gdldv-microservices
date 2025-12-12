package com.gdldv.vehicle.repository;

import com.gdldv.vehicle.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * GDLDV-510: Repository pour la gestion des avis
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Trouver tous les avis d'un véhicule avec pagination
     */
    Page<Review> findByVehicleId(Long vehicleId, Pageable pageable);

    /**
     * Trouver les avis d'un véhicule triés par utilité (helpful count)
     */
    @Query("SELECT r FROM Review r WHERE r.vehicleId = :vehicleId ORDER BY r.helpfulCount DESC")
    Page<Review> findByVehicleIdOrderByHelpfulCount(@Param("vehicleId") Long vehicleId, Pageable pageable);

    /**
     * Trouver les avis d'un véhicule triés par date (plus récents d'abord)
     */
    Page<Review> findByVehicleIdOrderByCreatedAtDesc(Long vehicleId, Pageable pageable);

    /**
     * Trouver les avis d'un véhicule triés par note (meilleures notes d'abord)
     */
    Page<Review> findByVehicleIdOrderByRatingDesc(Long vehicleId, Pageable pageable);

    /**
     * Calculer la note moyenne d'un véhicule
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.vehicleId = :vehicleId")
    Double getAverageRatingByVehicleId(@Param("vehicleId") Long vehicleId);

    /**
     * Compter le nombre d'avis pour un véhicule
     */
    Integer countByVehicleId(Long vehicleId);

    /**
     * Vérifier si un utilisateur a déjà laissé un avis pour un véhicule
     */
    boolean existsByUserIdAndVehicleId(Long userId, Long vehicleId);
}
