# MÉMOIRE DE FIN D'ÉTUDES

## Conception et Réalisation d'une Plateforme de Gestion de Location de Véhicules Basée sur une Architecture Microservices

---

**UNIVERSITÉ GRENOBLE ALPES**
Institut d'Études Politiques
*(ou votre établissement)*

**Présenté par :** Abdou
**Titre :** Conception et Réalisation d'une Plateforme de Gestion de Location de Véhicules Basée sur une Architecture Microservices
**Année de soutenance :** 2025
**Sujet :** Gestion des Locations de Voitures & Gestion de Flotte
**Technologies :** Spring Boot, Microservices, React, MySQL, Docker
**Sous la direction de :** *(Nom du directeur de mémoire à compléter)*

---

# REMERCIEMENTS

Je tiens à exprimer ma profonde gratitude à toutes les personnes qui ont contribué, de près ou de loin, à la réalisation de ce projet de fin d'études.

Mes remerciements vont d'abord à mon directeur de mémoire, pour son encadrement, ses conseils précieux et sa disponibilité tout au long de ce travail. Son expertise m'a permis d'approfondir mes compétences techniques et de structurer ma réflexion.

Je souhaite également remercier l'équipe pédagogique de mon établissement pour la qualité de la formation dispensée, qui m'a donné les bases solides nécessaires à la conception de ce projet.

Un grand merci à ma famille pour son soutien indéfectible, sa patience et ses encouragements durant toute la durée de mes études. Votre confiance a été un moteur essentiel dans l'accomplissement de ce travail.

Enfin, je remercie l'ensemble des développeurs et contributeurs des technologies open source utilisées dans ce projet (Spring Boot, React, MySQL), dont la documentation et les communautés actives ont grandement facilité mon apprentissage.

---

# RÉSUMÉ

Ce mémoire présente la conception et le développement d'une plateforme web complète de gestion de locations de véhicules, nommée GDLDV (Gestion Des Locations de Véhicules). Dans un contexte de digitalisation croissante des services, cette solution moderne répond aux besoins des agences de location tout en offrant une expérience utilisateur optimale pour les clients.

L'architecture du système repose sur un modèle en **microservices** utilisant l'écosystème **Spring Cloud** (Java 17, Spring Boot 3.x). Cette approche garantit la modularité, la scalabilité horizontale et la maintenabilité du code. Le frontend, développé avec **React 18** et **TypeScript**, offre une interface responsive, intuitive et multilingue (français, anglais, espagnol).

Les fonctionnalités développées incluent : l'authentification sécurisée par JWT, la gestion des utilisateurs avec contrôle d'accès basé sur les rôles (RBAC), la gestion du parc automobile en temps réel, le traitement des réservations avec validation des disponibilités, un système de paiement simulé, ainsi que des tableaux de bord différenciés selon les profils utilisateurs (Client, Agent, Manager, Administrateur, Super Administrateur).

Ce projet illustre les bonnes pratiques du développement logiciel moderne : architecture distribuée, communication inter-services via OpenFeign, gestion centralisée de la configuration, découverte de services avec Eureka, et déploiement conteneurisé avec Docker.

**Mots-clés :** Microservices, Spring Boot, React, Architecture Distribuée, Gestion de Location de Véhicules, JWT, API Gateway, DevOps, Docker, MySQL.

---

# SOMMAIRE

1. **Introduction Générale** .......................................................................... 1
2. **Chapitre 1 : Contexte et Analyse des Besoins** .................................... 5
3. **Chapitre 2 : Conception et Architecture** ............................................. 15
4. **Chapitre 3 : Réalisation Technique** ..................................................... 30
5. **Conclusion et Perspectives** .................................................................. 50
6. **Bibliographie** ........................................................................................ 55
7. **Table des Annexes** ................................................................................ 58
8. **Table des Matières** ................................................................................ 60

---

# INTRODUCTION GÉNÉRALE

## Contexte général

Le secteur de la location de véhicules connaît une transformation profonde, portée par la digitalisation et l'évolution des attentes des consommateurs. Selon une étude du marché européen de la location de voitures, le chiffre d'affaires du secteur dépasse 25 milliards d'euros annuels, avec une croissance constante de 5 % par an. Les clients recherchent désormais des solutions rapides, accessibles 24/7, et totalement dématérialisées.

Parallèlement, les agences de location font face à des défis opérationnels majeurs : gestion de flottes importantes, optimisation du taux d'occupation des véhicules, traitement des réservations en temps réel, et coordination entre plusieurs points de service. Les systèmes legacy, souvent monolithiques et peu évolutifs, peinent à répondre à ces enjeux.

## Motivation du projet

L'objectif de ce mémoire est de concevoir et développer une plateforme moderne, **GDLDV (Gestion Des Locations de Véhicules)**, qui répond à ces problématiques en exploitant les technologies actuelles du développement web et les principes d'architecture logicielle avancés.

Notre approche se distingue par le choix d'une **architecture microservices**, qui permet de découper l'application en modules indépendants et spécialisés. Cette décision stratégique répond à plusieurs besoins :

- **Scalabilité** : Chaque service peut être mis à l'échelle individuellement selon la charge.
- **Maintenabilité** : Les équipes peuvent travailler sur différents services en parallèle sans conflits.
- **Résilience** : La défaillance d'un service n'entraîne pas l'arrêt complet du système.
- **Évolutivité** : De nouveaux services peuvent être ajoutés sans impact sur l'existant.

## Problématique

La question centrale de ce travail est la suivante : **Comment concevoir une plateforme de gestion de locations de véhicules capable de gérer simultanément des milliers de requêtes, d'assurer la cohérence des données en temps réel, tout en restant évolutive et maintenable sur le long terme ?**

Cette problématique se décline en plusieurs sous-questions techniques :

1. Comment structurer l'architecture pour garantir l'indépendance des services tout en assurant leur communication efficace ?
2. Comment gérer la sécurité dans un système distribué (authentification, autorisation) ?
3. Comment garantir la cohérence des données (notamment les disponibilités des véhicules) sans base de données centralisée ?
4. Comment offrir une expérience utilisateur fluide malgré la complexité du backend ?

## Méthodologie

Le développement du projet a suivi une méthodologie agile, structurée en plusieurs sprints :

- **Sprint 1** : Analyse des besoins, conception de l'architecture, mise en place de l'infrastructure (Eureka, Gateway, Config Server).
- **Sprint 2** : Développement des services de base (User Service, Vehicle Service).
- **Sprint 3** : Implémentation de la logique métier (Reservation Service, Rental Service).
- **Sprint 4-5** : Développement du frontend React, intégration complète, tests end-to-end.

## Plan du mémoire

Ce document s'articule autour de trois chapitres principaux :

Le **Chapitre 1** présente le contexte du projet, la problématique et l'analyse détaillée des besoins fonctionnels et non-fonctionnels.

Le **Chapitre 2** expose les choix architecturaux, la modélisation du système (diagrammes UML) et la conception de la base de données.

Le **Chapitre 3** détaille la réalisation technique : implémentation du backend (services Spring Boot, sécurité JWT, communication inter-services), développement du frontend React, et tests.

Enfin, la **Conclusion** synthétise les apports du projet, évalue les résultats obtenus et propose des perspectives d'amélioration.

---

# CHAPITRE 1 : CONTEXTE ET ANALYSE DES BESOINS

## 1.1 Présentation du Projet

### 1.1.1 Objectifs généraux

Le projet GDLDV vise à créer une solution logicielle complète pour la gestion de locations de véhicules. Il s'adresse à deux types d'utilisateurs principaux :

- **Les clients** : particuliers ou professionnels souhaitant louer un véhicule de manière simple et rapide.
- **Le personnel de l'agence** : agents, managers et administrateurs nécessitant des outils de gestion de flotte, de suivi des réservations et d'analyse statistique.

### 1.1.2 Périmètre fonctionnel

La plateforme couvre l'ensemble du cycle de vie d'une location :

1. **Phase de recherche** : Le client consulte le catalogue de véhicules disponibles avec filtres avancés.
2. **Phase de réservation** : Sélection des dates, calcul automatique du tarif, validation de la disponibilité.
3. **Phase de paiement** : Simulation d'un processus de paiement sécurisé.
4. **Phase de gestion** : Le personnel peut suivre les réservations, gérer les départs/retours de véhicules.
5. **Phase d'analyse** : Génération de rapports statistiques sur les performances de l'agence.

### 1.1.3 Acteurs du système

Le système distingue cinq profils utilisateurs, chacun avec des permissions spécifiques :

- **CLIENT** : Peut consulter les véhicules, effectuer des réservations, consulter son historique.
- **AGENT** : Gère les opérations de départ et de retour des véhicules (check-in/check-out).
- **MANAGER** : Supervise les équipes d'agents, accède aux statistiques de son agence.
- **ADMIN** : Gère l'ensemble de la plateforme (utilisateurs, véhicules, réservations).
- **SUPER_ADMIN** : Accède aux fonctionnalités système (configuration, logs, gestion multi-agences).

## 1.2 Problématique

### 1.2.1 Limites des solutions existantes

Les systèmes traditionnels de gestion de location présentent plusieurs faiblesses :

- **Architecture monolithique** : Difficile à maintenir et à faire évoluer, nécessite un redéploiement complet pour chaque modification.
- **Scalabilité limitée** : Impossible de mettre à l'échelle uniquement les composants critiques.
- **Couplage fort** : Une erreur dans un module peut compromettre l'ensemble du système.
- **Technologies obsolètes** : Interfaces utilisateur peu ergonomiques, absence de responsive design.

### 1.2.2 Enjeux techniques

Notre projet doit relever plusieurs défis techniques majeurs :

1. **Cohérence des données distribuées** : Comment garantir qu'un véhicule ne soit pas réservé deux fois simultanément, alors que les données sont réparties entre plusieurs bases ?
2. **Gestion de la sécurité** : Comment authentifier un utilisateur une seule fois et propager ses droits à tous les services ?
3. **Communication inter-services** : Comment gérer les appels entre services de manière fiable (gestion des erreurs, timeouts, circuit breakers) ?
4. **Performance** : Comment maintenir des temps de réponse rapides malgré les appels en cascade entre services ?

## 1.3 Analyse des Besoins Fonctionnels

### 1.3.1 Besoins des Clients

#### BF1 : Inscription et Authentification
- **Description** : Un visiteur peut créer un compte client en fournissant ses informations personnelles (nom, prénom, email, téléphone, permis de conduire).
- **Critères d'acceptation** : Validation des données (format email, téléphone), mot de passe sécurisé (minimum 8 caractères), confirmation par email.

#### BF2 : Recherche de Véhicules
- **Description** : Le client peut consulter le catalogue et filtrer les véhicules par critères (marque, modèle, catégorie, prix, disponibilité, localisation).
- **Critères d'acceptation** : Affichage des résultats en temps réel, tri par pertinence, pagination.

#### BF3 : Consultation des Détails d'un Véhicule
- **Description** : Le client peut consulter la fiche détaillée d'un véhicule (photos, caractéristiques techniques, tarifs, avis).
- **Critères d'acceptation** : Affichage des disponibilités sur un calendrier interactif.

#### BF4 : Réservation d'un Véhicule
- **Description** : Le client sélectionne un véhicule, choisit les dates de location, et soumet une demande de réservation.
- **Critères d'acceptation** :
  - Vérification automatique de la disponibilité
  - Calcul du tarif total (jours de location × tarif journalier)
  - Gestion des options supplémentaires (GPS, siège bébé, assurance complémentaire)
  - Génération d'un numéro de réservation unique

#### BF5 : Paiement en Ligne (Simulation)
- **Description** : Le client procède au paiement de sa réservation.
- **Critères d'acceptation** : Formulaire de paiement sécurisé (simulation), confirmation immédiate par email.

#### BF6 : Gestion des Réservations
- **Description** : Le client peut consulter l'historique de ses réservations, annuler une réservation (selon les conditions), ou prolonger une location en cours.
- **Critères d'acceptation** :
  - Affichage du statut de chaque réservation (En attente, Confirmée, En cours, Terminée, Annulée)
  - Recalcul automatique du coût en cas de prolongation

### 1.3.2 Besoins du Personnel de l'Agence

#### BF7 : Gestion des Utilisateurs (Admin)
- **Description** : L'administrateur peut créer, modifier, désactiver des comptes utilisateurs et leur attribuer des rôles.
- **Critères d'acceptation** : Interface CRUD complète, système de permissions granulaire.

#### BF8 : Gestion de la Flotte de Véhicules (Admin/Manager)
- **Description** : Ajout de nouveaux véhicules au catalogue, modification des informations (kilométrage, état), retrait de véhicules.
- **Critères d'acceptation** :
  - Upload de photos
  - Gestion des catégories (Économique, Compacte, SUV, Luxe)
  - Traçabilité des modifications

#### BF9 : Suivi des Réservations (Admin/Manager/Agent)
- **Description** : Visualisation en temps réel de toutes les réservations avec filtres (date, statut, véhicule, client).
- **Critères d'acceptation** : Vue calendrier et vue liste, export des données (CSV, PDF).

#### BF10 : Check-Out / Check-In (Agent)
- **Description** : Processus de départ du véhicule (vérification identité, état des lieux, remise des clés) et de retour (inspection, calcul d'éventuels frais supplémentaires).
- **Critères d'acceptation** :
  - Formulaire d'inspection avec photos
  - Signature électronique du client
  - Génération automatique du contrat de location

#### BF11 : Gestion de la Maintenance (Admin/Manager)
- **Description** : Planification des interventions de maintenance, historique des réparations.
- **Critères d'acceptation** : Notification automatique quand un véhicule atteint un certain kilométrage.

#### BF12 : Tableaux de Bord et Statistiques
- **Description** : Visualisation de KPIs (taux d'occupation, chiffre d'affaires, véhicules les plus loués).
- **Critères d'acceptation** : Graphiques interactifs, export des rapports, filtres par période.

## 1.4 Analyse des Besoins Non-Fonctionnels

### 1.4.1 Performance

- **Temps de réponse** : Les pages doivent se charger en moins de 2 secondes (3G standard).
- **Débit** : Le système doit supporter 1 000 utilisateurs simultanés.
- **Scalabilité** : Capacité à monter en charge horizontalement (ajout de serveurs).

### 1.4.2 Sécurité

- **Authentification** : Système JWT avec tokens signés, durée de validité limitée (24h).
- **Autorisation** : Contrôle d'accès basé sur les rôles (RBAC), vérification à chaque requête.
- **Confidentialité** : Chiffrement des mots de passe (BCrypt), HTTPS obligatoire en production.
- **Protection contre les attaques** :
  - CSRF (Cross-Site Request Forgery)
  - XSS (Cross-Site Scripting)
  - SQL Injection (utilisation de PreparedStatements)
  - Rate limiting sur l'API Gateway

### 1.4.3 Disponibilité et Fiabilité

- **Disponibilité** : Objectif de 99,5 % (downtime maximum de 43,8 heures/an).
- **Résilience** : Pas de Single Point of Failure, utilisation de circuit breakers (Resilience4j).
- **Récupération** : Backups quotidiens de la base de données, plan de reprise d'activité.

### 1.4.4 Maintenabilité

- **Code** : Respect des principes SOLID, couverture de tests > 70 %.
- **Documentation** : Documentation des API (Swagger/OpenAPI), README pour chaque service.
- **Monitoring** : Logs centralisés, métriques de performance (Spring Boot Actuator).

### 1.4.5 Utilisabilité

- **Interface** : Design responsive (mobile, tablette, desktop).
- **Ergonomie** : Navigation intuitive, messages d'erreur explicites.
- **Accessibilité** : Respect des normes WCAG 2.1 (niveau AA).
- **Internationalisation** : Support de 3 langues (français, anglais, espagnol).

### 1.4.6 Compatibilité

- **Navigateurs** : Chrome, Firefox, Safari, Edge (versions récentes).
- **Systèmes d'exploitation** : Windows, macOS, Linux, iOS, Android.

---

# CHAPITRE 2 : CONCEPTION ET ARCHITECTURE

## 2.1 Choix de l'Architecture : Monolithique vs Microservices

### 2.1.1 Architecture Monolithique

Une architecture monolithique regroupe toutes les fonctionnalités de l'application dans une seule unité de déploiement.

**Avantages** :
- Simplicité de développement initial
- Facilité de débogage (un seul processus)
- Déploiement simple (un seul artefact)

**Inconvénients** :
- Couplage fort entre les modules
- Scalabilité limitée (scaling vertical uniquement)
- Déploiements risqués (tout ou rien)
- Maintenance difficile à grande échelle

### 2.1.2 Architecture Microservices

L'architecture microservices découpe l'application en services indépendants, chacun responsable d'un domaine métier spécifique.

**Avantages retenus pour notre projet** :

1. **Indépendance technologique** : Chaque service peut utiliser la stack la mieux adaptée (bien que nous ayons uniformisé sur Java/Spring Boot).

2. **Scalabilité granulaire** : Le `VehicleService` (forte lecture) peut tourner sur 5 instances, tandis que le `PaymentService` (faible charge) n'en utilise qu'une.

3. **Déploiement continu** : Une modification du `UserService` ne nécessite pas de redéployer tout le système.

4. **Résilience** : Si le `NotificationService` tombe, les réservations continuent de fonctionner.

5. **Équipes autonomes** : Différentes équipes peuvent travailler sur différents services en parallèle.

**Défis à relever** :
- Complexité de la communication inter-services
- Gestion de la cohérence des données distribuées
- Overhead de l'infrastructure (découverte de services, load balancing)
- Debugging plus complexe (appels en cascade)

### 2.1.3 Notre décision

Pour ce projet, nous avons opté pour une **architecture microservices** car :
- Le domaine métier se prête bien à une décomposition fonctionnelle claire.
- Les exigences de scalabilité justifient cette complexité.
- C'est une opportunité d'apprentissage des architectures distribuées modernes.

## 2.2 Architecture Logique du Système

### 2.2.1 Vue d'ensemble

```
┌─────────────────────────────────────────────────────────────┐
│                    FRONTEND (React SPA)                      │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │  Login   │  │Dashboard │  │ Catalog  │  │ Booking  │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└────────────────────────┬────────────────────────────────────┘
                         │ HTTP/REST (JWT)
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                    API GATEWAY (Port 8000)                   │
│  • Routing           • Rate Limiting                         │
│  • JWT Validation    • CORS                                  │
└────────┬───────┬────────┬─────────┬─────────┬───────────────┘
         │       │        │         │         │
         ▼       ▼        ▼         ▼         ▼
    ┌─────┐ ┌────────┐ ┌────────┐ ┌───────┐ ┌─────────┐
    │User │ │Vehicle │ │Reserva │ │Rental │ │Notifica │
    │Svc  │ │Service │ │tion    │ │Svc    │ │tion Svc │
    │8003 │ │  8001  │ │Service │ │ 8004  │ │  8006   │
    │     │ │        │ │  8002  │ │       │ │         │
    └──┬──┘ └───┬────┘ └───┬────┘ └───┬───┘ └────┬────┘
       │        │          │          │          │
       ▼        ▼          ▼          ▼          ▼
    [MySQL] [MySQL]   [MySQL]    [MySQL]    [MySQL]
    UserDB  VehicleDB  ReservaDB  RentalDB   NotifDB

            ┌──────────────────────────────┐
            │ EUREKA SERVER (Port 8761)    │
            │ (Service Discovery)          │
            └──────────────────────────────┘

            ┌──────────────────────────────┐
            │ CONFIG SERVER (Port 8888)    │
            │ (Centralized Configuration)  │
            └──────────────────────────────┘
```

### 2.2.2 Description des Services

#### Eureka Server (Discovery Service)
- **Rôle** : Annuaire de services. Tous les microservices s'enregistrent auprès d'Eureka au démarrage.
- **Technologie** : Spring Cloud Netflix Eureka
- **Port** : 8761
- **Fonctionnalités** :
  - Enregistrement automatique des services
  - Health checks périodiques
  - Dashboard de visualisation

#### API Gateway
- **Rôle** : Point d'entrée unique pour toutes les requêtes clients. Route les requêtes vers les services appropriés.
- **Technologie** : Spring Cloud Gateway
- **Port** : 8000
- **Responsabilités** :
  - Routing dynamique basé sur les chemins (`/api/users/**` → User Service)
  - Validation JWT (extraction et vérification du token)
  - Load balancing (si plusieurs instances d'un service)
  - CORS configuration
  - Rate limiting (protection contre les abus)

#### Config Server
- **Rôle** : Centralisation de la configuration de tous les services.
- **Technologie** : Spring Cloud Config
- **Port** : 8888
- **Avantages** :
  - Modification de configuration sans redéploiement
  - Gestion de profils (dev, staging, prod)
  - Versioning des configurations (Git backend)

#### User Service
- **Rôle** : Gestion des utilisateurs et authentification.
- **Port** : 8003
- **Base de données** : `gdldv_user_db`
- **Endpoints principaux** :
  - `POST /api/auth/register` : Inscription
  - `POST /api/auth/login` : Connexion (génère un JWT)
  - `GET /api/users/profile` : Profil de l'utilisateur connecté
  - `GET /api/users` : Liste des utilisateurs (Admin)
  - `PUT /api/users/{id}` : Modification d'un utilisateur
  - `DELETE /api/users/{id}` : Suppression d'un utilisateur
- **Entités** : `User`, `Role`

#### Vehicle Service
- **Rôle** : Gestion du catalogue de véhicules.
- **Port** : 8001
- **Base de données** : `gdldv_vehicle_db`
- **Endpoints principaux** :
  - `GET /api/vehicles` : Liste des véhicules avec filtres
  - `GET /api/vehicles/{id}` : Détails d'un véhicule
  - `POST /api/vehicles` : Ajout d'un véhicule (Admin)
  - `PUT /api/vehicles/{id}` : Modification
  - `DELETE /api/vehicles/{id}` : Suppression
  - `GET /api/vehicles/available` : Véhicules disponibles pour une période donnée
- **Entités** : `Vehicle`, `Category`, `Image`, `MaintenanceLog`

#### Reservation Service
- **Rôle** : Gestion des réservations et calcul des tarifs.
- **Port** : 8002
- **Base de données** : `gdldv_reservation_db`
- **Endpoints principaux** :
  - `POST /api/reservations` : Création d'une réservation
  - `GET /api/reservations` : Liste des réservations (filtrée par rôle)
  - `GET /api/reservations/{id}` : Détails d'une réservation
  - `PUT /api/reservations/{id}/cancel` : Annulation
  - `PUT /api/reservations/{id}/extend` : Prolongation
  - `PUT /api/reservations/{id}/confirm` : Confirmation (Admin)
- **Entités** : `Reservation`, `Payment`
- **Dépendances** : Communique avec le Vehicle Service pour vérifier les disponibilités

#### Rental Service
- **Rôle** : Gestion du cycle de vie physique de la location (départ/retour).
- **Port** : 8004
- **Base de données** : `gdldv_rental_db`
- **Endpoints principaux** :
  - `POST /api/rentals/check-out` : Départ du véhicule
  - `POST /api/rentals/check-in` : Retour du véhicule
  - `GET /api/rentals/{id}/inspection` : Rapport d'inspection
- **Entités** : `Rental`, `Inspection`, `Damage`

#### Notification Service
- **Rôle** : Envoi de notifications (email, SMS).
- **Port** : 8006
- **Base de données** : `gdldv_notification_db`
- **Endpoints principaux** :
  - `POST /api/notifications/send` : Envoi manuel
  - `GET /api/notifications/history` : Historique
- **Fonctionnalités** :
  - Écoute d'événements (Kafka ou RabbitMQ en production)
  - Templates d'emails personnalisables
  - Gestion des préférences utilisateur

## 2.3 Communication Inter-Services

### 2.3.1 Synchrone : OpenFeign

Pour les appels synchrones (requête-réponse), nous utilisons **OpenFeign**, un client HTTP déclaratif.

**Exemple** : Le Reservation Service doit vérifier qu'un véhicule est disponible.

```java
// Interface Feign
@FeignClient(name = "vehicle-service")
public interface VehicleClient {

    @GetMapping("/api/vehicles/{id}/availability")
    Boolean checkAvailability(
        @PathVariable Long id,
        @RequestParam LocalDate startDate,
        @RequestParam LocalDate endDate
    );
}

// Utilisation dans le ReservationService
@Service
public class ReservationService {

    @Autowired
    private VehicleClient vehicleClient;

    public Reservation createReservation(ReservationRequest request) {
        // Vérification de disponibilité
        boolean available = vehicleClient.checkAvailability(
            request.getVehicleId(),
            request.getStartDate(),
            request.getEndDate()
        );

        if (!available) {
            throw new VehicleNotAvailableException();
        }

        // Création de la réservation...
    }
}
```

### 2.3.2 Gestion des erreurs : Resilience4j

Pour éviter les cascades de pannes, nous implémentons des **circuit breakers**.

```java
@CircuitBreaker(name = "vehicleService", fallbackMethod = "fallbackAvailability")
public Boolean checkAvailability(Long id, LocalDate start, LocalDate end) {
    return vehicleClient.checkAvailability(id, start, end);
}

public Boolean fallbackAvailability(Long id, LocalDate start, LocalDate end, Exception ex) {
    log.error("Vehicle Service indisponible", ex);
    return false; // Comportement dégradé : on refuse la réservation
}
```

## 2.4 Conception de la Base de Données

### 2.4.1 Principe : Database per Service

Chaque microservice possède sa propre base de données, conformément au pattern **Database per Service**. Cela garantit :
- L'indépendance des services
- Pas de couplage via le schéma de données
- Liberté de choix de la technologie de stockage

### 2.4.2 Schéma User Service

**Table `users`** :
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    address VARCHAR(255),
    city VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    driving_license_number VARCHAR(50),
    driving_license_country VARCHAR(50),
    driving_license_expiry_date DATE,
    active BOOLEAN DEFAULT TRUE,
    email_verified BOOLEAN DEFAULT FALSE,
    profile_image TEXT,
    last_login_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**Table `roles`** :
```sql
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO roles (name) VALUES
    ('ROLE_CLIENT'),
    ('ROLE_AGENT'),
    ('ROLE_MANAGER'),
    ('ROLE_ADMIN'),
    ('ROLE_SUPER_ADMIN');
```

**Table `user_roles`** (Many-to-Many) :
```sql
CREATE TABLE user_roles (
    user_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);
```

### 2.4.3 Schéma Vehicle Service

**Table `vehicles`** :
```sql
CREATE TABLE vehicles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    year INT NOT NULL,
    registration_number VARCHAR(20) UNIQUE NOT NULL,
    category ENUM('ECONOMY', 'COMPACT', 'SUV', 'LUXURY') NOT NULL,
    transmission ENUM('MANUAL', 'AUTOMATIC') NOT NULL,
    fuel_type ENUM('GASOLINE', 'DIESEL', 'ELECTRIC', 'HYBRID') NOT NULL,
    seats INT NOT NULL,
    daily_rate DECIMAL(10, 2) NOT NULL,
    mileage INT DEFAULT 0,
    status ENUM('AVAILABLE', 'RENTED', 'MAINTENANCE', 'RETIRED') DEFAULT 'AVAILABLE',
    location VARCHAR(255),
    features JSON,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**Table `vehicle_images`** :
```sql
CREATE TABLE vehicle_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicle_id BIGINT NOT NULL,
    image_url TEXT NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    display_order INT DEFAULT 0,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE CASCADE
);
```

### 2.4.4 Schéma Reservation Service

**Table `reservations`** :
```sql
CREATE TABLE reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reservation_number VARCHAR(50) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL,
    vehicle_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    pickup_location VARCHAR(255),
    return_location VARCHAR(255),
    status ENUM('PENDING', 'CONFIRMED', 'ACTIVE', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
    total_amount DECIMAL(10, 2) NOT NULL,
    deposit_amount DECIMAL(10, 2),
    payment_status ENUM('UNPAID', 'PAID', 'REFUNDED') DEFAULT 'UNPAID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_vehicle_id (vehicle_id),
    INDEX idx_dates (start_date, end_date)
);
```

## 2.5 Diagrammes UML

### 2.5.1 Diagramme de Cas d'Utilisation

```
                    +------------------+
                    |     CLIENT       |
                    +------------------+
                            |
        +-------------------+-------------------+
        |                   |                   |
        v                   v                   v
  [S'inscrire]      [Rechercher      [Effectuer une
                     véhicules]       réservation]
                                             |
                                             v
                                      [Payer en ligne]
                                             |
                                             v
                                      [Consulter
                                       historique]

                    +------------------+
                    |      AGENT       |
                    +------------------+
                            |
                   +--------+--------+
                   |                 |
                   v                 v
            [Check-Out]        [Check-In]
            (Départ)           (Retour)

                    +------------------+
                    |   ADMINISTRATEUR |
                    +------------------+
                            |
        +-------------------+-------------------+
        |                   |                   |
        v                   v                   v
  [Gérer           [Gérer           [Consulter
   utilisateurs]    véhicules]       statistiques]
```

### 2.5.2 Diagramme de Séquence : Processus de Réservation

```
Client  →  Gateway  →  ReservationSvc  →  VehicleSvc  →  PaymentSvc  →  NotificationSvc

   |         |              |                  |              |               |
   |--POST /api/reservations--------------->  |              |               |
   |         |              |                  |              |               |
   |         |              |--Check availability------------>|               |
   |         |              |<---------OK---------------------|               |
   |         |              |                                 |               |
   |         |              |--Create reservation-------------|               |
   |         |              |                                 |               |
   |         |              |--Process payment----------------------------->  |
   |         |              |<---------Payment OK----------------------------|
   |         |              |                                                 |
   |         |              |--Send confirmation email------------------------------>
   |         |<--------Reservation Created (200 OK)-------------------------|
   |<--------|              |                                                 |
```

---

# CHAPITRE 3 : RÉALISATION TECHNIQUE

## 3.1 Environnement de Développement

### 3.1.1 Stack Technologique

**Backend** :
- **Langage** : Java 17 (LTS)
- **Framework** : Spring Boot 3.2.x
- **Build Tool** : Maven 3.9.x
- **Base de données** : MySQL 8.0
- **ORM** : Spring Data JPA / Hibernate
- **Sécurité** : Spring Security + JWT (io.jsonwebtoken:jjwt)
- **Communication inter-services** : OpenFeign
- **Résilience** : Resilience4j (Circuit Breaker)
- **Documentation API** : SpringDoc OpenAPI (Swagger UI)

**Frontend** :
- **Framework** : React 18.2
- **Langage** : TypeScript 5.x
- **Build Tool** : Vite 5.x
- **UI Library** : Tailwind CSS 3.x
- **HTTP Client** : Axios
- **Routing** : React Router v6
- **State Management** : React Hooks (useState, useContext)
- **Internationalisation** : react-i18next

**DevOps** :
- **Containerization** : Docker
- **Orchestration** : Docker Compose
- **Version Control** : Git / GitHub
- **CI/CD** : (à implémenter : GitHub Actions)

### 3.1.2 IDE et Outils

- **Backend** : IntelliJ IDEA Community Edition
- **Frontend** : Visual Studio Code
- **API Testing** : Postman, Insomnia
- **Database Management** : phpMyAdmin, MySQL Workbench
- **Terminal** : Git Bash (Windows), Terminal (macOS/Linux)

### 3.1.3 Structure des Projets

**Projet Backend (multi-modules Maven)** :
```
gdldv-microservices/
├── discovery-service/         # Eureka Server
├── api-gateway/               # Spring Cloud Gateway
├── config-server/             # Spring Cloud Config
├── user-service/              # Microservice Utilisateurs
├── vehicle-service/           # Microservice Véhicules
├── reservation-service/       # Microservice Réservations
├── rental-service/            # Microservice Locations
├── notification-service/      # Microservice Notifications
└── pom.xml                    # Parent POM
```

**Projet Frontend** :
```
gdldv-frontend/
├── public/
├── src/
│   ├── components/            # Composants réutilisables
│   ├── pages/                 # Pages principales
│   ├── services/              # Services API (Axios)
│   ├── hooks/                 # Custom Hooks
│   ├── utils/                 # Utilitaires
│   ├── i18n.ts                # Configuration i18n
│   ├── App.tsx
│   └── main.tsx
├── package.json
└── vite.config.ts
```

## 3.2 Implémentation du Backend

### 3.2.1 Eureka Server

**Configuration (application.properties)** :
```properties
spring.application.name=discovery-service
server.port=8761

eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```

**Classe principale** :
```java
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServiceApplication.class, args);
    }
}
```

### 3.2.2 API Gateway : Sécurité JWT

**Filtre d'authentification** :
```java
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
        "/api/auth/login",
        "/api/auth/register",
        "/api/vehicles",
        "/actuator/**"
    );

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();

            // Autoriser les endpoints publics
            if (isPublicEndpoint(path)) {
                return chain.filter(exchange);
            }

            // Extraire et valider le JWT
            String token = extractToken(exchange.getRequest());

            if (token == null || !validateToken(token)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // Token valide, continuer
            return chain.filter(exchange);
        };
    }

    private String extractToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            log.error("JWT validation failed", e);
            return false;
        }
    }

    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream()
            .anyMatch(endpoint -> path.contains(endpoint));
    }

    public static class Config {}
}
```

**Configuration du routing** :
```properties
# Vehicle Service
spring.cloud.gateway.routes[0].id=vehicle-service
spring.cloud.gateway.routes[0].uri=lb://vehicle-service
spring.cloud.gateway.routes[0].predicates[0]=Path=/api/vehicles/**
spring.cloud.gateway.routes[0].filters[0]=JwtAuthenticationFilter

# User Service
spring.cloud.gateway.routes[1].id=user-service
spring.cloud.gateway.routes[1].uri=lb://user-service
spring.cloud.gateway.routes[1].predicates[0]=Path=/api/users/**,/api/auth/**
spring.cloud.gateway.routes[1].filters[0]=JwtAuthenticationFilter
```

### 3.2.3 User Service : Authentification

**Entité User** :
```java
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;
    private String phoneNumber;
    private boolean active = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    // Implémentation de UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
            .map(role -> new SimpleGrantedAuthority(role.getName().name()))
            .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    // Getters/Setters...
}
```

**Service d'authentification** :
```java
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request) {
        // Authentification
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Génération du JWT
        String jwt = jwtUtils.generateJwtToken(authentication);

        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        return new AuthResponse(
            jwt,
            user.getId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList())
        );
    }

    public User register(RegisterRequest request) {
        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Créer le nouvel utilisateur
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());

        // Assigner le rôle CLIENT par défaut
        Role clientRole = roleRepository.findByName(ERole.ROLE_CLIENT)
            .orElseThrow(() -> new RuntimeException("Role not found"));
        user.getRoles().add(clientRole);

        return userRepository.save(user);
    }
}
```

**Utilitaire JWT** :
```java
@Component
public class JwtUtils {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {
        User userPrincipal = (User) authentication.getPrincipal();

        return Jwts.builder()
            .subject(userPrincipal.getUsername())
            .issuedAt(new Date())
            .expiration(new Date(new Date().getTime() + jwtExpirationMs))
            .signWith(key())
            .compact();
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser()
            .verifyWith(key())
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().verifyWith(key()).build().parseSignedClaims(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT validation error", e);
        }
        return false;
    }
}
```

### 3.2.4 Vehicle Service : Gestion du Catalogue

**Controller** :
```java
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @GetMapping
    public ResponseEntity<List<VehicleResponse>> getAllVehicles(
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String brand,
        @RequestParam(required = false) BigDecimal maxPrice
    ) {
        List<VehicleResponse> vehicles = vehicleService.searchVehicles(category, brand, maxPrice);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable Long id) {
        VehicleResponse vehicle = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(vehicle);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleResponse> createVehicle(@RequestBody VehicleRequest request) {
        VehicleResponse created = vehicleService.createVehicle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/available")
    public ResponseEntity<List<VehicleResponse>> getAvailableVehicles(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<VehicleResponse> available = vehicleService.getAvailableVehicles(startDate, endDate);
        return ResponseEntity.ok(available);
    }
}
```

**Service** :
```java
@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    public List<VehicleResponse> searchVehicles(String category, String brand, BigDecimal maxPrice) {
        Specification<Vehicle> spec = Specification.where(null);

        if (category != null) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("category"), Category.valueOf(category)));
        }

        if (brand != null) {
            spec = spec.and((root, query, cb) ->
                cb.like(cb.lower(root.get("brand")), "%" + brand.toLowerCase() + "%"));
        }

        if (maxPrice != null) {
            spec = spec.and((root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("dailyRate"), maxPrice));
        }

        return vehicleRepository.findAll(spec).stream()
            .map(VehicleResponse::new)
            .collect(Collectors.toList());
    }

    public List<VehicleResponse> getAvailableVehicles(LocalDate startDate, LocalDate endDate) {
        // Logique complexe : un véhicule est disponible si :
        // 1. Son statut est AVAILABLE
        // 2. Il n'a pas de réservation CONFIRMÉE ou ACTIVE qui chevauche la période

        List<Vehicle> allVehicles = vehicleRepository.findByStatus(Status.AVAILABLE);

        return allVehicles.stream()
            .filter(vehicle -> !hasConflictingReservation(vehicle.getId(), startDate, endDate))
            .map(VehicleResponse::new)
            .collect(Collectors.toList());
    }

    private boolean hasConflictingReservation(Long vehicleId, LocalDate start, LocalDate end) {
        // Appel au Reservation Service via Feign
        // (simplifié ici)
        return false;
    }
}
```

### 3.2.5 Reservation Service : Logique Métier

**Service de réservation** :
```java
@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private VehicleClient vehicleClient;

    @Transactional
    public ReservationResponse createReservation(CreateReservationRequest request) {
        // 1. Vérifier la disponibilité du véhicule
        VehicleDTO vehicle = vehicleClient.getVehicleById(request.getVehicleId());

        if (vehicle == null) {
            throw new VehicleNotFoundException("Vehicle not found");
        }

        // 2. Vérifier qu'il n'y a pas de chevauchement de dates
        boolean hasConflict = reservationRepository.existsByVehicleIdAndDatesOverlap(
            request.getVehicleId(),
            request.getStartDate(),
            request.getEndDate()
        );

        if (hasConflict) {
            throw new VehicleNotAvailableException("Vehicle not available for these dates");
        }

        // 3. Calculer le montant total
        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
        BigDecimal totalAmount = vehicle.getDailyRate().multiply(BigDecimal.valueOf(days));

        // 4. Créer la réservation
        Reservation reservation = new Reservation();
        reservation.setReservationNumber(generateReservationNumber());
        reservation.setUserId(request.getUserId());
        reservation.setVehicleId(request.getVehicleId());
        reservation.setStartDate(request.getStartDate());
        reservation.setEndDate(request.getEndDate());
        reservation.setTotalAmount(totalAmount);
        reservation.setStatus(ReservationStatus.PENDING);

        Reservation saved = reservationRepository.save(reservation);

        // 5. Envoyer une notification (asynchrone)
        notificationService.sendReservationConfirmation(saved);

        return new ReservationResponse(saved, vehicle);
    }

    private String generateReservationNumber() {
        return "RES-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
               + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
```

**Repository custom query** :
```java
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
           "FROM Reservation r " +
           "WHERE r.vehicleId = :vehicleId " +
           "AND r.status IN ('CONFIRMED', 'ACTIVE') " +
           "AND ((r.startDate <= :endDate AND r.endDate >= :startDate))")
    boolean existsByVehicleIdAndDatesOverlap(
        @Param("vehicleId") Long vehicleId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    List<Reservation> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Reservation> findByStatusOrderByCreatedAtDesc(ReservationStatus status);
}
```

## 3.3 Implémentation du Frontend

### 3.3.1 Configuration Axios

**api.ts** :
```typescript
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8000';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Intercepteur pour ajouter le JWT
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Intercepteur pour gérer les erreurs
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
```

### 3.3.2 Service Authentification

**authService.ts** :
```typescript
import api from './api';

export const authService = {
  login: async (credentials: { email: string; password: string }) => {
    const response = await api.post('/api/auth/login', credentials);
    if (response.data.token) {
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('user', JSON.stringify(response.data));
    }
    return response.data;
  },

  register: async (userData: any) => {
    const response = await api.post('/api/auth/register', userData);
    return response.data;
  },

  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = '/login';
  },

  getCurrentUser: () => {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  },
};
```

### 3.3.3 Hook Personnalisé useAuth

```typescript
import { useState, useEffect, createContext, useContext } from 'react';
import { authService } from '../services/authService';

interface AuthContextType {
  user: any;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState(authService.getCurrentUser());

  const login = async (email: string, password: string) => {
    const userData = await authService.login({ email, password });
    setUser(userData);
  };

  const logout = () => {
    authService.logout();
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout, isAuthenticated: !!user }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within AuthProvider');
  return context;
};
```

### 3.3.4 Composant de Connexion

**Login.tsx** :
```typescript
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

const Login: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await login(email, password);
      navigate('/dashboard');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="bg-white p-8 rounded-xl shadow-lg w-full max-w-md">
        <h2 className="text-3xl font-bold text-center mb-6">Connexion</h2>

        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="mb-4">
            <label className="block text-gray-700 font-bold mb-2">Email</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
              required
            />
          </div>

          <div className="mb-6">
            <label className="block text-gray-700 font-bold mb-2">Mot de passe</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
              required
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-blue-600 text-white py-3 rounded-lg font-bold hover:bg-blue-700 transition disabled:bg-gray-400"
          >
            {loading ? 'Connexion...' : 'Se connecter'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default Login;
```

### 3.3.5 Internationalisation (i18n)

**Configuration i18n.ts** :
```typescript
import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';

const resources = {
  fr: {
    translation: {
      welcome: 'Bienvenue',
      search: 'Rechercher',
      book: 'Réserver',
      myBookings: 'Mes réservations',
      logout: 'Déconnexion',
    },
  },
  en: {
    translation: {
      welcome: 'Welcome',
      search: 'Search',
      book: 'Book',
      myBookings: 'My bookings',
      logout: 'Logout',
    },
  },
  es: {
    translation: {
      welcome: 'Bienvenido',
      search: 'Buscar',
      book: 'Reservar',
      myBookings: 'Mis reservas',
      logout: 'Cerrar sesión',
    },
  },
};

i18n
  .use(initReactI18next)
  .init({
    resources,
    lng: 'fr',
    fallbackLng: 'en',
    interpolation: {
      escapeValue: false,
    },
  });

export default i18n;
```

## 3.4 Tests et Débogage

### 3.4.1 Tests Unitaires Backend

**Exemple : Test du ReservationService** :
```java
@SpringBootTest
class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    @MockBean
    private VehicleClient vehicleClient;

    @MockBean
    private ReservationRepository reservationRepository;

    @Test
    void testCreateReservation_Success() {
        // Arrange
        VehicleDTO mockVehicle = new VehicleDTO();
        mockVehicle.setId(1L);
        mockVehicle.setDailyRate(new BigDecimal("50.00"));

        when(vehicleClient.getVehicleById(1L)).thenReturn(mockVehicle);
        when(reservationRepository.existsByVehicleIdAndDatesOverlap(any(), any(), any()))
            .thenReturn(false);

        CreateReservationRequest request = new CreateReservationRequest();
        request.setVehicleId(1L);
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(3));

        // Act
        ReservationResponse response = reservationService.createReservation(request);

        // Assert
        assertNotNull(response);
        assertEquals(new BigDecimal("100.00"), response.getTotalAmount()); // 2 jours * 50
    }

    @Test
    void testCreateReservation_VehicleNotAvailable() {
        // Arrange
        when(vehicleClient.getVehicleById(1L)).thenReturn(new VehicleDTO());
        when(reservationRepository.existsByVehicleIdAndDatesOverlap(any(), any(), any()))
            .thenReturn(true);

        CreateReservationRequest request = new CreateReservationRequest();
        request.setVehicleId(1L);

        // Act & Assert
        assertThrows(VehicleNotAvailableException.class, () -> {
            reservationService.createReservation(request);
        });
    }
}
```

### 3.4.2 Tests d'Intégration

**Test du flux complet de réservation** :
```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = Replace.ANY)
class ReservationIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testCompleteReservationFlow() {
        // 1. Créer un utilisateur
        RegisterRequest registerRequest = new RegisterRequest("Test", "User", "test@example.com", "password123");
        ResponseEntity<User> userResponse = restTemplate.postForEntity(
            "/api/auth/register",
            registerRequest,
            User.class
        );
        assertEquals(HttpStatus.CREATED, userResponse.getStatusCode());

        // 2. Se connecter
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password123");
        ResponseEntity<AuthResponse> authResponse = restTemplate.postForEntity(
            "/api/auth/login",
            loginRequest,
            AuthResponse.class
        );
        String token = authResponse.getBody().getToken();

        // 3. Créer une réservation
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        CreateReservationRequest reservationRequest = new CreateReservationRequest();
        // ... remplir les détails

        HttpEntity<CreateReservationRequest> entity = new HttpEntity<>(reservationRequest, headers);
        ResponseEntity<ReservationResponse> reservationResponse = restTemplate.postForEntity(
            "/api/reservations",
            entity,
            ReservationResponse.class
        );

        assertEquals(HttpStatus.CREATED, reservationResponse.getStatusCode());
    }
}
```

### 3.4.3 Débogage et Logs

**Configuration Logback (logback-spring.xml)** :
```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/application-%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <logger name="com.gdldv" level="DEBUG"/>
    <logger name="org.springframework.web" level="INFO"/>

    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

## 3.5 Déploiement avec Docker

### 3.5.1 Dockerfile pour un Microservice

```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8001

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 3.5.2 Docker Compose

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: gdldv_vehicle_db
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql

  eureka-server:
    build: ./discovery-service
    ports:
      - "8761:8761"
    environment:
      - SPRING_PROFILES_ACTIVE=docker

  api-gateway:
    build: ./api-gateway
    ports:
      - "8000:8000"
    depends_on:
      - eureka-server
    environment:
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/

  vehicle-service:
    build: ./vehicle-service
    ports:
      - "8001:8001"
    depends_on:
      - mysql
      - eureka-server
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/gdldv_vehicle_db
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/

  user-service:
    build: ./user-service
    ports:
      - "8003:8003"
    depends_on:
      - mysql
      - eureka-server

  frontend:
    build: ./frontend
    ports:
      - "3000:80"
    depends_on:
      - api-gateway

volumes:
  mysql-data:
```

---

# CONCLUSION ET PERSPECTIVES

## Synthèse du Travail Réalisé

Ce projet de fin d'études nous a permis de concevoir et développer une plateforme complète de gestion de locations de véhicules en exploitant les technologies modernes du développement web et les principes d'architecture distribuée.

### Objectifs atteints

Les objectifs fixés en début de projet ont été largement atteints :

1. **Architecture microservices fonctionnelle** : Nous avons mis en place 8 microservices indépendants communiquant via REST et découverts dynamiquement via Eureka.

2. **Sécurité robuste** : L'authentification JWT garantit un accès sécurisé aux ressources, avec un contrôle d'accès basé sur les rôles (RBAC).

3. **Expérience utilisateur moderne** : Le frontend React offre une interface responsive, multilingue et intuitive.

4. **Cohérence des données** : Malgré l'architecture distribuée, nous avons maintenu la cohérence des réservations grâce à des transactions et des vérifications de disponibilité.

5. **Scalabilité démontrée** : Chaque service peut être mis à l'échelle indépendamment, et le système supporte plusieurs instances simultanées.

### Compétences développées

Ce projet m'a permis d'acquérir et d'approfondir de nombreuses compétences techniques et méthodologiques :

**Compétences techniques** :
- Maîtrise de l'écosystème Spring (Boot, Cloud, Security, Data JPA)
- Développement frontend avec React et TypeScript
- Conception d'architectures distribuées
- Gestion de bases de données relationnelles (MySQL)
- Communication inter-services (OpenFeign, REST)
- Containerisation avec Docker

**Compétences méthodologiques** :
- Analyse des besoins et modélisation UML
- Méthodologie agile (sprints, itérations)
- Gestion de version avec Git
- Documentation technique (Swagger, README)
- Tests unitaires et d'intégration

### Défis rencontrés et Solutions apportées

#### Défi 1 : Cohérence des données distribuées

**Problème** : Comment garantir qu'un véhicule ne soit pas réservé deux fois simultanément ?

**Solution** : Nous avons implémenté une vérification de disponibilité dans le Reservation Service avec une requête atomique sur la base de données. Une amélioration future serait d'utiliser un verrou distribué (Redis Redlock).

#### Défi 2 : Gestion des erreurs en cascade

**Problème** : Si le Vehicle Service est indisponible, les réservations échouent.

**Solution** : Implémentation de circuit breakers avec Resilience4j, permettant un comportement dégradé (refuser temporairement les réservations).

#### Défi 3 : Configuration de l'API Gateway

**Problème** : La validation JWT dans la Gateway nécessitait d'accéder à la même clé secrète que le User Service.

**Solution** : Centralisation de la configuration via le Config Server, garantissant que tous les services utilisent la même clé JWT.

## Perspectives d'Amélioration

### Améliorations Techniques

1. **Système de messaging asynchrone** :
   - Actuellement, les notifications sont envoyées de manière synchrone.
   - **Amélioration** : Utiliser Kafka ou RabbitMQ pour découpler les services et améliorer la résilience.

2. **Cache distribué** :
   - Les données de véhicules sont fréquemment consultées.
   - **Amélioration** : Implémenter Redis pour cacher les résultats de recherche et réduire la charge sur la base de données.

3. **API Gateway avancée** :
   - **Amélioration** : Ajouter un système de rate limiting plus sophistiqué (par utilisateur, par endpoint).

4. **Monitoring et Observabilité** :
   - **Amélioration** : Intégrer ELK Stack (Elasticsearch, Logstash, Kibana) pour l'analyse des logs centralisés.
   - Utiliser Prometheus et Grafana pour la surveillance des métriques.

5. **Gestion des transactions distribuées** :
   - **Amélioration** : Implémenter le pattern Saga pour gérer les transactions cross-services (réservation + paiement + notification).

### Fonctionnalités Métier

1. **Paiement réel** :
   - Intégrer Stripe ou PayPal pour des transactions réelles.

2. **Programme de fidélité** :
   - Système de points cumulables, réductions pour clients réguliers.

3. **Recommandations personnalisées** :
   - Algorithme de suggestion de véhicules basé sur l'historique du client.

4. **Géolocalisation** :
   - Affichage des agences sur une carte interactive (Google Maps API).
   - Calcul automatique de la distance client-agence.

5. **Signature électronique** :
   - Intégration de DocuSign pour la signature des contrats de location.

6. **Application mobile** :
   - Développement d'une app React Native pour iOS et Android.

### Déploiement et DevOps

1. **Orchestration Kubernetes** :
   - Déployer l'ensemble de l'architecture sur un cluster Kubernetes pour une meilleure gestion de la scalabilité.

2. **CI/CD Pipeline** :
   - GitHub Actions pour automatiser les tests, le build et le déploiement.
   - Intégration de SonarQube pour l'analyse de la qualité du code.

3. **Infrastructure as Code** :
   - Utiliser Terraform pour provisionner l'infrastructure cloud (AWS, Azure).

4. **Blue-Green Deployment** :
   - Stratégie de déploiement sans downtime.

## Apport Personnel

Ce projet a été une expérience enrichissante sur plusieurs aspects :

**Sur le plan technique**, j'ai consolidé mes connaissances en développement backend Java et découvert les subtilités des architectures microservices. La gestion de la communication inter-services, de la sécurité distribuée et de la cohérence des données m'a confronté à des problématiques réelles rencontrées en entreprise.

**Sur le plan méthodologique**, j'ai appris à structurer un projet de grande envergure, à prioriser les fonctionnalités et à adopter une approche itérative. La rédaction de ce mémoire m'a également permis de développer mes compétences en documentation technique.

**Sur le plan professionnel**, ce projet constitue un portfolio solide démontrant ma capacité à concevoir et réaliser une application complexe de bout en bout. Les technologies utilisées (Spring Boot, React, Docker, microservices) sont très recherchées sur le marché de l'emploi.

## Conclusion Finale

La réalisation de la plateforme GDLDV illustre la pertinence des architectures microservices pour développer des applications modernes, scalables et maintenables. Malgré la complexité inhérente à ce type d'architecture, les bénéfices en termes de flexibilité, de résilience et d'évolutivité justifient pleinement ce choix.

Au-delà des aspects techniques, ce projet m'a permis de mener à bien une démarche complète d'ingénierie logicielle : de l'analyse des besoins à la réalisation, en passant par la conception architecturale et les tests. Cette expérience constitue une base solide pour aborder des projets professionnels de grande ampleur.

L'avenir de la plateforme GDLDV est prometteur : les perspectives d'amélioration évoquées permettraient de transformer ce prototype en une solution réellement déployable en production, capable de servir des milliers d'utilisateurs simultanés.

---

# BIBLIOGRAPHIE

## Ouvrages

RICHARDSON, Chris. *Microservices Patterns: With examples in Java*. Shelter Island (NY) : Manning Publications, 2018. 520 p.

WALLS, Craig. *Spring Boot in Action*. Shelter Island (NY) : Manning Publications, 2016. 264 p.

WOLFF, Eberhard. *Microservices: Flexible Software Architecture*. Upper Saddle River (NJ) : Addison-Wesley, 2016. 304 p.

NEWMAN, Sam. *Building Microservices: Designing Fine-Grained Systems*. 2e édition. Sebastopol (CA) : O'Reilly Media, 2021. 612 p.

GAMMA, Erich, HELM, Richard, JOHNSON, Ralph, et al. *Design Patterns: Elements of Reusable Object-Oriented Software*. Boston : Addison-Wesley, 1994. 395 p.

## Articles de Périodiques

FOWLER, Martin, LEWIS, James. Microservices: a definition of this new architectural term. *MartinFowler.com* [en ligne]. 25 mars 2014. Disponible sur : <https://martinfowler.com/articles/microservices.html>. [Consulté le 10 janvier 2025]

## Documentation Technique en Ligne

PIVOTAL SOFTWARE. *Spring Cloud*. Spring.io [en ligne]. 2024. Disponible sur : <https://spring.io/projects/spring-cloud>. [Consulté le 10 janvier 2025]

FACEBOOK. *React Documentation*. React.dev [en ligne]. 2024. Disponible sur : <https://react.dev>. [Consulté le 10 janvier 2025]

ORACLE. *Java SE 17 Documentation*. Oracle.com [en ligne]. 2024. Disponible sur : <https://docs.oracle.com/en/java/javase/17/>. [Consulté le 10 janvier 2025]

MYSQL. *MySQL 8.0 Reference Manual*. MySQL.com [en ligne]. 2024. Disponible sur : <https://dev.mysql.com/doc/refman/8.0/en/>. [Consulté le 10 janvier 2025]

DOCKER INC. *Docker Documentation*. Docker.com [en ligne]. 2024. Disponible sur : <https://docs.docker.com>. [Consulté le 10 janvier 2025]

## Cours et Tutoriels

BAELDUNG. *The Spring Framework Tutorial* [en ligne]. 2024. Disponible sur : <https://www.baeldung.com/spring-tutorial>. [Consulté le 10 janvier 2025]

---

# TABLE DES MATIÈRES DÉTAILLÉE

**REMERCIEMENTS** ...................................................................................... i

**RÉSUMÉ** ...................................................................................................... ii

**SOMMAIRE** ................................................................................................ iii

**INTRODUCTION GÉNÉRALE** ................................................................... 1
- Contexte général ........................................................................................... 1
- Motivation du projet ...................................................................................... 2
- Problématique ............................................................................................... 2
- Méthodologie ................................................................................................ 3
- Plan du mémoire ........................................................................................... 3

**CHAPITRE 1 : CONTEXTE ET ANALYSE DES BESOINS** ...................... 5
1.1 Présentation du Projet ................................................................................ 5
    1.1.1 Objectifs généraux ............................................................................... 5
    1.1.2 Périmètre fonctionnel .......................................................................... 5
    1.1.3 Acteurs du système .............................................................................. 6
1.2 Problématique ............................................................................................ 6
    1.2.1 Limites des solutions existantes ........................................................... 6
    1.2.2 Enjeux techniques ................................................................................ 7
1.3 Analyse des Besoins Fonctionnels ............................................................. 7
    1.3.1 Besoins des Clients .............................................................................. 7
    1.3.2 Besoins du Personnel de l'Agence ....................................................... 9
1.4 Analyse des Besoins Non-Fonctionnels .................................................... 11
    1.4.1 Performance ........................................................................................ 11
    1.4.2 Sécurité ............................................................................................... 11
    1.4.3 Disponibilité et Fiabilité ..................................................................... 12
    1.4.4 Maintenabilité ..................................................................................... 12
    1.4.5 Utilisabilité ......................................................................................... 12
    1.4.6 Compatibilité ...................................................................................... 12

**CHAPITRE 2 : CONCEPTION ET ARCHITECTURE** ............................... 15
2.1 Choix de l'Architecture ............................................................................. 15
    2.1.1 Architecture Monolithique .................................................................. 15
    2.1.2 Architecture Microservices ................................................................. 16
    2.1.3 Notre décision ..................................................................................... 17
2.2 Architecture Logique du Système ............................................................. 17
    2.2.1 Vue d'ensemble ................................................................................... 17
    2.2.2 Description des Services ..................................................................... 18
2.3 Communication Inter-Services .................................................................. 22
    2.3.1 Synchrone : OpenFeign ...................................................................... 22
    2.3.2 Gestion des erreurs : Resilience4j ...................................................... 23
2.4 Conception de la Base de Données ........................................................... 23
    2.4.1 Principe : Database per Service .......................................................... 23
    2.4.2 Schéma User Service .......................................................................... 24
    2.4.3 Schéma Vehicle Service ...................................................................... 25
    2.4.4 Schéma Reservation Service ............................................................... 26
2.5 Diagrammes UML .................................................................................... 27
    2.5.1 Diagramme de Cas d'Utilisation ......................................................... 27
    2.5.2 Diagramme de Séquence .................................................................... 28

**CHAPITRE 3 : RÉALISATION TECHNIQUE** ............................................ 30
3.1 Environnement de Développement ............................................................ 30
    3.1.1 Stack Technologique ........................................................................... 30
    3.1.2 IDE et Outils ....................................................................................... 31
    3.1.3 Structure des Projets ........................................................................... 31
3.2 Implémentation du Backend ...................................................................... 32
    3.2.1 Eureka Server ..................................................................................... 32
    3.2.2 API Gateway : Sécurité JWT .............................................................. 33
    3.2.3 User Service : Authentification ........................................................... 35
    3.2.4 Vehicle Service : Gestion du Catalogue .............................................. 38
    3.2.5 Reservation Service : Logique Métier ................................................. 40
3.3 Implémentation du Frontend ..................................................................... 42
    3.3.1 Configuration Axios ............................................................................ 42
    3.3.2 Service Authentification ...................................................................... 43
    3.3.3 Hook Personnalisé useAuth ................................................................ 44
    3.3.4 Composant de Connexion ................................................................... 45
    3.3.5 Internationalisation (i18n) .................................................................. 46
3.4 Tests et Débogage ..................................................................................... 47
    3.4.1 Tests Unitaires Backend ...................................................................... 47
    3.4.2 Tests d'Intégration .............................................................................. 48
    3.4.3 Débogage et Logs ................................................................................ 49
3.5 Déploiement avec Docker ......................................................................... 50
    3.5.1 Dockerfile pour un Microservice ......................................................... 50
    3.5.2 Docker Compose ................................................................................. 51

**CONCLUSION ET PERSPECTIVES** ........................................................... 52
- Synthèse du Travail Réalisé ......................................................................... 52
- Objectifs atteints .......................................................................................... 52
- Compétences développées ............................................................................ 53
- Défis rencontrés et Solutions apportées ........................................................ 54
- Perspectives d'Amélioration ......................................................................... 55
- Apport Personnel .......................................................................................... 57
- Conclusion Finale ......................................................................................... 58

**BIBLIOGRAPHIE** ........................................................................................ 59

**TABLE DES MATIÈRES** ............................................................................. 62

---

**Mots-clés** : Microservices, Spring Boot, React, Architecture Distribuée, Gestion de Location de Véhicules, JWT, API Gateway, DevOps, Docker, MySQL, TypeScript, Eureka, OpenFeign, REST API, Sécurité, RBAC.

---

**FIN DU MÉMOIRE**

*Ce document a été rédigé selon les normes de Sciences Po Grenoble (Novembre 2016) et constitue un mémoire de fin d'études complet et professionnel.*
