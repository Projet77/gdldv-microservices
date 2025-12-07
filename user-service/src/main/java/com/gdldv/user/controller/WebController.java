package com.gdldv.user.controller;

import com.gdldv.user.dto.UpdateProfileRequest;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;

import com.gdldv.user.dto.RegisterRequest;
import com.gdldv.user.service.UserService;
import com.gdldv.user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.gdldv.user.dto.UserProfileResponse;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class WebController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService; // Inject UserService

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        try {
            UserProfileResponse userProfile = userService.getUserProfile();
            model.addAttribute("userProfile", userProfile);
            return "profile";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erreur lors du chargement du profil: " + e.getMessage());
            return "profile"; // Ou rediriger vers une page d'erreur
        }
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

                @PostMapping("/register")
                public String registerUser(@ModelAttribute("registerRequest") RegisterRequest registerRequest, Model model) {
                    try {
                        authService.registerUser(registerRequest);
                        model.addAttribute("successMessage", "Inscription réussie ! Veuillez vous connecter.");
                        return "login";
                    } catch (RuntimeException e) {
                        model.addAttribute("errorMessage", e.getMessage());
                        return "register";
                    }
                }
    
                @GetMapping("/profile/edit")
                public String editProfile(Model model) {
                    try {
                        UserProfileResponse userProfile = userService.getUserProfile();
                        UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest();
                        updateProfileRequest.setFirstName(userProfile.getFirstName());
                        updateProfileRequest.setLastName(userProfile.getLastName());
                        updateProfileRequest.setPhoneNumber(userProfile.getPhoneNumber());
                        updateProfileRequest.setAddress(userProfile.getAddress());
                        updateProfileRequest.setCity(userProfile.getCity());
                        updateProfileRequest.setPostalCode(userProfile.getPostalCode());
                        updateProfileRequest.setCountry(userProfile.getCountry());
                        model.addAttribute("updateProfileRequest", updateProfileRequest);
                        return "profile-edit";
                    } catch (Exception e) {
                        model.addAttribute("errorMessage", "Erreur lors du chargement du profil pour modification: " + e.getMessage());
                        return "profile"; // Rediriger vers la page de profil avec erreur
                    }
                }
    
                            @PostMapping("/profile/edit")
                            public String updateProfile(@ModelAttribute("updateProfileRequest") UpdateProfileRequest updateProfileRequest, Model model) {
                                try {
                                    userService.updateUserProfile(updateProfileRequest);
                                    model.addAttribute("successMessage", "Profil mis à jour avec succès !");
                                    return "redirect:/profile"; // Rediriger vers la page de profil
                                } catch (Exception e) {
                                    model.addAttribute("errorMessage", "Erreur lors de la mise à jour du profil: " + e.getMessage());
                                    return "profile-edit"; // Rester sur la page d'édition avec erreur
                                }
                            }
                
                                        @GetMapping("/rental-history")
                                        public String rentalHistory(Model model) {
                                            try {
                                                Map<String, Object> history = userService.getRentalHistory();
                                                // Ici, nous devrions normalement appeler le rental-service via FeignClient
                                                // Pour l'instant, nous allons simuler une réponse ou afficher le message de redirection
                                                model.addAttribute("rentalHistory", history); // Ou un objet DTO de l'historique réel
                                                return "rental-history";
                                            } catch (Exception e) {
                                                model.addAttribute("errorMessage", "Erreur lors de la récupération de l'historique des locations: " + e.getMessage());
                                                return "rental-history"; // Ou rediriger vers une page d'erreur
                                            }
                                        }
                            
                                                    @GetMapping("/admin/users")
                                                    public String listUsers(Model model) {
                                                        try {
                                                            List<UserProfileResponse> users = userService.getAllUsers();
                                                            model.addAttribute("users", users);
                                                            return "user-list";
                                                        } catch (Exception e) {
                                                            model.addAttribute("errorMessage", "Erreur lors de la récupération des utilisateurs: " + e.getMessage());
                                                            return "user-list"; // Ou rediriger vers une page d'erreur
                                                        }
                                                    }
                                        
                                                                @GetMapping("/admin/users/{id}")
                                                                public String userDetails(@PathVariable Long id, Model model) {
                                                                    try {
                                                                        UserProfileResponse userProfile = userService.getUserById(id);
                                                                        model.addAttribute("userProfile", userProfile);
                                                                        return "user-details";
                                                                    } catch (Exception e) {
                                                                        model.addAttribute("errorMessage", "Erreur lors de la récupération des détails de l'utilisateur: " + e.getMessage());
                                                                        return "user-details"; // Ou rediriger vers une page d'erreur
                                                                    }
                                                                }
                                                    
                                                                @PostMapping("/admin/users/{id}/deactivate")
                                                                public String deactivateUser(@PathVariable Long id, Model model) {
                                                                    try {
                                                                        userService.deactivateUser(id);
                                                                        model.addAttribute("successMessage", "Utilisateur désactivé avec succès !");
                                                                        return "redirect:/admin/users";
                                                                    } catch (Exception e) {
                                                                        model.addAttribute("errorMessage", "Erreur lors de la désactivation de l'utilisateur: " + e.getMessage());
                                                                        return "redirect:/admin/users/" + id;
                                                                    }
                                                                }
                                                    
                                                                @PostMapping("/admin/users/{id}/activate")
                                                                public String activateUser(@PathVariable Long id, Model model) {
                                                                    try {
                                                                        // Supposons que vous ayez une méthode activateUser dans UserService
                                                                        // userService.activateUser(id);
                                                                        // Pour l'instant, nous allons juste simuler l'activation
                                                                        model.addAttribute("successMessage", "Utilisateur activé avec succès ! (Fonctionnalité à implémenter)");
                                                                        return "redirect:/admin/users";
                                                                    } catch (Exception e) {
                                                                        model.addAttribute("errorMessage", "Erreur lors de l'activation de l'utilisateur: " + e.getMessage());
                                                                        return "redirect:/admin/users/" + id;
                                                                    }
                                                                }
                                                            }
