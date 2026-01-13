package com.gdldv.user.config;

import com.gdldv.user.entity.Role;
import com.gdldv.user.entity.User;
import com.gdldv.user.repository.RoleRepository;
import com.gdldv.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Initialise les données de base nécessaires au démarrage de l'application
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Initialisation des données de base...");
        initializeRoles();
        initializeAdminUser();
        logger.info("Initialisation terminée.");
    }

    /**
     * Crée les rôles de base s'ils n'existent pas
     */
    private void initializeRoles() {
        logger.info("Vérification des rôles...");

        for (Role.ERole roleName : Role.ERole.values()) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
                logger.info("Rôle créé: {}", roleName);
            } else {
                logger.debug("Rôle déjà existant: {}", roleName);
            }
        }

        logger.info("Rôles vérifiés: {} rôles disponibles", roleRepository.count());
    }

    /**
     * Crée un utilisateur admin par défaut s'il n'existe pas
     */
    private void initializeAdminUser() {
        logger.info("Vérification de l'utilisateur admin...");

        String adminEmail = "admin@gdldv.com";
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("GDLDV");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setPhoneNumber("+221000000000");
            admin.setActive(true);
            admin.setEmailVerified(true);

            // Assigner le rôle ADMIN
            Role adminRole = roleRepository.findByName(Role.ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Role ADMIN not found"));
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            admin.setRoles(roles);

            userRepository.save(admin);
            logger.info("✅ Utilisateur admin créé: {} / admin123", adminEmail);
        } else {
            logger.info("✅ Utilisateur admin déjà existant: {}", adminEmail);
        }
    }
}
