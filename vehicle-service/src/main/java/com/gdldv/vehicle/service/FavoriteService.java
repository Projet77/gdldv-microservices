package com.gdldv.vehicle.service;

import com.gdldv.vehicle.entity.Favorite;
import com.gdldv.vehicle.entity.Vehicle;
import com.gdldv.vehicle.repository.FavoriteRepository;
import com.gdldv.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * GDLDV-515: Service pour la gestion des favoris (wishlist)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final VehicleRepository vehicleRepository;

    /**
     * Ajouter un véhicule aux favoris
     */
    @Transactional
    public void addFavorite(Long userId, Long vehicleId) {
        log.info("Ajout du véhicule {} aux favoris de l'utilisateur: {}", vehicleId, userId);

        // Vérifier que le véhicule existe
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Véhicule non trouvé: " + vehicleId));

        // Vérifier si déjà en favoris
        if (favoriteRepository.existsByUserIdAndVehicleId(userId, vehicleId)) {
            log.warn("Le véhicule {} est déjà dans les favoris de l'utilisateur {}", vehicleId, userId);
            return;
        }

        Favorite favorite = Favorite.builder()
                .userId(userId)
                .vehicleId(vehicleId)
                .build();

        favoriteRepository.save(favorite);
        log.info("Véhicule {} ajouté aux favoris avec succès", vehicleId);
    }

    /**
     * Retirer un véhicule des favoris
     */
    @Transactional
    public void removeFavorite(Long userId, Long vehicleId) {
        log.info("Retrait du véhicule {} des favoris de l'utilisateur: {}", vehicleId, userId);

        favoriteRepository.deleteByUserIdAndVehicleId(userId, vehicleId);

        log.info("Véhicule {} retiré des favoris avec succès", vehicleId);
    }

    /**
     * Récupérer tous les véhicules favoris d'un utilisateur
     */
    @Transactional(readOnly = true)
    public Page<Vehicle> getUserFavorites(Long userId, Pageable pageable) {
        log.info("Récupération des favoris pour l'utilisateur: {}", userId);

        return favoriteRepository.findVehiclesByUserId(userId, pageable);
    }

    /**
     * Vérifier si un véhicule est dans les favoris d'un utilisateur
     */
    @Transactional(readOnly = true)
    public Boolean isFavorite(Long userId, Long vehicleId) {
        log.info("Vérification si le véhicule {} est en favori pour l'utilisateur: {}", vehicleId, userId);

        return favoriteRepository.existsByUserIdAndVehicleId(userId, vehicleId);
    }

    /**
     * Compter le nombre de favoris pour un véhicule (popularité)
     */
    @Transactional(readOnly = true)
    public Integer getFavoriteCount(Long vehicleId) {
        log.info("Comptage des favoris pour le véhicule: {}", vehicleId);

        Integer count = favoriteRepository.countByVehicleId(vehicleId);
        return count != null ? count : 0;
    }
}
