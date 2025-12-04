# Docker Deployment Guide

## Prérequis

- Docker Desktop installé et en cours d'exécution
- MySQL/WAMP en cours d'exécution sur la machine hôte (pour host.docker.internal)
- Les bases de données créées: gdldv_vehicle_db, gdldv_user_db, gdldv_reservation_db

## Démarrage rapide

### 1. Build les images Docker

```bash
docker-compose build
```

Cette commande va:
- Compiler chaque service avec Maven
- Créer les images Docker pour tous les microservices

### 2. Lancer tous les services

```bash
docker-compose up
```

Options utiles:
- `-d` : mode détaché (background)
- `--build` : rebuild les images avant de démarrer

### 3. Vérifier les logs

```bash
# Tous les services
docker-compose logs -f

# Service spécifique
docker-compose logs -f eureka-server
docker-compose logs -f api-gateway
```

### 4. Vérifier que les services sont démarrés

- Eureka Dashboard: http://localhost:8761
- API Gateway: http://localhost:8000/actuator/health
- Vehicle Service: http://localhost:8001/vehicle-service/actuator/health
- User Service: http://localhost:8003/user-service/actuator/health
- Reservation Service: http://localhost:8002/reservation-service/actuator/health

### 5. Arrêter les services

```bash
# Arrêter
docker-compose down

# Arrêter et supprimer les volumes
docker-compose down -v
```

## Ordre de démarrage

Le docker-compose.yml gère automatiquement l'ordre de démarrage:

1. **Eureka Server** (port 8761) - démarre en premier
2. **API Gateway** (port 8000) - attend que Eureka soit healthy
3. **Vehicle Service** (port 8001) - attend que Eureka soit healthy
4. **User Service** (port 8003) - attend que Eureka soit healthy
5. **Reservation Service** (port 8002) - attend que Eureka soit healthy

## Variables d'environnement

Les variables d'environnement sont configurées dans docker-compose.yml:

- `JAVA_OPTS`: Options JVM (mémoire, etc.)
- `SPRING_DATASOURCE_URL`: URL de connexion MySQL
- `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE`: URL Eureka Server

## Troubleshooting

### Les services ne peuvent pas se connecter à MySQL

Vérifiez que:
1. MySQL/WAMP est en cours d'exécution
2. Les bases de données sont créées
3. L'utilisateur root a les permissions nécessaires

### Eureka Server ne démarre pas

```bash
docker-compose logs eureka-server
```

Vérifiez les logs pour les erreurs.

### Un service ne s'enregistre pas dans Eureka

1. Vérifiez que Eureka Server est accessible
2. Vérifiez les logs du service
3. Vérifiez les variables d'environnement EUREKA_CLIENT_SERVICEURL_DEFAULTZONE

## Commandes utiles

```bash
# Voir les conteneurs en cours d'exécution
docker-compose ps

# Redémarrer un service spécifique
docker-compose restart api-gateway

# Rebuilder un service spécifique
docker-compose build --no-cache eureka-server

# Entrer dans un conteneur
docker-compose exec api-gateway /bin/sh

# Voir l'utilisation des ressources
docker stats
```

## Build de développement rapide

Pour éviter de rebuilder avec Maven à chaque fois, vous pouvez:

1. Builder localement avec Maven:
```bash
mvn clean package -DskipTests
```

2. Créer des Dockerfiles légers qui copient directement le JAR

Voir les fichiers `Dockerfile.dev` dans chaque service (si disponibles).
