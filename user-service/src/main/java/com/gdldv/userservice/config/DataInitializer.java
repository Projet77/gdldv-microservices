package com.gdldv.userservice.config;

import com.gdldv.userservice.model.Role;
import com.gdldv.userservice.model.User;
import com.gdldv.userservice.repository.RoleRepository;
import com.gdldv.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        promoteAdminUser();
    }

    private void promoteAdminUser() {
        String email = "admin@example.com";
        
        // 1. Ensure ROLE_SUPER_ADMIN exists
        Role superAdminRole = roleRepository.findByName(Role.RoleName.ROLE_SUPER_ADMIN)
                .orElseGet(() -> roleRepository.save(Role.builder().name(Role.RoleName.ROLE_SUPER_ADMIN).build()));

        // 2. Find or Create User
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            boolean hasRole = user.getRoles().stream()
                    .anyMatch(r -> r.getName() == Role.RoleName.ROLE_SUPER_ADMIN);
            
            if (!hasRole) {
                user.getRoles().add(superAdminRole);
                userRepository.save(user);
                System.out.println("✅ UPDATE: User " + email + " promoted to SUPER_ADMIN.");
            } else {
                 System.out.println("ℹ️ INFO: User " + email + " is already SUPER_ADMIN.");
            }
        } else {
            // Create default super admin if doesn't exist
            User newUser = User.builder()
                    .firstName("Super")
                    .lastName("Admin")
                    .email(email)
                    .password(passwordEncoder.encode("password")) // Default password
                    .roles(new HashSet<>(Collections.singletonList(superAdminRole)))
                    .active(true)
                    .emailVerified(true)
                    .build();
            userRepository.save(newUser);
            System.out.println("✅ CREATE: Created new SUPER_ADMIN user: " + email + " (password: password)");
        }
    }
}
