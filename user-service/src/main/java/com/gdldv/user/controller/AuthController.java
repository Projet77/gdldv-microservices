package com.gdldv.user.controller;

import com.gdldv.user.dto.ApiResponse;
import com.gdldv.user.dto.AuthResponse;
import com.gdldv.user.dto.LoginRequest;
import com.gdldv.user.dto.RegisterRequest;
import com.gdldv.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentification", description = "API de gestion de l'authentification (ÉPIC-3)")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    /**
     * GDLDV-443: Inscription d'un nouveau client
     * POST /api/auth/register
     */
    @PostMapping("/register")
    @Operation(summary = "Inscription d'un nouveau client", description = "Créer un nouveau compte client (GDLDV-443)")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            logger.info("Demande d'inscription reçue pour l'email: {}", registerRequest.getEmail());

            AuthResponse authResponse = authService.registerUser(registerRequest);

            logger.info("Inscription réussie pour l'utilisateur: ID={}", authResponse.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Inscription réussie!", authResponse));

        } catch (RuntimeException e) {
            logger.error("Erreur lors de l'inscription: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de l'inscription", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Une erreur est survenue lors de l'inscription", null));
        }
    }

    /**
     * GDLDV-452: Connexion avec génération JWT
     * POST /api/auth/login
     */
    @PostMapping("/login")
    @Operation(summary = "Connexion avec génération JWT", description = "Authentifier un utilisateur et générer un token JWT (GDLDV-452)")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Tentative de connexion pour l'email: {}", loginRequest.getEmail());

            AuthResponse authResponse = authService.authenticateUser(loginRequest);

            logger.info("Connexion réussie pour l'utilisateur: ID={}", authResponse.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Connexion réussie!", authResponse));

        } catch (RuntimeException e) {
            logger.error("Erreur lors de la connexion: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Email ou mot de passe incorrect", null));
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de la connexion", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Une erreur est survenue lors de la connexion", null));
        }
    }

    /**
     * Endpoint de test pour vérifier que le service est en ligne
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Vérifier que le service d'authentification fonctionne")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Service d'authentification opérationnel", null));
    }
}