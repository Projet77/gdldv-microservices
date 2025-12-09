package com.gdldv.user.controller;

import com.gdldv.user.entity.User;
import com.gdldv.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    /**
     * Page de connexion
     */
    @GetMapping("/login")
    public String showLoginPage(Model model, @RequestParam(required = false) String error) {
        log.info("Accessing login page");
        if (error != null) {
            model.addAttribute("error", "Email ou mot de passe incorrect");
        }
        return "user/login";
    }

    /**
     * Traiter la connexion
     */
    @PostMapping("/login")
    public String processLogin(@RequestParam String email,
                               @RequestParam String password,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        log.info("Login attempt for email: {}", email);

        Optional<User> userOptional = userService.authenticateUser(email, password);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            session.setAttribute("currentUser", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userRole", user.getRole());

            log.info("User logged in successfully: {} ({})", user.getEmail(), user.getRole());
            redirectAttributes.addFlashAttribute("success", "Connexion réussie!");
            return "redirect:/dashboard";
        } else {
            log.warn("Failed login attempt for email: {}", email);
            redirectAttributes.addFlashAttribute("error", "Email ou mot de passe incorrect");
            return "redirect:/login?error";
        }
    }

    /**
     * Page d'inscription
     */
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        log.info("Accessing register page");
        model.addAttribute("user", new User());
        return "user/register";
    }

    /**
     * Traiter l'inscription
     */
    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("user") User user,
                                      BindingResult result,
                                      @RequestParam String confirmPassword,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {
        log.info("Registration attempt for email: {}", user.getEmail());

        if (result.hasErrors()) {
            log.warn("Validation errors in registration form");
            return "user/register";
        }

        // Vérifier que les mots de passe correspondent
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Les mots de passe ne correspondent pas");
            return "user/register";
        }

        // Vérifier que l'email n'existe pas déjà
        if (userService.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "Cet email est déjà utilisé");
            return "user/register";
        }

        // Définir le rôle par défaut
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("CLIENT");
        }

        try {
            User createdUser = userService.createUser(user);
            log.info("User registered successfully: {}", createdUser.getEmail());
            redirectAttributes.addFlashAttribute("success", "Inscription réussie! Vous pouvez maintenant vous connecter.");
            return "redirect:/login";
        } catch (Exception e) {
            log.error("Error during registration: {}", e.getMessage());
            model.addAttribute("error", "Erreur lors de l'inscription: " + e.getMessage());
            return "user/register";
        }
    }

    /**
     * Page d'accueil (dashboard)
     */
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            log.warn("Unauthorized access to dashboard - no user in session");
            redirectAttributes.addFlashAttribute("error", "Veuillez vous connecter");
            return "redirect:/login";
        }

        log.info("User accessing dashboard: {}", currentUser.getEmail());
        model.addAttribute("user", currentUser);

        // Ajouter la liste des utilisateurs pour les admins
        if ("ADMIN".equals(currentUser.getRole())) {
            model.addAttribute("users", userService.getAllUsersEntities());
        }

        return "user/dashboard";
    }

    /**
     * Déconnexion
     */
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            log.info("User logging out: {}", currentUser.getEmail());
        }

        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "Déconnexion réussie");
        return "redirect:/login";
    }

    /**
     * Page d'accueil - redirection vers login
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
}
