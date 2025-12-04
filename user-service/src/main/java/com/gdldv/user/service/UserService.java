package com.gdldv.user.service;

import com.gdldv.user.dto.UpdateProfileRequest;
import com.gdldv.user.dto.UserProfileResponse;
import com.gdldv.user.entity.User;
import com.gdldv.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * GDLDV-458: Consulter son profil
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile() {
        User user = getCurrentUser();
        logger.info("Consultation du profil pour l'utilisateur: ID={}, Email={}", user.getId(), user.getEmail());
        return new UserProfileResponse(user);
    }

    /**
     * GDLDV-458: Modifier son profil
     */
    @Transactional
    public UserProfileResponse updateUserProfile(UpdateProfileRequest updateRequest) {
        User user = getCurrentUser();
        logger.info("Mise à jour du profil pour l'utilisateur: ID={}, Email={}", user.getId(), user.getEmail());

        // Mettre à jour uniquement les champs fournis (non null)
        if (updateRequest.getFirstName() != null && !updateRequest.getFirstName().isBlank()) {
            user.setFirstName(updateRequest.getFirstName());
        }

        if (updateRequest.getLastName() != null && !updateRequest.getLastName().isBlank()) {
            user.setLastName(updateRequest.getLastName());
        }

        if (updateRequest.getPhoneNumber() != null && !updateRequest.getPhoneNumber().isBlank()) {
            user.setPhoneNumber(updateRequest.getPhoneNumber());
        }

        if (updateRequest.getAddress() != null) {
            user.setAddress(updateRequest.getAddress());
        }

        if (updateRequest.getCity() != null) {
            user.setCity(updateRequest.getCity());
        }

        if (updateRequest.getPostalCode() != null) {
            user.setPostalCode(updateRequest.getPostalCode());
        }

        if (updateRequest.getCountry() != null) {
            user.setCountry(updateRequest.getCountry());
        }

        // Sauvegarder les modifications
        User updatedUser = userRepository.save(user);
        logger.info("Profil mis à jour avec succès pour l'utilisateur: ID={}", updatedUser.getId());

        return new UserProfileResponse(updatedUser);
    }

    /**
     * GDLDV-463: Consulter historique des locations
     * Note: Cette méthode retourne un placeholder pour l'instant.
     * L'implémentation complète nécessitera l'intégration avec le service de réservation.
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getRentalHistory() {
        User user = getCurrentUser();
        logger.info("Consultation de l'historique des locations pour l'utilisateur: ID={}, Email={}", user.getId(), user.getEmail());

        // TODO: Intégration avec le microservice de réservation
        // Pour l'instant, retourne une liste vide
        // À l'avenir, il faudra faire un appel REST au service de réservation:
        // GET /reservation-service/api/reservations/user/{userId}

        logger.warn("L'historique des locations n'est pas encore implémenté. Intégration avec reservation-service nécessaire.");

        return List.of(); // Retourne une liste vide temporairement
    }

    /**
     * Récupérer un utilisateur par ID (pour usage interne ou admin)
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + userId));

        logger.info("Récupération de l'utilisateur: ID={}, Email={}", user.getId(), user.getEmail());
        return new UserProfileResponse(user);
    }

    /**
     * Récupérer tous les utilisateurs (pour admin)
     */
    @Transactional(readOnly = true)
    public List<UserProfileResponse> getAllUsers() {
        logger.info("Récupération de tous les utilisateurs");
        return userRepository.findAll().stream()
                .map(UserProfileResponse::new)
                .toList();
    }

    /**
     * Désactiver un utilisateur (soft delete)
     */
    @Transactional
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + userId));

        user.setActive(false);
        userRepository.save(user);
        logger.info("Utilisateur désactivé: ID={}, Email={}", user.getId(), user.getEmail());
    }

    /**
     * Récupérer l'utilisateur actuellement connecté
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
}