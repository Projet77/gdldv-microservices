package com.gdldv.user.config;

import com.gdldv.user.entity.Role;
import com.gdldv.user.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Initialise les données de base nécessaires au démarrage de l'application
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Initialisation des données de base...");
        initializeRoles();
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
}
