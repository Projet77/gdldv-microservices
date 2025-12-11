package com.gdldv.user.controller;

import com.gdldv.user.dto.*;
import com.gdldv.user.entity.User;
import com.gdldv.user.security.JwtUtils;
import com.gdldv.user.service.AuthService;
import com.gdldv.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * API REST Controller pour l'authentification et la gestion de profil
 * Sprint 1: GDLDV-443 (Register), GDLDV-452 (Login), GDLDV-458 (Profile), GDLDV-463 (History)
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Tag(name = "Authentication API", description = "Endpoints pour inscription, connexion et gestion de profil")
public class AuthApiController {

    private final AuthService authService;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    /**
     * GDLDV-443: Inscription d'un nouveau client avec JWT
     * POST /api/auth/register
     */
    @PostMapping("/register")
    @Operation(summary = "Inscription", description = "Créer un nouveau compte utilisateur et obtenir un JWT token")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("API registration attempt for email: {}", request.getEmail());

        try {
            AuthResponse response = authService.registerUser(request);
            log.info("User registered successfully via API: {}", request.getEmail());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * GDLDV-452: Connexion avec JWT
     * POST /api/auth/login
     */
    @PostMapping("/login")
    @Operation(summary = "Connexion", description = "Se connecter et obtenir un JWT token")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        log.info("API login attempt for email: {}", request.getEmail());

        try {
            AuthResponse response = authService.authenticateUser(request);
            log.info("User logged in successfully via API: {}", request.getEmail());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(false, "Email ou mot de passe incorrect", null));
        }
    }

    /**
     * GDLDV-458: Consulter son profil
     * GET /api/auth/profile
     */
    @GetMapping("/profile")
    @Operation(summary = "Consulter son profil", description = "Récupérer les informations du profil de l'utilisateur connecté")
    public ResponseEntity<?> getProfile(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Extraire le token du header Authorization
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Token JWT manquant ou invalide", null));
            }

            String token = authHeader.substring(7);

            // Valider le token
            if (!jwtUtils.validateJwtToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Token JWT invalide ou expiré", null));
            }

            // Récupérer l'email du token
            String email = jwtUtils.getUsernameFromJwtToken(token);

            // Récupérer l'utilisateur
            User user = userService.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            UserProfileResponse profile = new UserProfileResponse(user);

            log.info("Profile retrieved via API for user: {}", email);
            return ResponseEntity.ok(new ApiResponse<>(true, "Profil récupéré avec succès", profile));

        } catch (Exception e) {
            log.error("Error retrieving profile: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Erreur lors de la récupération du profil", null));
        }
    }

    /**
     * GDLDV-458: Modifier son profil
     * PUT /api/auth/profile
     */
    @PutMapping("/profile")
    @Operation(summary = "Modifier son profil", description = "Mettre à jour les informations du profil de l'utilisateur connecté")
    public ResponseEntity<?> updateProfile(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody UpdateProfileRequest updateRequest) {
        try {
            // Extraire et valider le token
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Token JWT manquant ou invalide", null));
            }

            String token = authHeader.substring(7);

            if (!jwtUtils.validateJwtToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Token JWT invalide ou expiré", null));
            }

            String email = jwtUtils.getUsernameFromJwtToken(token);

            // Mettre à jour le profil
            User user = userService.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Appliquer les modifications
            if (updateRequest.getFirstName() != null) {
                user.setFirstName(updateRequest.getFirstName());
            }
            if (updateRequest.getLastName() != null) {
                user.setLastName(updateRequest.getLastName());
            }
            if (updateRequest.getPhoneNumber() != null) {
                user.setPhoneNumber(updateRequest.getPhoneNumber());
            }
            if (updateRequest.getAddress() != null) {
                user.setAddress(updateRequest.getAddress());
            }
            if (updateRequest.getCity() != null) {
                user.setCity(updateRequest.getCity());
            }

            User updatedUser = userService.updateUser(user);
            UserProfileResponse profile = new UserProfileResponse(updatedUser);

            log.info("Profile updated via API for user: {}", email);
            return ResponseEntity.ok(new ApiResponse<>(true, "Profil mis à jour avec succès", profile));

        } catch (Exception e) {
            log.error("Error updating profile: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Erreur lors de la mise à jour du profil", null));
        }
    }

    /**
     * GDLDV-463: Consulter historique des locations
     * GET /api/auth/reservations
     */
    @GetMapping("/reservations")
    @Operation(summary = "Historique des locations", description = "Récupérer l'historique des locations de l'utilisateur connecté")
    public ResponseEntity<?> getUserReservations(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        try {
            // Extraire et valider le token
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Token JWT manquant ou invalide", null));
            }

            String token = authHeader.substring(7);

            if (!jwtUtils.validateJwtToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Token JWT invalide ou expiré", null));
            }

            String email = jwtUtils.getUsernameFromJwtToken(token);
            User user = userService.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Récupérer les réservations avec pagination
            Pageable pageable = PageRequest.of(page, size);
            Page<ReservationResponse> reservations;

            if (status != null && !status.isEmpty()) {
                reservations = userService.getUserReservationsByStatus(user.getId(), status, pageable);
            } else {
                reservations = userService.getUserReservations(user.getId(), pageable);
            }

            log.info("Reservations retrieved via API for user: {}", email);
            return ResponseEntity.ok(new ApiResponse<>(true, "Historique récupéré avec succès", reservations));

        } catch (Exception e) {
            log.error("Error retrieving reservations: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Erreur lors de la récupération de l'historique", null));
        }
    }
}
