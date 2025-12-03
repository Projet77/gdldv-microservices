# GDLDV - Système de Gestion des Locations De Voitures

Système de gestion des locations de voitures basé sur une architecture microservices avec Spring Boot.

## Architecture

Ce projet utilise une architecture microservices composée de 4 services:

- **Vehicle Service** (Port 8001) - Gestion des véhicules
- **Reservation Service** (Port 8002) - Gestion des réservations
- **User Service** (Port 8003) - Gestion des utilisateurs et authentification
- **API Gateway** (Port 8000) - Point d'entrée unique pour tous les services

## Technologies utilisées

- **Framework**: Spring Boot 3.3.0
- **Langage**: Java 17 (LTS)
- **Build Tool**: Maven
- **Base de données**: MySQL (via WAMP)
- **Architecture**: Microservices
- **Communication inter-services**: OpenFeign
- **API Gateway**: Spring Cloud Gateway
- **Sécurité**: Spring Security + JWT
- **Documentation API**: SpringDoc OpenAPI (Swagger)
- **Templates**: Thymeleaf

## Prérequis

Avant de démarrer le projet, assurez-vous d'avoir installé:

- [x] JDK 17 (version LTS recommandée)
- [x] Maven 3.8+
- [x] IntelliJ IDEA (Community ou Pro)
- [x] WAMP Server (avec MySQL actif)
- [x] Git

**Note:** Si vous avez Java 24 installé, consultez `FIX-JAVA-VERSION.md` pour configurer Java 17 dans IntelliJ.

## Structure du projet

```
gdldv-project/
├── pom.xml (parent)
├── .gitignore
│
├── vehicle-service/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/gdldv/vehicle/
│   │   │   │   ├── entity/Vehicle.java
│   │   │   │   ├── repository/VehicleRepository.java
│   │   │   │   ├── service/VehicleService.java
│   │   │   │   ├── controller/VehicleController.java
│   │   │   │   └── VehicleServiceApplication.java
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── templates/vehicle/
│   │   └── test/
│   └── pom.xml
│
├── reservation-service/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/gdldv/reservation/
│   │   │   │   ├── entity/Reservation.java
│   │   │   │   ├── repository/ReservationRepository.java
│   │   │   │   ├── service/ReservationService.java
│   │   │   │   ├── controller/ReservationController.java
│   │   │   │   ├── client/VehicleClient.java
│   │   │   │   ├── dto/VehicleDTO.java
│   │   │   │   └── ReservationServiceApplication.java
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── templates/reservation/
│   │   └── test/
│   └── pom.xml
│
├── user-service/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/gdldv/user/
│   │   │   │   ├── entity/User.java
│   │   │   │   ├── repository/UserRepository.java
│   │   │   │   ├── service/UserService.java
│   │   │   │   ├── controller/UserController.java
│   │   │   │   ├── security/SecurityConfig.java
│   │   │   │   └── UserServiceApplication.java
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── templates/user/
│   │   └── test/
│   └── pom.xml
│
└── api-gateway/
    ├── src/
    │   ├── main/
    │   │   ├── java/com/gdldv/gateway/
    │   │   │   ├── config/CorsConfig.java
    │   │   │   └── ApiGatewayApplication.java
    │   │   └── resources/
    │   │       └── application.properties
    │   └── test/
    └── pom.xml
```

## Installation et Configuration

### 1. Cloner le projet

```bash
cd "C:\Users\Abdou\Documents\Projet stage\Gestion des locations"
```

### 2. Créer les bases de données MySQL

1. **Démarrer WAMP Server** et vérifier que MySQL est actif (icône verte)

2. **Ouvrir phpMyAdmin**: http://localhost/phpmyadmin

3. **Créer les 3 bases de données vides** (juste les bases, pas les tables):
   - Cliquer sur "Nouvelle base de données"
   - Nom: `gdldv_vehicle_db`
   - Interclassement: `utf8mb4_unicode_ci`
   - Cliquer "Créer"

   Répéter pour:
   - `gdldv_reservation_db`
   - `gdldv_user_db`

**Note:** Les tables seront créées automatiquement par Hibernate au premier démarrage grâce aux annotations JPA (@Entity, @Table, etc.) et à la configuration `spring.jpa.hibernate.ddl-auto=update`.

### 3. Configurer IntelliJ IDEA

1. **Ouvrir le projet**:
   - File → Open
   - Sélectionner le dossier `Gestion des locations`

2. **Charger les dépendances Maven**:
   - Clic droit sur le projet → Maven → Reload project
   - Attendre que toutes les dépendances soient téléchargées

3. **Vérifier le JDK**:
   - File → Project Structure → Project
   - SDK: Sélectionner JDK 21

## Démarrage des services

### Option 1: Démarrer depuis IntelliJ IDEA

Pour chaque service, créer une configuration:

1. **Run → Edit Configurations**
2. **+ → Application**
3. **Configuration**:
   - Name: `Vehicle Service`
   - Module: `vehicle-service`
   - Main class: `com.gdldv.vehicle.VehicleServiceApplication`
4. **Répéter pour les 4 services**

**Ordre de démarrage recommandé**:
1. Vehicle Service (8001)
2. User Service (8003)
3. Reservation Service (8002)
4. API Gateway (8000)

### Option 2: Démarrer en ligne de commande

```bash
# Terminal 1 - Vehicle Service
cd vehicle-service
mvn spring-boot:run

# Terminal 2 - User Service
cd user-service
mvn spring-boot:run

# Terminal 3 - Reservation Service
cd reservation-service
mvn spring-boot:run

# Terminal 4 - API Gateway
cd api-gateway
mvn spring-boot:run
```

## Vérification du démarrage

Une fois les services lancés, vérifier leur état:

### Health Checks
- Vehicle Service: http://localhost:8001/vehicle-service/actuator/health
- Reservation Service: http://localhost:8002/reservation-service/actuator/health
- User Service: http://localhost:8003/user-service/actuator/health
- API Gateway: http://localhost:8000/actuator/health

### Documentation Swagger
- Vehicle Service: http://localhost:8001/vehicle-service/swagger-ui.html
- Reservation Service: http://localhost:8002/reservation-service/swagger-ui.html
- User Service: http://localhost:8003/user-service/swagger-ui.html

## Utilisation de l'API

### Via API Gateway (Recommandé)

Toutes les requêtes passent par le port 8000:

```bash
# Récupérer tous les véhicules
GET http://localhost:8000/api/vehicles/vehicles

# Créer un véhicule
POST http://localhost:8000/api/vehicles/vehicles
Content-Type: application/json
{
  "brand": "Toyota",
  "model": "Corolla",
  "licensePlate": "ABC-123",
  "mileage": 50000,
  "dailyPrice": 50.0,
  "category": "Berline",
  "status": "AVAILABLE"
}

# Récupérer tous les utilisateurs
GET http://localhost:8000/api/users/users

# Créer une réservation
POST http://localhost:8000/api/reservations/reservations
Content-Type: application/json
{
  "vehicleId": 1,
  "userId": 1,
  "startDate": "2025-12-10",
  "endDate": "2025-12-15",
  "notes": "Besoin d'un GPS"
}
```

### Directement via les services

```bash
# Vehicle Service direct
GET http://localhost:8001/vehicle-service/vehicles

# Reservation Service direct
GET http://localhost:8002/reservation-service/reservations

# User Service direct
GET http://localhost:8003/user-service/users
```

## Configuration des services

Chaque service a son propre fichier `application.properties`:

- **Vehicle Service**: `vehicle-service/src/main/resources/application.properties`
- **Reservation Service**: `reservation-service/src/main/resources/application.properties`
- **User Service**: `user-service/src/main/resources/application.properties`
- **API Gateway**: `api-gateway/src/main/resources/application.properties`

Les paramètres MySQL par défaut sont:
- **URL**: `jdbc:mysql://localhost:3306/gdldv_xxx_db`
- **Username**: `root`
- **Password**: (vide)

## Dépannage

### Erreur "Connection refused"
- Vérifier que WAMP Server est démarré
- Vérifier que MySQL est actif (icône verte)

### Erreur "Unknown database"
- Créer les bases de données dans phpMyAdmin

### Erreur "Port already in use"
- Vérifier qu'aucun autre service n'utilise les ports 8000-8003
- Tuer le processus: `netstat -ano | findstr :8001`

### Les tables ne se créent pas
- Vérifier `spring.jpa.hibernate.ddl-auto=update` dans application.properties
- Vérifier les logs au démarrage du service

## Endpoints principaux

### Vehicle Service (8001)
- GET `/vehicles` - Liste des véhicules
- GET `/vehicles/{id}` - Détails d'un véhicule
- POST `/vehicles` - Créer un véhicule
- PUT `/vehicles/{id}` - Modifier un véhicule
- DELETE `/vehicles/{id}` - Supprimer un véhicule
- GET `/vehicles/status/{status}` - Véhicules par statut
- GET `/vehicles/category/{category}` - Véhicules par catégorie

### Reservation Service (8002)
- GET `/reservations` - Liste des réservations
- GET `/reservations/{id}` - Détails d'une réservation
- POST `/reservations` - Créer une réservation
- PUT `/reservations/{id}` - Modifier une réservation
- DELETE `/reservations/{id}` - Supprimer une réservation
- GET `/reservations/user/{userId}` - Réservations d'un utilisateur
- GET `/reservations/vehicle/{vehicleId}` - Réservations d'un véhicule
- GET `/reservations/check-availability` - Vérifier disponibilité

### User Service (8003)
- GET `/users` - Liste des utilisateurs
- GET `/users/{id}` - Détails d'un utilisateur
- POST `/users` - Créer un utilisateur
- PUT `/users/{id}` - Modifier un utilisateur
- DELETE `/users/{id}` - Supprimer un utilisateur
- GET `/users/email/{email}` - Utilisateur par email
- GET `/users/role/{role}` - Utilisateurs par rôle

## Développement

### Comment les tables sont créées automatiquement

Le projet utilise les annotations JPA/Hibernate pour créer automatiquement les tables:

- Voir le guide complet: `ANNOTATIONS-JPA-GUIDE.md`
- Configuration: `spring.jpa.hibernate.ddl-auto=update`
- Les entités (`@Entity`) définissent la structure des tables
- Au démarrage, Hibernate crée ou met à jour les tables automatiquement

### Compiler le projet
```bash
mvn clean install
```

### Lancer les tests
```bash
mvn test
```

### Créer les packages
```bash
mvn package
```

## Prochaines étapes

- [ ] Implémenter l'authentification JWT complète
- [ ] Ajouter des tests unitaires et d'intégration
- [ ] Créer des DTOs pour les réponses API
- [ ] Ajouter la gestion des erreurs globale
- [ ] Implémenter la pagination pour les listes
- [ ] Ajouter des validations métier avancées
- [ ] Créer une interface web (Angular/React)
- [ ] Configurer un service de découverte (Eureka)
- [ ] Ajouter un système de configuration centralisée
- [ ] Implémenter le logging distribué

## Auteur

**Abdou**
Email: Al.b3@zig.univ.sn
Projet de stage - 2025

## Licence

Ce projet est destiné à un usage académique.
#   g d l d v - m i c r o s e r v i c e s  
 