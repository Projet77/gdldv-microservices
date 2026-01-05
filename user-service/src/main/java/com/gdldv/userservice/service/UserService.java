package com.gdldv.userservice.service;

import com.gdldv.userservice.model.Role;
import com.gdldv.userservice.model.User;
import com.gdldv.userservice.repository.RoleRepository;
import com.gdldv.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email déjà utilisé : " + user.getEmail());
        }
        
        // As it's a new user, we assign default ROLE_CLIENT
        // Note: Production code should probably take role from DTO or have administrative endpoint for roles
        Role clientRole = roleRepository.findByName(Role.RoleName.ROLE_CLIENT)
                .orElseThrow(() -> new RuntimeException("Role ROLE_CLIENT not found in DB"));
        
        user.setRoles(new HashSet<>(Collections.singletonList(clientRole)));
        
        // TODO: Hash password before saving in production (Actually AuthService handles Login, but Registration logic would need encoding too)
        // Ideally UserService should inject PasswordEncoder and encode here.
        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id : " + id));

        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        if (userDetails.getRoles() != null && !userDetails.getRoles().isEmpty()) {
            user.setRoles(userDetails.getRoles());
        } 
        
        // Only update email if it's different and not taken
        if (!user.getEmail().equals(userDetails.getEmail())) {
             if (userRepository.existsByEmail(userDetails.getEmail())) {
                throw new RuntimeException("Email déjà utilisé");
             }
             user.setEmail(userDetails.getEmail());
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur non trouvé");
        }
        userRepository.deleteById(id);
    }
}
