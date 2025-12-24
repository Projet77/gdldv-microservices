# 🌐 Ports et URLs - GDLDV

## 📊 Vue d'ensemble des services

| Service | Port | URL | Description |
|---------|------|-----|-------------|
| **Frontend** | 3000 | http://localhost:3000 | Interface utilisateur React |
| **API Gateway** | 8000 | http://localhost:8000 | Point d'entrée principal API |
| **Vehicle Service** | 8001 | http://localhost:8001 | Gestion des véhicules |
| **Reservation Service** | 8002 | http://localhost:8002 | Gestion des réservations |
| **User Service** | 8003 | http://localhost:8003 | Gestion des utilisateurs |
| **Rental Service** | 8004 | http://localhost:8004 | Gestion des locations |
| **Eureka Server** | 8761 | http://localhost:8761 | Service Discovery |
| **Config Server** | 8888 | http://localhost:8888 | Configuration centralisée |
| **MySQL Vehicle** | 3307 | localhost:3307 | Base de données véhicules |
| **MySQL Reservation** | 3308 | localhost:3308 | Base de données réservations |
| **MySQL User** | 3309 | localhost:3309 | Base de données utilisateurs |
| **MySQL Rental** | 3310 | localhost:3310 | Base de données locations |
| **Redis** | 6379 | localhost:6379 | Cache distribué |

---

## 🔗 URLs importantes

### Interface utilisateur
- **Application principale** : http://localhost:3000
- **Page de connexion** : http://localhost:3003/user-service/login

### Monitoring et Administration
- **Eureka Dashboard** : http://localhost:8761
  - Vue d'ensemble de tous les microservices
  - État de santé (health status)
  - Instances enregistrées

### APIs REST (Swagger UI)

| Service | Swagger URL |
|---------|-------------|
| Vehicle Service | http://localhost:8001/swagger-ui.html |
| Reservation Service | http://localhost:8002/swagger-ui.html |
| User Service | http://localhost:8003/swagger-ui.html |
| Rental Service | http://localhost:8004/swagger-ui.html |

### API Gateway (Routage)

Toutes les APIs sont accessibles via l'API Gateway :

```bash
# Via API Gateway (recommandé)
http://localhost:8000/api/vehicles
http://localhost:8000/api/reservations
http://localhost:8000/api/users
http://localhost:8000/api/rentals

# Ou directement (développement uniquement)
http://localhost:8001/vehicle-service/vehicles
http://localhost:8002/reservation-service/reservations
http://localhost:8003/user-service/api/users
http://localhost:8004/rental-service/rentals
```

### Health Checks (Actuator)

```bash
# Via API Gateway
http://localhost:8000/actuator/health

# Services individuels
http://localhost:8001/actuator/health  # Vehicle
http://localhost:8002/actuator/health  # Reservation
http://localhost:8003/actuator/health  # User
http://localhost:8004/actuator/health  # Rental
http://localhost:8761/actuator/health  # Eureka
http://localhost:8888/actuator/health  # Config
```

---

## 🗄️ Connexions aux bases de données

### Via MySQL Workbench ou DBeaver

| Database | Host | Port | Username | Password | Schema |
|----------|------|------|----------|----------|--------|
| Vehicle DB | localhost | 3307 | gdldv_user | gdldv_password | gdldv_vehicle_db |
| Reservation DB | localhost | 3308 | gdldv_user | gdldv_password | gdldv_reservation_db |
| User DB | localhost | 3309 | gdldv_user | gdldv_password | gdldv_user_db |
| Rental DB | localhost | 3310 | gdldv_user | gdldv_password | gdldv_rental_db |

**Root user :**
- Username: `root`
- Password: `root` (voir `.env` pour la valeur exacte)

### Exemple de connexion MySQL CLI

```bash
# Vehicle Database
mysql -h 127.0.0.1 -P 3307 -u gdldv_user -p
# Password: gdldv_password

# Ou avec root
mysql -h 127.0.0.1 -P 3307 -u root -p
# Password: root
```

---

## 🔴 Redis

### Connexion Redis CLI

```bash
# Via Docker
docker exec -it gdldv-redis redis-cli

# Commandes utiles
127.0.0.1:6379> PING
127.0.0.1:6379> INFO
127.0.0.1:6379> KEYS *
127.0.0.1:6379> FLUSHDB  # Vider le cache (développement uniquement)
```

### Connexion externe (RedisInsight, etc.)
- **Host** : localhost
- **Port** : 6379
- **Password** : aucun (non configuré en développement)

---

## 🔐 Authentification

### JWT Token

Les endpoints protégés nécessitent un token JWT dans le header :

```bash
Authorization: Bearer <votre-token-jwt>
```

### Obtenir un token

```bash
# POST http://localhost:8003/api/auth/login
curl -X POST http://localhost:8003/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@gdldv.com",
    "password": "admin123"
  }'
```

Réponse :
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "expiresIn": 86400000
}
```

---

## 🧪 Tests rapides

### Test via cURL

```bash
# Tester l'API Gateway
curl http://localhost:8000/actuator/health

# Lister les véhicules
curl http://localhost:8000/api/vehicles

# Vérifier Eureka
curl http://localhost:8761/eureka/apps

# Tester Redis
docker exec -it gdldv-redis redis-cli PING
```

### Test via navigateur

1. **Eureka** : http://localhost:8761
   - Vérifier que 5 services sont enregistrés (VEHICLE, RESERVATION, USER, RENTAL, API-GATEWAY)

2. **Frontend** : http://localhost:3000
   - Vérifier que la page d'accueil s'affiche

3. **Swagger** : http://localhost:8001/swagger-ui.html
   - Tester les endpoints API interactivement

---

## 📝 Variables d'environnement importantes

```env
# API Gateway (point d'entrée principal)
API_GATEWAY_URL=http://localhost:8000

# Frontend
FRONTEND_URL=http://localhost:3000

# Eureka
EUREKA_URL=http://localhost:8761/eureka/

# Config Server
CONFIG_SERVER_URL=http://localhost:8888
```

---

## 🔍 Ordre de démarrage (automatique)

Les services démarrent dans cet ordre (géré par Docker Compose) :

1. **Bases de données** : MySQL × 4, Redis
2. **Infrastructure** : Eureka Server, Config Server
3. **Services métier** : Vehicle, User, Reservation, Rental
4. **API Gateway**
5. **Frontend**

Tous les services attendent que leurs dépendances soient "healthy" avant de démarrer.

---

## 🆘 Vérification rapide

### Tous les services fonctionnent ?

```bash
# Vérifier l'état
docker-compose -f docker-compose-full.yml ps

# Tous doivent afficher "Up (healthy)"
```

### Test complet

```bash
# Frontend
curl http://localhost:3000

# API Gateway
curl http://localhost:8000/actuator/health

# Eureka (voir les services enregistrés)
curl http://localhost:8761/eureka/apps | grep -i status

# Redis
docker exec -it gdldv-redis redis-cli PING
```

---

**📌 Conseil** : Ajoutez cette page à vos favoris pour un accès rapide aux URLs pendant le développement !
