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
                    .body(new ApiResponse<>(false, "Une erreur est survenue lors de la récupération du profil", null));
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
                    .body(new ApiResponse<>(false, "Une erreur est survenue lors de la mise à jour du profil", null));
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

            Map<String, Object> history = userService.getRentalHistory();

            logger.info("Historique des locations récupéré avec succès");
            return ResponseEntity.ok(new ApiResponse<>(true, "Historique des locations récupéré avec succès", history));

        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de l'historique des locations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Une erreur est survenue lors de la récupération de l'historique", null));
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
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de l'utilisateur", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Une erreur est survenue", null));
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
                    .body(new ApiResponse<>(false, "Une erreur est survenue", null)); // Ligne 147 corrigée
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

            return ResponseEntity.ok(new ApiResponse<>(true, "Utilisateur désactivé avec succès", null));

        } catch (RuntimeException e) {
            logger.error("Utilisateur non trouvé: ID={}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Erreur lors de la désactivation de l'utilisateur", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Une erreur est survenue", null));
        }
    }
    /**
     * Récupérer un utilisateur par ID (Usage interne)
     * GET /api/users/internal/{id}
     */
    @GetMapping("/internal/{id}")
    @Operation(summary = "Récupérer un utilisateur par ID (Usage interne)", description = "Récupérer les informations d'un utilisateur spécifique pour la communication inter-services")
    public ResponseEntity<?> getUserByIdInternal(@PathVariable Long id) {
        try {
            logger.info("Demande de récupération de l'utilisateur avec ID: {} (interne)", id);

            UserProfileResponse user = userService.getUserById(id);

            return ResponseEntity.ok(new ApiResponse<>(true, "Utilisateur récupéré avec succès", user));

        } catch (RuntimeException e) {
            logger.error("Utilisateur non trouvé: ID={}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de l'utilisateur (interne)", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Une erreur est survenue", null));
        }
    }

    /**
     * Créer un utilisateur (Admin uniquement)
     * POST /api/users
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Créer un utilisateur", description = "Créer un nouvel utilisateur avec rôles (Admin uniquement)")
    public ResponseEntity<?> createUser(@Valid @RequestBody com.gdldv.user.dto.CreateUserRequest request) {
        try {
            logger.info("Admin creating user: {}", request.getEmail());

            UserProfileResponse user = userService.createUserByAdmin(request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Utilisateur créé avec succès", user));

        } catch (RuntimeException e) {
            logger.error("Error creating user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Unexpected error creating user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Une erreur est survenue", null));
        }
    }

    /**
     * Mettre à jour un utilisateur (Admin uniquement)
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Modifier un utilisateur", description = "Mettre à jour les informations d'un utilisateur (Admin uniquement)")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody com.gdldv.user.dto.UpdateUserRequest request) {
        try {
            logger.info("Admin updating user: ID={}", id);

            UserProfileResponse user = userService.updateUserByAdmin(id, request);

            return ResponseEntity.ok(new ApiResponse<>(true, "Utilisateur mis à jour avec succès", user));

        } catch (RuntimeException e) {
            logger.error("Error updating user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Unexpected error updating user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Une erreur est survenue", null));
        }
    }

    /**
     * Activer un utilisateur (Admin uniquement)
     * PATCH /api/users/{id}/activate
     */
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Activer un utilisateur", description = "Réactiver un compte utilisateur désactivé (Admin uniquement)")
    public ResponseEntity<?> activateUser(@PathVariable Long id) {
        try {
            logger.info("Admin activating user: ID={}", id);

            UserProfileResponse user = userService.activateUser(id);

            return ResponseEntity.ok(new ApiResponse<>(true, "Utilisateur activé avec succès", user));

        } catch (RuntimeException e) {
            logger.error("Error activating user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Unexpected error activating user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Une erreur est survenue", null));
        }
    }

    /**
     * Récupérer les utilisateurs par rôle (Admin uniquement)
     * GET /api/users/role/{role}
     */
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Récupérer utilisateurs par rôle", description = "Filtrer les utilisateurs par rôle (Admin uniquement)")
    public ResponseEntity<?> getUsersByRole(@PathVariable String role) {
        try {
            logger.info("Fetching users by role: {}", role);

            List<UserProfileResponse> users = userService.getUsersByRole(role);

            return ResponseEntity.ok(new ApiResponse<>(true, "Utilisateurs récupérés avec succès", users));

        } catch (Exception e) {
            logger.error("Error fetching users by role", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Une erreur est survenue", null));
        }
    }

    /**
     * Récupérer les utilisateurs archivés (Admin uniquement)
     * GET /api/users/archived
     */
    @GetMapping("/archived")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Récupérer utilisateurs archivés", description = "Liste des utilisateurs désactivés (Admin uniquement)")
    public ResponseEntity<?> getArchivedUsers() {
        try {
            logger.info("Fetching archived users");

            List<UserProfileResponse> users = userService.getArchivedUsers();

            return ResponseEntity.ok(new ApiResponse<>(true, "Utilisateurs archivés récupérés avec succès", users));

        } catch (Exception e) {
            logger.error("Error fetching archived users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Une erreur est survenue", null));
        }
    }

    // ========== DOCUMENT MANAGEMENT ENDPOINTS ==========

    /**
     * Upload identity card document
     * POST /api/users/profile/documents/identity
     */
    @PostMapping("/profile/documents/identity")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Upload identity card", description = "Upload user's identity card document")
    public ResponseEntity<?> uploadIdentityCard(@RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            logger.info("Uploading identity card document");

            com.gdldv.user.dto.DocumentUploadResponse response = userService.uploadDocument(file, "identity_card");

            return ResponseEntity.ok(new ApiResponse<>(true, "Document uploadé avec succès", response));

        } catch (IllegalArgumentException e) {
            logger.error("Invalid file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Error uploading identity card", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Une erreur est survenue lors de l'upload", null));
        }
    }

    /**
     * Upload passport document
     * POST /api/users/profile/documents/passport
     */
    @PostMapping("/profile/documents/passport")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Upload passport", description = "Upload user's passport document")
    public ResponseEntity<?> uploadPassport(@RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            logger.info("Uploading passport document");

            com.gdldv.user.dto.DocumentUploadResponse response = userService.uploadDocument(file, "passport");

            return ResponseEntity.ok(new ApiResponse<>(true, "Document uploadé avec succès", response));

        } catch (IllegalArgumentException e) {
            logger.error("Invalid file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Error uploading passport", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Une erreur est survenue lors de l'upload", null));
        }
    }

    /**
     * Upload driver license document
     * POST /api/users/profile/documents/license
     */
    @PostMapping("/profile/documents/license")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Upload driver license", description = "Upload user's driver license document")
    public ResponseEntity<?> uploadDriverLicense(@RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            logger.info("Uploading driver license document");

            com.gdldv.user.dto.DocumentUploadResponse response = userService.uploadDocument(file, "driver_license");

            return ResponseEntity.ok(new ApiResponse<>(true, "Document uploadé avec succès", response));

        } catch (IllegalArgumentException e) {
            logger.error("Invalid file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Error uploading driver license", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Une erreur est survenue lors de l'upload", null));
        }
    }

    /**
     * Get user's documents
     * GET /api/users/profile/documents
     */
    @GetMapping("/profile/documents")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get user documents", description = "Get all uploaded documents for the current user")
    public ResponseEntity<?> getUserDocuments() {
        try {
            logger.info("Fetching user documents");

            com.gdldv.user.dto.UserDocumentsResponse response = userService.getUserDocuments();

            return ResponseEntity.ok(new ApiResponse<>(true, "Documents récupérés avec succès", response));

        } catch (Exception e) {
            logger.error("Error fetching user documents", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Une erreur est survenue", null));
        }
    }

    /**
     * Delete user document
     * DELETE /api/users/profile/documents/{documentType}
     */
    @DeleteMapping("/profile/documents/{documentType}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete document", description = "Delete a specific document")
    public ResponseEntity<?> deleteDocument(@PathVariable String documentType) {
        try {
            logger.info("Deleting document: {}", documentType);

            userService.deleteDocument(documentType);

            return ResponseEntity.ok(new ApiResponse<>(true, "Document supprimé avec succès", null));

        } catch (IllegalArgumentException e) {
            logger.error("Invalid document type: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Error deleting document", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Une erreur est survenue", null));
        }
    }

    /**
     * Get documents for a specific user (Admin only)
     * GET /api/users/{userId}/documents
     */
    @GetMapping("/{userId}/documents")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get user documents (Admin)", description = "Get all documents for a specific user (Admin only)")
    public ResponseEntity<?> getUserDocumentsByAdmin(@PathVariable Long userId) {
        try {
            logger.info("Admin fetching documents for user: ID={}", userId);

            // Get user by ID first to ensure they exist
            UserProfileResponse user = userService.getUserById(userId);

            // Build response from user profile (which includes document URLs)
            // Parse documentsUploadedAt from String to LocalDateTime if present
            java.time.LocalDateTime uploadedAt = null;
            if (user.getDocumentsUploadedAt() != null && !user.getDocumentsUploadedAt().isEmpty()) {
                uploadedAt = java.time.LocalDateTime.parse(user.getDocumentsUploadedAt());
            }

            com.gdldv.user.dto.UserDocumentsResponse response = com.gdldv.user.dto.UserDocumentsResponse.builder()
                    .userId(userId)
                    .identityCardUrl(user.getIdentityCardUrl())
                    .passportUrl(user.getPassportUrl())
                    .driverLicensePhotoUrl(user.getDriverLicensePhotoUrl())
                    .documentVerificationStatus(user.getDocumentVerificationStatus())
                    .documentsUploadedAt(uploadedAt)
                    .hasIdentityCard(user.getIdentityCardUrl() != null)
                    .hasPassport(user.getPassportUrl() != null)
                    .hasDriverLicense(user.getDriverLicensePhotoUrl() != null)
                    .build();

            return ResponseEntity.ok(new ApiResponse<>(true, "Documents récupérés avec succès", response));

        } catch (Exception e) {
            logger.error("Error fetching user documents", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Une erreur est survenue", null));
        }
    }

    /**
     * Verify user documents (Admin only)
     * PATCH /api/users/{userId}/documents/verify
     */
    @PatchMapping("/{userId}/documents/verify")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Verify user documents", description = "Set verification status for user documents (Admin only)")
    public ResponseEntity<?> verifyUserDocuments(
            @PathVariable Long userId,
            @Valid @RequestBody com.gdldv.user.dto.DocumentVerificationRequest request) {
        try {
            logger.info("Admin verifying documents for user: ID={}, Status={}", userId, request.getStatus());

            userService.verifyUserDocuments(userId, request.getStatus(), request.getNotes());

            return ResponseEntity.ok(new ApiResponse<>(true, "Vérification mise à jour avec succès", null));

        } catch (IllegalArgumentException e) {
            logger.error("Invalid status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Error verifying documents", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Une erreur est survenue", null));
        }
    }
}