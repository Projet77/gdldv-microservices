package com.gdldv.userservice.service;

import com.gdldv.userservice.dto.AuthResponse;
import com.gdldv.userservice.dto.LoginRequest;
import com.gdldv.userservice.model.User;
import com.gdldv.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Verify password using BCrypt
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
             throw new RuntimeException("Mot de passe incorrect");
        }

        // Generate a fake JWT for now (since we don't have the full security stack setup in this microservice yet)
        String token = Base64.getEncoder().encodeToString((user.getEmail() + ":" + System.currentTimeMillis()).getBytes());

        return AuthResponse.builder()
                .token(token)
                .user(user)
                .build();
    }
}
