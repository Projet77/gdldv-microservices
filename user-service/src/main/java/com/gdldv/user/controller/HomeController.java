package com.gdldv.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Contrôleur pour la page d'accueil publique (Landing Page)
 *
 * @author GDLDV Team
 * @version 1.0
 */
@Controller
public class HomeController {

    /**
     * Affiche la landing page (page d'accueil)
     *
     * Routes:
     * - GET / : Page d'accueil principale
     * - GET /home : Alias pour la page d'accueil
     *
     * @return Le nom de la vue Thymeleaf (home.html)
     */
    @GetMapping({"/", "/home"})
    public String home() {
        return "home";
    }

    /**
     * Redirection depuis /index vers la page d'accueil
     *
     * @return Redirection vers la page d'accueil
     */
    @GetMapping("/index")
    public String index() {
        return "redirect:/";
    }
}
