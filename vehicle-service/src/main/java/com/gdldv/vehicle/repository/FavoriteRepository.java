package com.gdldv.vehicle.repository;

import com.gdldv.vehicle.entity.Favorite;
import com.gdldv.vehicle.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * GDLDV-515: Repository pour la gestion des favoris
 */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    /**
     * Vérifier si un véhicule est dans les favoris d'un utilisateur
     */
    boolean existsByUserIdAndVehicleId(Long userId, Long vehicleId);

    /**
     * Supprimer un favori par utilisateur et véhicule
     */
    void deleteByUserIdAndVehicleId(Long userId, Long vehicleId);

    /**
     * Récupérer tous les véhicules favoris d'un utilisateur
     */
    @Query("SELECT v FROM Vehicle v INNER JOIN Favorite f ON v.id = f.vehicleId " +
            "WHERE f.userId = :userId ORDER BY f.createdAt DESC")
    Page<Vehicle> findVehiclesByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * Compter le nombre de favoris pour un véhicule
     */
    Integer countByVehicleId(Long vehicleId);

    /**
     * Récupérer tous les favoris d'un utilisateur (entités Favorite)
     */
    Page<Favorite> findByUserId(Long userId, Pageable pageable);
}
