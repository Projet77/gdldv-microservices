package com.gdldv.user.service;

import com.gdldv.user.dto.ReservationResponse;
import com.gdldv.user.dto.UpdateProfileRequest;
import com.gdldv.user.dto.UserProfileResponse;
import com.gdldv.user.entity.Reservation;
import com.gdldv.user.entity.ReservationStatus;
import com.gdldv.user.entity.User;
import com.gdldv.user.repository.ReservationRepository;
import com.gdldv.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Autowired
    private com.gdldv.user.repository.RoleRepository roleRepository;

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

        if (updateRequest.getProfileImage() != null) {
            user.setProfileImage(updateRequest.getProfileImage());
        }

        // Sauvegarder les modifications
        User updatedUser = userRepository.save(user);
        logger.info("Profil mis à jour avec succès pour l'utilisateur: ID={}", updatedUser.getId());

        return new UserProfileResponse(updatedUser);
    }

    /**
     * GDLDV-463: Consulter historique des locations
     * Note: Cette méthode retourne un message indiquant que l'historique
     * doit être récupéré via le rental-service
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getRentalHistory() {
        User user = getCurrentUser();
        logger.info("Consultation de l'historique des locations pour l'utilisateur: ID={}, Email={}", user.getId(),
                user.getEmail());

        // L'historique des locations est géré par le rental-service
        // Le client doit appeler directement : GET
        // /rental-service/api/rentals/user/{userId}

        logger.info("Redirection vers rental-service pour l'historique");

        return Map.of(
                "message",
                "Pour consulter l'historique des locations, appelez GET /rental-service/api/rentals/user/"
                        + user.getId(),
                "userId", user.getId(),
                "rentalServiceEndpoint", "/rental-service/api/rentals/user/" + user.getId());
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
     * Récupérer tous les utilisateurs sous forme d'entités User (pour interface
     * web)
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsersEntities() {
        logger.info("Récupération de tous les utilisateurs (entités)");
        return userRepository.findAll();
    }

    /**
     * Vérifier si un email existe
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Trouver un utilisateur par email
     */
    public java.util.Optional<User> findUserByEmail(String email) {
        logger.debug("Finding user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    /**
     * Trouver un utilisateur par ID
     */
    public java.util.Optional<User> findUserById(Long id) {
        logger.debug("Finding user by ID: {}", id);
        return userRepository.findById(id);
    }

    /**
     * Créer un utilisateur (utilisé pour inscription web)
     */
    @Transactional
    public User createUser(User user) {
        logger.info("Création d'un nouvel utilisateur: {}", user.getEmail());

        // Encoder le mot de passe avant de sauvegarder
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // Ajouter le rôle CLIENT par défaut si aucun rôle n'est défini
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            com.gdldv.user.entity.Role clientRole = roleRepository
                    .findByName(com.gdldv.user.entity.Role.ERole.ROLE_CLIENT)
                    .orElseThrow(
                            () -> new RuntimeException("Erreur: Le rôle CLIENT n'existe pas dans la base de données"));
            user.getRoles().add(clientRole);
        }

        // S'assurer que l'utilisateur est actif par défaut
        if (!user.isActive()) {
            user.setActive(true);
        }

        User savedUser = userRepository.save(user);
        logger.info("Utilisateur créé avec succès: ID={}, Email={}, Rôles={}",
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRoles().stream().map(r -> r.getName().name()).toList());

        return savedUser;
    }

    /**
     * Mettre à jour un utilisateur (méthode simple)
     */
    @Transactional
    public User updateUser(User user) {
        logger.info("Updating user: {}", user.getId());
        return userRepository.save(user);
    }

    /**
     * Authentifier un utilisateur pour l'interface web
     * (sans génération de JWT, utilise les sessions HTTP)
     */
    @Transactional(readOnly = true)
    public java.util.Optional<User> authenticateUser(String email, String password) {
        logger.info("Tentative d'authentification pour: {}", email);
        java.util.Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Pour l'instant, retourner l'utilisateur sans vérification de mot de passe
            // (la vérification sera ajoutée avec PasswordEncoder)
            logger.info("Utilisateur trouvé: {}", email);
            return java.util.Optional.of(user);
        }

        logger.warn("Utilisateur non trouvé: {}", email);
        return java.util.Optional.empty();
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

    /**
     * GDLDV-463: Récupérer les réservations d'un utilisateur avec pagination
     */
    @Transactional(readOnly = true)
    public Page<ReservationResponse> getUserReservations(Long userId, Pageable pageable) {
        logger.info("Récupération des réservations pour l'utilisateur: ID={}", userId);
        return reservationRepository.findByUserId(userId, pageable)
                .map(this::mapReservationToResponse);
    }

    /**
     * GDLDV-463: Récupérer les réservations d'un utilisateur par status avec
     * pagination
     */
    @Transactional(readOnly = true)
    public Page<ReservationResponse> getUserReservationsByStatus(
            Long userId, String status, Pageable pageable) {
        logger.info("Récupération des réservations pour l'utilisateur: ID={} avec status: {}", userId, status);

        ReservationStatus reservationStatus;
        try {
            reservationStatus = ReservationStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.error("Status de réservation invalide: {}", status);
            throw new RuntimeException("Status de réservation invalide: " + status);
        }

        return reservationRepository.findByUserIdAndStatus(userId, reservationStatus, pageable)
                .map(this::mapReservationToResponse);
    }

    /**
     * Mapper une Reservation vers ReservationResponse
     */
    private ReservationResponse mapReservationToResponse(Reservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .confirmationNumber(reservation.getConfirmationNumber())
                .vehicleId(reservation.getVehicleId())
                .vehicleBrand(reservation.getVehicleBrand())
                .vehicleModel(reservation.getVehicleModel())
                .startDate(reservation.getStartDate())
                .endDate(reservation.getEndDate())
                .totalPrice(reservation.getTotalPrice())
                .status(reservation.getStatus().toString())
                .createdAt(reservation.getCreatedAt())
                .build();
    }

    // ========== NOUVELLES METHODES POUR CORRIGER LES ERREURS DE COMPILATION
    // ==========

    @Transactional
    public UserProfileResponse createUserByAdmin(com.gdldv.user.dto.CreateUserRequest request) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            // Logique d'attribution des rôles
            for (String roleName : request.getRoles()) {
                com.gdldv.user.entity.Role role = roleRepository
                        .findByName(com.gdldv.user.entity.Role.ERole.valueOf(roleName))
                        .orElseThrow(() -> new RuntimeException("Role non trouvé: " + roleName));
                user.getRoles().add(role);
            }
        } else {
            // Default to USER role
            com.gdldv.user.entity.Role clientRole = roleRepository
                    .findByName(com.gdldv.user.entity.Role.ERole.ROLE_CLIENT)
                    .orElseThrow(() -> new RuntimeException("Role CLIENT non trouvé"));
            user.getRoles().add(clientRole);
        }

        User savedUser = userRepository.save(user);
        return new UserProfileResponse(savedUser);
    }

    @Transactional
    public UserProfileResponse updateUserByAdmin(Long id, com.gdldv.user.dto.UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + id));

        if (request.getFirstName() != null)
            user.setFirstName(request.getFirstName());
        if (request.getLastName() != null)
            user.setLastName(request.getLastName());
        if (request.getEmail() != null)
            user.setEmail(request.getEmail());
        if (request.getPhoneNumber() != null)
            user.setPhoneNumber(request.getPhoneNumber());
        if (request.getAddress() != null)
            user.setAddress(request.getAddress());
        if (request.getCity() != null)
            user.setCity(request.getCity());
        if (request.getPostalCode() != null)
            user.setPostalCode(request.getPostalCode());
        if (request.getCountry() != null)
            user.setCountry(request.getCountry());

        // Handle Active status update if provided
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }

        // Handle Roles update if provided
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            user.getRoles().clear(); // Supprimer les anciens rôles
            for (String roleName : request.getRoles()) {
                com.gdldv.user.entity.Role role = roleRepository
                        .findByName(com.gdldv.user.entity.Role.ERole.valueOf(roleName))
                        .orElseThrow(() -> new RuntimeException("Role non trouvé: " + roleName));
                user.getRoles().add(role);
            }
        }

        User updatedUser = userRepository.save(user);
        return new UserProfileResponse(updatedUser);
    }

    @Transactional
    public UserProfileResponse activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + id));
        user.setActive(true);
        User updatedUser = userRepository.save(user);
        return new UserProfileResponse(updatedUser);
    }

    @Transactional(readOnly = true)
    public List<UserProfileResponse> getUsersByRole(String roleName) {
        // Note: Cette implémentation est simplifiée et suppose que le repository
        // supporte findByRolesName
        // Si non, il faudrait adapter UserRepository
        return userRepository.findAll().stream()
                .filter(u -> u.getRoles().stream().anyMatch(r -> r.getName().name().equalsIgnoreCase(roleName)))
                .map(UserProfileResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserProfileResponse> getArchivedUsers() {
        return userRepository.findAll().stream()
                .filter(user -> !user.isActive())
                .map(UserProfileResponse::new)
                .toList();
    }

    // --- Document Management Placeholders ---

    public com.gdldv.user.dto.DocumentUploadResponse uploadDocument(
            org.springframework.web.multipart.MultipartFile file, String documentType) {
        // Placeholder implementation
        String fakeUrl = "http://localhost:9000/documents/" + documentType + "/" + file.getOriginalFilename();
        return new com.gdldv.user.dto.DocumentUploadResponse(documentType, fakeUrl, "Document uploadé (simulation)");
    }

    @Transactional(readOnly = true)
    public com.gdldv.user.dto.UserDocumentsResponse getUserDocuments() {
        User user = getCurrentUser();
        // Return dummy data or empty for now since fields might not exist in User
        // entity yet
        return com.gdldv.user.dto.UserDocumentsResponse.builder()
                .userId(user.getId())
                .documentVerificationStatus("PENDING")
                .build();
    }

    public void deleteDocument(String documentType) {
        // Placeholder
        logger.info("Document deleted: {}", documentType);
    }

    @Transactional
    public void verifyUserDocuments(Long userId, String status, String notes) {
        // Placeholder
        logger.info("Documents verified for user {}: status={}, notes={}", userId, status, notes);
    }
}