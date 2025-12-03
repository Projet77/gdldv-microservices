# Guide de Démarrage Rapide - GDLDV

Ce guide vous permet de démarrer rapidement le projet en quelques étapes.

## Étapes de démarrage

### 1. Vérifier les prérequis

Assurez-vous d'avoir:
- [x] JDK 17 installé (pas Java 24!)
- [x] Maven installé
- [x] WAMP Server installé et démarré
- [x] IntelliJ IDEA installé

**⚠️ Important:** Si vous avez Java 24, consultez `FIX-JAVA-VERSION.md` pour configurer Java 17.

### 2. Créer les bases de données MySQL (vides)

1. Ouvrir http://localhost/phpmyadmin
2. Cliquer sur "Nouvelle base de données"
3. Créer ces 3 bases (vides):
   - Nom: `gdldv_vehicle_db`, Interclassement: `utf8mb4_unicode_ci`, Cliquer "Créer"
   - Nom: `gdldv_reservation_db`, Interclassement: `utf8mb4_unicode_ci`, Cliquer "Créer"
   - Nom: `gdldv_user_db`, Interclassement: `utf8mb4_unicode_ci`, Cliquer "Créer"

**Important:** Ne créez PAS les tables! Elles seront créées automatiquement par Hibernate au démarrage des services grâce à:
- Les annotations JPA: `@Entity`, `@Table`, `@Column`, etc.
- La configuration: `spring.jpa.hibernate.ddl-auto=update`

### 3. Ouvrir le projet dans IntelliJ

1. Lancer IntelliJ IDEA
2. File → Open
3. Sélectionner le dossier `Gestion des locations`
4. Attendre que Maven charge toutes les dépendances

### 4. Charger les dépendances Maven

Dans IntelliJ:
1. Ouvrir la fenêtre Maven (View → Tool Windows → Maven)
2. Cliquer sur l'icône 🔄 "Reload All Maven Projects"
3. Attendre la fin du téléchargement

### 5. Démarrer les services

**Ordre recommandé:**

#### Service 1: Vehicle Service
1. Ouvrir `vehicle-service/src/main/java/com/gdldv/vehicle/VehicleServiceApplication.java`
2. Clic droit → Run 'VehicleServiceApplication'
3. Attendre le message: `Tomcat started on port(s): 8001`

#### Service 2: User Service
1. Ouvrir `user-service/src/main/java/com/gdldv/user/UserServiceApplication.java`
2. Clic droit → Run 'UserServiceApplication'
3. Attendre le message: `Tomcat started on port(s): 8003`

#### Service 3: Reservation Service
1. Ouvrir `reservation-service/src/main/java/com/gdldv/reservation/ReservationServiceApplication.java`
2. Clic droit → Run 'ReservationServiceApplication'
3. Attendre le message: `Tomcat started on port(s): 8002`

#### Service 4: API Gateway
1. Ouvrir `api-gateway/src/main/java/com/gdldv/gateway/ApiGatewayApplication.java`
2. Clic droit → Run 'ApiGatewayApplication'
3. Attendre le message: `Netty started on port 8000`

### 6. Vérifier que tout fonctionne

Ouvrir dans le navigateur:

- ✅ Vehicle Service Health: http://localhost:8001/vehicle-service/actuator/health
- ✅ User Service Health: http://localhost:8003/user-service/actuator/health
- ✅ Reservation Service Health: http://localhost:8002/reservation-service/actuator/health
- ✅ API Gateway Health: http://localhost:8000/actuator/health

**Résultat attendu pour chaque URL:**
```json
{"status":"UP"}
```

### 7. Accéder à Swagger UI

Documentation interactive des APIs:

- Vehicle Service: http://localhost:8001/vehicle-service/swagger-ui.html
- User Service: http://localhost:8003/user-service/swagger-ui.html
- Reservation Service: http://localhost:8002/reservation-service/swagger-ui.html

### 8. Tester l'API

**Créer un véhicule:**
```bash
curl -X POST http://localhost:8000/api/vehicles/vehicles \
  -H "Content-Type: application/json" \
  -d '{
    "brand": "Toyota",
    "model": "Corolla",
    "licensePlate": "ABC-123",
    "mileage": 50000,
    "dailyPrice": 50.0,
    "category": "Berline",
    "status": "AVAILABLE"
  }'
```

**Récupérer tous les véhicules:**
```bash
curl http://localhost:8000/api/vehicles/vehicles
```

## Dépannage rapide

### Problème: "Connection refused"
**Solution:** Vérifier que WAMP est démarré et que MySQL est actif (icône verte)

### Problème: "Unknown database"
**Solution:** Exécuter le script `create-databases.sql`

### Problème: "Port already in use"
**Solution:** Un autre service utilise ce port. Arrêter le service ou changer le port dans `application.properties`

### Problème: Maven ne télécharge pas les dépendances
**Solution:**
1. File → Settings → Build, Execution, Deployment → Maven
2. Vérifier la configuration Maven
3. Reload All Maven Projects

## Commandes utiles

### Compiler le projet complet
```bash
mvn clean install
```

### Démarrer un service en ligne de commande
```bash
cd vehicle-service
mvn spring-boot:run
```

### Arrêter tous les services
Dans IntelliJ: Cliquer sur le bouton Stop ⏹️ pour chaque service

### Voir les logs d'un service
Dans IntelliJ: Onglet "Run" en bas de l'écran

## Structure des ports

| Service | Port | Context Path | Health Check |
|---------|------|--------------|--------------|
| Vehicle Service | 8001 | /vehicle-service | http://localhost:8001/vehicle-service/actuator/health |
| Reservation Service | 8002 | /reservation-service | http://localhost:8002/reservation-service/actuator/health |
| User Service | 8003 | /user-service | http://localhost:8003/user-service/actuator/health |
| API Gateway | 8000 | / | http://localhost:8000/actuator/health |

## Prochaines étapes

Maintenant que le projet est démarré:

1. Explorer les APIs via Swagger UI
2. Créer des données de test (véhicules, utilisateurs, réservations)
3. Tester les endpoints via Postman ou curl
4. Consulter les logs dans IntelliJ
5. Vérifier les tables créées dans phpMyAdmin

## Ressources

- README complet: `README.md`
- Guide des annotations JPA: `ANNOTATIONS-JPA-GUIDE.md`
- Fiches de référence: `FICHE_DEMARRAGE_COMPLET_INTELLIJ.md` et `APPLICATION_PROPERTIES_COMPLET.md`

Bon développement! 🚀
