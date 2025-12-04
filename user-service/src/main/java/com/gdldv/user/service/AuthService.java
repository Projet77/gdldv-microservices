package com.gdldv.user.service;

import com.gdldv.user.dto.AuthResponse;
import com.gdldv.user.dto.LoginRequest;
import com.gdldv.user.dto.RegisterRequest;
import com.gdldv.user.entity.User;
import com.gdldv.user.repository.UserRepository;
import com.gdldv.user.security.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * GDLDV-443: Inscription d'un nouveau client
     */
    @Transactional
    public AuthResponse registerUser(RegisterRequest registerRequest) {
        logger.info("Tentative d'inscription pour l'email: {}", registerRequest.getEmail());

        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Erreur: L'email est déjà utilisé!");
        }

        // Vérifier si le numéro de permis existe déjà
        if (userRepository.existsByDrivingLicenseNumber(registerRequest.getDrivingLicenseNumber())) {
            throw new RuntimeException("Erreur: Ce numéro de permis de conduire est déjà enregistré!");
        }

        // Créer le nouvel utilisateur
        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setDrivingLicenseNumber(registerRequest.getDrivingLicenseNumber());
        user.setDrivingLicenseCountry(registerRequest.getDrivingLicenseCountry());
        user.setDrivingLicenseExpiryDate(registerRequest.getDrivingLicenseExpiryDate());
        user.setAddress(registerRequest.getAddress());
        user.setCity(registerRequest.getCity());
        user.setPostalCode(registerRequest.getPostalCode());
        user.setCountry(registerRequest.getCountry());
        user.setRole(User.UserRole.CLIENT);
        user.setActive(true);
        user.setEmailVerified(false);

        // Sauvegarder l'utilisateur
        User savedUser = userRepository.save(user);
        logger.info("Utilisateur créé avec succès: ID={}, Email={}", savedUser.getId(), savedUser.getEmail());

        // Générer le token JWT
        String jwt = jwtUtils.generateTokenFromUsername(savedUser.getEmail());

        // Retourner la réponse avec le token
        return new AuthResponse(
                jwt,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getRole()
        );
    }

    /**
     * GDLDV-452: Connexion avec génération JWT
     */
    @Transactional
    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        logger.info("Tentative de connexion pour l'email: {}", loginRequest.getEmail());

        // Authentifier l'utilisateur
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Générer le token JWT
        String jwt = jwtUtils.generateJwtToken(authentication);

        // Récupérer l'utilisateur
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Mettre à jour la date de dernière connexion
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        logger.info("Connexion réussie pour l'utilisateur: ID={}, Email={}", user.getId(), user.getEmail());

        // Retourner la réponse avec le token
        return new AuthResponse(
                jwt,
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole()
        );
    }

    /**
     * Récupérer l'utilisateur actuellement connecté
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
}