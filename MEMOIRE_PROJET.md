# MÉMOIRE DE FIN D'ÉTUDES
## Conception et Réalisation d'une Plateforme de Gestion de Location de Véhicules Basée sur une Architecture Microservices

---

**Présenté par :** Abdou  
**Sujet :** Gestion des Locations de Voitures & Gestion de Flotte  
**Technologies :** Spring Boot, Microservices, React, Docker  

---

# REMERCIEMENTS

*(Section à compléter avec vos remerciements personnels aux professeurs, tuteurs de stage, famille, etc.)*

---

# RÉSUMÉ

Ce projet consiste en la conception et le développement d'une plateforme web robuste et évolutive pour la gestion de locations de véhicules. Dans un contexte où la digitalisation des services est primordiale, cette solution vise à simplifier le processus de réservation pour les clients tout en offrant des outils puissants de gestion de flotte pour les administrateurs.

L'application repose sur une architecture moderne en **Microservices** utilisant l'écosystème **Spring Cloud**, garantissant modularité, scalabilité et maintenabilité. Le frontend, développé avec **React** et **TypeScript**, offre une expérience utilisateur fluide et responsive. Les fonctionnalités incluent la gestion des utilisateurs (RBAC), la gestion du parc automobile en temps réel, le traitement des réservations, la facturation multidevises et un système de sécurité avancé.

**Mots-clés :** Microservices, Spring Boot, React, Gestion de Location, Architecture Distribuée, DevOps.

---

# TABLE DES MATIÈRES

1. **Introduction Générale**
2. **Chapitre 1 : Contexte et Analyse des Besoins**
    * 1.1 Présentation du projet
    * 1.2 Problématique
    * 1.3 Analyse des besoins fonctionnels
    * 1.4 Analyse des besoins non-fonctionnels
3. **Chapitre 2 : Conception et Architecture**
    * 2.1 Choix de l'architecture (Monolithique vs Microservices)
    * 2.2 Architecture Logique (Vue d'ensemble)
    * 2.3 Conception de la Base de Données (Modèles par service)
    * 2.4 Diagrammes UML (Cas d'utilisation, Séquence déploiement)
4. **Chapitre 3 : Réalisation Technique**
    * 3.1 Environnement de développement et Outils
    * 3.2 Implémentation du Backend (Services Spring Boot)
    * 3.3 Sécurité et Authentification (JWT, Gateway)
    * 3.4 Implémentation du Frontend (React, dashboards)
5. **Conclusion et Perspectives**

---

# INTRODUCTION GÉNÉRALE

Le secteur de la location de voitures est en pleine mutation, porté par une demande croissante de flexibilité et de rapidité. Les agences traditionnelles peinent souvent à gérer efficacement leur flotte, leurs réservations et leur relation client en raison d'outils obsolètes ou déconnectés.

L'objectif de ce projet est de proposer une solution intégrée, "Gestion des Locations" (GDLDV), capable de répondre aux exigences modernes de performance et d'expérience utilisateur. Plutôt que de s'appuyer sur une architecture monolithique classique, souvent difficile à maintenir à grande échelle, nous avons opté pour une approche distribuée basée sur les microservices.

Ce mémoire retrace les étapes de la réalisation de ce projet, de l'analyse des besoins à la mise en production, en détaillant les choix techniques stratégiques qui ont guidé le développement.

---

# CHAPITRE 1 : CONTEXTE ET ANALYSE DES BESOINS

## 1.1 Présentation du Projet
Le projet vise à dématérialiser l'ensemble du processus de location de véhicules, depuis la recherche d'un véhicule par le client jusqu'à la restitution et la facturation, en passant par la gestion administrative de la flotte.

## 1.2 Problématique
Comment concevoir une plateforme capable de gérer simultanément des milliers de requêtes, d'assurer la cohérence des données de réservation en temps réel, et de s'adapter facilement à l'ajout de nouveaux services (comme la gestion de la maintenance ou des programmes de fidélité) sans interrompre le service existant ?

## 1.3 Analyse des Besoins Fonctionnels

### Pour les Clients :
*   **Inscription et Connexion sécurisée** : Accès personnel.
*   **Recherche de véhicules** : Filtrage par marque, modèle, prix, disponibilité.
*   **Réservation** : Sélection des dates, calcul automatique du prix.
*   **Gestion des réservations** : Consultation de l'historique, annulation, prolongation.
*   **Paiement** : Simulation de paiement sécurisé.

### Pour les Administrateurs / Gestionnaires :
*   **Gestion de flotte** : Ajout, modification, suppression de véhicules.
*   **Suivi des locations** : Vue d'ensemble des véhicules sortis et disponibles.
*   **Gestion des utilisateurs** : Rôles et permissions (Admin, Agent, Client).
*   **Statistiques** : Tableaux de bord financiers et opérationnels.

## 1.4 Analyse des Besoins Non-Fonctionnels
*   **Sécurité** : Protection des données personnelles et authentification robuste (JWT).
*   **Disponibilité** : Le système doit être résilient aux pannes d'un service isolé.
*   **Scalabilité** : Capacité à monter en charge.
*   **Internationalisation (i18n)** : Support du Français, Anglais, Espagnol.
*   **Ergonomie** : Interface intuitive et "Premium".

---

# CHAPITRE 2 : CONCEPTION ET ARCHITECTURE

## 2.1 Choix de l'Architecture : Pourquoi les Microservices ?
Contrairement à une application monolithique où tout le code réside dans une seule entité, l'architecture microservices découpe l'application en petits services autonomes.
*   **Avantage 1** : Chaque service peut être développé, déployé et mis à l'échelle indépendamment.
*   **Avantage 2** : Utilisation de technologies différentes si nécessaire (bien que nous ayons uniformisé sur Java/Spring Boot).
*   **Choix** : Java Spring Boot a été retenu pour sa maturité et son écosystème cloud (Netflix Eureka, Spring Cloud Gateway).

## 2.2 Architecture Logique

Le système est composé des services suivants :

1.  **Discovery Service (Eureka Server)** : L'annuaire où tous les services s'enregistrent pour se découvrir mutuellement.
2.  **API Gateway** : Le point d'entrée unique pour le Frontend. Il route les requêtes (ex: `/api/users` -> User Service) et gère la sécurité transversale.
3.  **Config Server** : Centralise la configuration (fichiers `.properties`) de tous les microservices.
4.  **User Service** : Gère les comptes, rôles, et l'authentification.
5.  **Vehicle Service** : Gère le catalogue des voitures (CRUD, images, état).
6.  **Reservation Service** : Gère la logique métier des locations (dates, calculs, conflits).
7.  **Rental Service** : Gère le cycle de vie de la location physique (départ, retour, état des lieux).
8.  **Payment Service** : Gère les transactions financières.

## 2.3 Conception de la Base de Données
Chaque microservice possède sa propre base de données (Pattern *Database per Service*) pour assurer un couplage faible.

*   **BDD Users** : Tables `users`, `roles`.
*   **BDD Vehicles** : Tables `vehicles`, `categories`, `maintenance_logs`.
*   **BDD Reservations** : Tables `reservations`.

---

# CHAPITRE 3 : RÉALISATION TECHNIQUE

## 3.1 Environnement de Développement
*   **Langage Backend** : Java 17
*   **Framework** : Spring Boot 3.x
*   **Frontend** : React 18, TypeScript, Tailwind CSS, Vite
*   **Base de Données** : MySQL
*   **Outils** : Postman (Tests API), Git (Versionning), IntelliJ IDEA / VS Code.

## 3.2 Implémentation du Backend (Points clés)

### La Communication Inter-Services
Nous utilisons **OpenFeign** pour permettre aux services de communiquer entre eux de manière déclarative (ex: `ReservationService` interroge `VehicleService` pour vérifier la disponibilité).

### La Sécurité
Implémentation de **Spring Security** avec **JWT (JSON Web Tokens)**.
1.  Le client se logue via `/login`.
2.  `User Service` génère un Token signé.
3.  Pour chaque requête suivante, le Token est envoyé dans le header `Authorization`.
4.  L'API Gateway valide le token avant de router la requête.

## 3.3 Implémentation du Frontend

### Architecture des Composants React
L'interface est découpée en composants réutilisables :
*   `Layouts` : Structure commune (Sidebar, Navbar).
*   `Pages` : Vues principales (Login, Dashboard, Catalog).
*   `Components` : Éléments UI (RentalCard, StatCard, Modal).

### Gestion de l'État et API
*   Utilisation de **Axios** pour les requêtes HTTP avec intercepteurs (ajout automatique du Token).
*   Hooks personnalisés : `useAuth` (gestion session), `useCurrency` (gestion multidevises dynamique), `useTranslation` (i18n).

### Fonctionnalités Avancées Développées
*   **Internationalisation** : Changement de langue instantané (FR/EN/ES) sans rechargement.
*   **Prolongation de Location** : Interface modale permettant au client de modifier sa réservation en temps réel avec recalcul automatique du coût.
*   **Tableaux de Bords** : Vues différenciées selon le rôle (Super Admin vs Client).

---

# CONCLUSION

Ce projet de fin d'études a permis de mettre en œuvre une architecture complexe et moderne répondant aux standards actuels de l'industrie logicielle. La plateforme "Gestion des locations" est désormais fonctionnelle, sécurisée et modulaire.

Les défis techniques, notamment la configuration de la Gateway et la synchronisation entre microservices, ont été surmontés grâce à une approche rigoureuse et à l'utilisation des patterns de conception adaptés.

**Perspectives d'amélioration :**
*   Intégration réelle de Stripe pour les paiements.
*   Déploiement conteneurisé complet avec **Kubernetes**.
*   Ajout d'un module d'IA pour la prédiction dynamique des prix (Yield Management).

---

# BIBLIOGRAPHIE

1.  Walls, C. (2018). *Spring Boot in Action*. Manning Publications.
2.  Wolff, E. (2016). *Microservices: Flexible Software Architecture*. Addison-Wesley.
3.  Documentation officielle React (react.dev).
4.  Documentation Spring Cloud (spring.io/projects/spring-cloud).
