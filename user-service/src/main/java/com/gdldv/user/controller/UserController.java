package com.gdldv.user.controller;

import com.gdldv.user.dto.ApiResponse;
import com.gdldv.user.dto.UpdateProfileRequest;
import com.gdldv.user.dto.UserProfileResponse;
import com.gdldv.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Utilisateurs", description = "API de gestion des profils utilisateurs (ÉPIC-3)")
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * GDLDV-458: Consulter son profil
     * GET /api/users/profile
     */
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Consulter son profil", description = "Récupérer les informations du profil de l'utilisateur connecté (GDLDV-458)")
    public ResponseEntity<?> getUserProfile() {
        try {
            logger.info("Demande de consultation du profil");

            UserProfileResponse profile = userService.getUserProfile();

            logger.info("Profil récupéré avec succès pour l'utilisateur: ID={}", profile.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Profil récupéré avec succès", profile));

        } catch (Exception e) {
            logger.error("Erreur lors de la récupération du profil", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Une erreur est survenue lors de la récupération du profil"));
        }
    }

    /**
     * GDLDV-458: Modifier son profil
     * PUT /api/users/profile
     */
    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Modifier son profil", description = "Mettre à jour les informations du profil de l'utilisateur connecté (GDLDV-458)")
    public ResponseEntity<?> updateUserProfile(@Valid @RequestBody UpdateProfileRequest updateRequest) {
        try {
            logger.info("Demande de mise à jour du profil");

            UserProfileResponse updatedProfile = userService.updateUserProfile(updateRequest);

            logger.info("Profil mis à jour avec succès pour l'utilisateur: ID={}", updatedProfile.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Profil mis à jour avec succès", updatedProfile));

        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du profil", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Une erreur est survenue lors de la mise à jour du profil"));
        }
    }

    /**
     * GDLDV-463: Consulter historique des locations
     * GET /api/users/rental-history
     */
    @GetMapping("/rental-history")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Consulter l'historique des locations", description = "Récupérer l'historique des locations de l'utilisateur connecté (GDLDV-463)")
    public ResponseEntity<?> getRentalHistory() {
        try {
            logger.info("Demande de consultation de l'historique des locations");

            List<Map<String, Object>> history = userService.getRentalHistory();

            logger.info("Historique des locations récupéré avec succès");
            return ResponseEntity.ok(new ApiResponse<>(true, "Historique des locations récupéré avec succès", history));

        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de l'historique des locations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Une erreur est survenue lors de la récupération de l'historique"));
        }
    }

    /**
     * Récupérer un utilisateur par ID (Admin uniquement)
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    @Operation(summary = "Récupérer un utilisateur par ID", description = "Récupérer les informations d'un utilisateur spécifique (Admin/Employé)")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            logger.info("Demande de récupération de l'utilisateur avec ID: {}", id);

            UserProfileResponse user = userService.getUserById(id);

            return ResponseEntity.ok(new ApiResponse<>(true, "Utilisateur récupéré avec succès", user));

        } catch (RuntimeException e) {
            logger.error("Utilisateur non trouvé: ID={}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage()));
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de l'utilisateur", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Une erreur est survenue"));
        }
    }

    /**
     * Récupérer tous les utilisateurs (Admin uniquement)
     * GET /api/users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Récupérer tous les utilisateurs", description = "Liste de tous les utilisateurs (Admin uniquement)")
    public ResponseEntity<?> getAllUsers() {
        try {
            logger.info("Demande de récupération de tous les utilisateurs");

            List<UserProfileResponse> users = userService.getAllUsers();

            return ResponseEntity.ok(new ApiResponse<>(true, "Utilisateurs récupérés avec succès", users));

        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des utilisateurs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Une erreur est survenue"));
        }
    }

    /**
     * Désactiver un utilisateur (Admin uniquement)
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Désactiver un utilisateur", description = "Désactiver un compte utilisateur (soft delete) (Admin uniquement)")
    public ResponseEntity<?> deactivateUser(@PathVariable Long id) {
        try {
            logger.info("Demande de désactivation de l'utilisateur avec ID: {}", id);

            userService.deactivateUser(id);

            return ResponseEntity.ok(new ApiResponse<>(true, "Utilisateur désactivé avec succès"));

        } catch (RuntimeException e) {
            logger.error("Utilisateur non trouvé: ID={}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage()));
        } catch (Exception e) {
            logger.error("Erreur lors de la désactivation de l'utilisateur", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Une erreur est survenue"));
        }
    }
}