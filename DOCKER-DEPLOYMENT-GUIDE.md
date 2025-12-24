# 📦 Guide Complet de Déploiement Docker - GDLDV Microservices

## Table des Matières
1. [Vue d'ensemble de l'architecture](#vue-densemble-de-larchitecture)
2. [Prérequis](#prérequis)
3. [Structure du projet Docker](#structure-du-projet-docker)
4. [Configuration de l'environnement](#configuration-de-lenvironnement)
5. [Déploiement étape par étape](#déploiement-étape-par-étape)
6. [Commandes Docker utiles](#commandes-docker-utiles)
7. [Monitoring et Logs](#monitoring-et-logs)
8. [Troubleshooting](#troubleshooting)
9. [Déploiement en Production](#déploiement-en-production)

---

## Vue d'ensemble de l'architecture

### Architecture Microservices
```
┌─────────────────────────────────────────────────────────────┐
│                      API Gateway (8000)                      │
│              Authentication + Routing + Load Balancing        │
└────────────┬────────────────────────────────────────────────┘
             │
    ┌────────┴────────┐
    │                 │
┌───▼────┐      ┌────▼──────────┐
│ Eureka │      │ Config Server │
│ (8761) │      │    (8888)     │
└────────┘      └───────────────┘
                      │
    ┌─────────────────┼─────────────────┬──────────────┐
    │                 │                 │              │
┌───▼────────┐ ┌─────▼──────┐ ┌────────▼─────┐ ┌─────▼──────┐
│  Vehicle   │ │Reservation │ │     User     │ │   Rental   │
│  Service   │ │  Service   │ │   Service    │ │  Service   │
│   (8001)   │ │   (8002)   │ │   (8003)     │ │   (8004)   │
└─────┬──────┘ └─────┬──────┘ └──────┬───────┘ └─────┬──────┘
      │              │                │               │
┌─────▼──────┐ ┌─────▼──────┐ ┌──────▼───────┐ ┌─────▼──────┐
│   MySQL    │ │   MySQL    │ │    MySQL     │ │   MySQL    │
│  Vehicle   │ │Reservation │ │     User     │ │   Rental   │
│   (3307)   │ │   (3308)   │ │    (3309)    │ │   (3310)   │
└────────────┘ └────────────┘ └──────────────┘ └────────────┘
```

### Services Déployés

#### Services d'Infrastructure (3)
1. **Eureka Server** (Port 8761)
   - Service Discovery
   - Health monitoring des services

2. **Config Server** (Port 8888)
   - Configuration centralisée
   - Gestion des profils (dev, prod)

3. **API Gateway** (Port 8000)
   - Point d'entrée unique
   - Authentification JWT
   - Load balancing

#### Services Métier (4)
4. **Vehicle Service** (Port 8001)
   - Gestion du parc automobile
   - Base de données: `gdldv_vehicle_db`

5. **Reservation Service** (Port 8002)
   - Gestion des réservations
   - Intégration Stripe
   - Base de données: `gdldv_reservation_db`

6. **User Service** (Port 8003)
   - Authentification & autorisation
   - Gestion des utilisateurs
   - Base de données: `gdldv_user_db`

7. **Rental Service** (Port 8004)
   - Gestion des locations actives
   - Génération de contrats PDF
   - Notifications email
   - Base de données: `gdldv_rental_db`

#### Bases de Données (4)
- **MySQL 8.0** pour chaque service métier
- Volumes persistants pour la sauvegarde des données

---

## Prérequis

### Logiciels Requis

#### Windows
```bash
# Docker Desktop for Windows (inclut Docker Compose)
# Version minimale: 4.0+
# Télécharger: https://www.docker.com/products/docker-desktop

# Vérifier l'installation
docker --version        # Docker version 24.0+
docker-compose --version  # Docker Compose version 2.0+
```

#### Linux
```bash
# Docker Engine
sudo apt-get update
sudo apt-get install docker-ce docker-ce-cli containerd.io

# Docker Compose
sudo apt-get install docker-compose-plugin

# Vérifier l'installation
docker --version
docker compose version
```

### Configuration Système Minimale

#### Développement
- **RAM**: 8 GB minimum, 16 GB recommandé
- **CPU**: 4 cores minimum
- **Disque**: 20 GB d'espace libre
- **OS**: Windows 10/11, Ubuntu 20.04+, macOS 11+

#### Production
- **RAM**: 16 GB minimum, 32 GB recommandé
- **CPU**: 8 cores minimum
- **Disque**: 50 GB d'espace libre + SSD recommandé
- **Réseau**: Connexion stable, ports 8000-8761 disponibles

### Configurer Docker Resources (Docker Desktop)

1. Ouvrir Docker Desktop
2. Settings → Resources
3. Configurer:
   - **CPUs**: 4-6
   - **Memory**: 6-8 GB
   - **Swap**: 2 GB
   - **Disk image size**: 64 GB

---

## Structure du projet Docker

```
Gestion des locations/
├── docker-compose-full.yml          # Configuration Docker complète
├── .env.example                     # Exemple de variables d'environnement
├── .env                             # Variables d'environnement (à créer)
├── .dockerignore                    # Fichiers à ignorer lors du build
│
├── scripts/                         # Scripts de déploiement
│   ├── deploy.bat                   # Déploiement Windows
│   ├── deploy.sh                    # Déploiement Linux/Mac
│   ├── stop.bat                     # Arrêt des services
│   ├── restart.bat                  # Redémarrage
│   └── logs.bat                     # Visualisation des logs
│
├── db-init/                         # Scripts d'initialisation DB
│   ├── vehicle-init.sql
│   ├── reservation-init.sql
│   ├── user-init.sql
│   └── rental-init.sql
│
├── eureka-server/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│
├── config-server/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│
├── api-gateway/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│
├── vehicle-service/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│
├── reservation-service/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│
├── user-service/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│
└── rental-service/
    ├── Dockerfile
    ├── pom.xml
    └── src/
```

---

## Configuration de l'environnement

### Étape 1: Créer le fichier .env

```bash
# Copier le fichier d'exemple
cp .env.example .env
```

### Étape 2: Configurer les variables d'environnement

Éditer le fichier `.env`:

```env
# ==================== GENERAL ====================
SPRING_PROFILES_ACTIVE=dev
COMPOSE_PROJECT_NAME=gdldv

# ==================== MYSQL DATABASES ====================
MYSQL_ROOT_PASSWORD=VotreMotDePasseSecurise123!
MYSQL_USER=gdldv_user
MYSQL_PASSWORD=VotreMotDePasseDB456!

# Database Names
VEHICLE_DB_NAME=gdldv_vehicle_db
RESERVATION_DB_NAME=gdldv_reservation_db
USER_DB_NAME=gdldv_user_db
RENTAL_DB_NAME=gdldv_rental_db

# ==================== JWT CONFIGURATION ====================
# IMPORTANT: Générer une clé secrète forte pour la production
JWT_SECRET=votre-cle-secrete-jwt-256-bits-minimum-changez-moi
JWT_EXPIRATION=86400000

# ==================== STRIPE PAYMENT ====================
# Obtenir vos clés sur: https://dashboard.stripe.com/test/apikeys
STRIPE_API_KEY=sk_test_votre_cle_stripe_ici
STRIPE_PUBLIC_KEY=pk_test_votre_cle_publique_ici

# ==================== EMAIL CONFIGURATION ====================
# Pour Gmail: Activer 2FA et générer un mot de passe d'application
# https://myaccount.google.com/apppasswords
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=votre-email@gmail.com
MAIL_PASSWORD=votre-mot-de-passe-application

# ==================== APPLICATION ADMIN ====================
ADMIN_EMAIL=admin@gdldv.com
ADMIN_NAME=GDLDV Administrator
```

### Configuration spécifique

#### 1. Configuration Gmail pour les emails
1. Activer la validation en deux étapes: https://myaccount.google.com/security
2. Générer un mot de passe d'application: https://myaccount.google.com/apppasswords
3. Utiliser ce mot de passe dans `MAIL_PASSWORD`

#### 2. Configuration Stripe (optionnel pour dev)
1. Créer un compte: https://dashboard.stripe.com/register
2. Mode Test: https://dashboard.stripe.com/test/apikeys
3. Copier les clés de test dans `.env`

---

## Déploiement étape par étape

### Méthode 1: Script de déploiement automatique (Recommandé)

#### Windows
```bash
# Naviguer vers le répertoire du projet
cd "C:\Users\Abdou\Documents\Projet stage\Gestion des locations"

# Exécuter le script de déploiement
scripts\deploy.bat
```

#### Linux/Mac
```bash
# Naviguer vers le répertoire du projet
cd ~/Gestion-des-locations

# Rendre le script exécutable
chmod +x scripts/deploy.sh

# Exécuter le script
./scripts/deploy.sh
```

Le script effectue automatiquement:
1. ✅ Vérification des prérequis
2. ✅ Configuration de l'environnement
3. ✅ Build des images Docker
4. ✅ Démarrage orchestré des services
5. ✅ Vérification de la santé des services

### Méthode 2: Déploiement manuel

#### Étape 1: Build des images Docker
```bash
docker-compose -f docker-compose-full.yml build --no-cache
```

#### Étape 2: Démarrage des bases de données
```bash
docker-compose -f docker-compose-full.yml up -d mysql-vehicle mysql-reservation mysql-user mysql-rental

# Attendre que les DB soient prêtes (30 secondes)
timeout 30  # Windows
sleep 30    # Linux/Mac
```

#### Étape 3: Démarrage Eureka Server
```bash
docker-compose -f docker-compose-full.yml up -d eureka-server

# Attendre 30 secondes pour initialisation
timeout 30
```

Vérifier: http://localhost:8761

#### Étape 4: Démarrage Config Server
```bash
docker-compose -f docker-compose-full.yml up -d config-server

# Attendre 20 secondes
timeout 20
```

Vérifier: http://localhost:8888/actuator/health

#### Étape 5: Démarrage API Gateway
```bash
docker-compose -f docker-compose-full.yml up -d api-gateway

# Attendre 20 secondes
timeout 20
```

Vérifier: http://localhost:8000/actuator/health

#### Étape 6: Démarrage des services métier
```bash
docker-compose -f docker-compose-full.yml up -d vehicle-service reservation-service user-service

# Attendre 30 secondes
timeout 30
```

#### Étape 7: Démarrage Rental Service
```bash
docker-compose -f docker-compose-full.yml up -d rental-service

# Attendre 20 secondes
timeout 20
```

#### Étape 8: Vérification du déploiement
```bash
# Voir l'état de tous les services
docker-compose -f docker-compose-full.yml ps

# Vérifier les logs si nécessaire
docker-compose -f docker-compose-full.yml logs -f
```

---

## Commandes Docker utiles

### Gestion des services

```bash
# Démarrer tous les services
docker-compose -f docker-compose-full.yml up -d

# Arrêter tous les services
docker-compose -f docker-compose-full.yml down

# Redémarrer un service spécifique
docker-compose -f docker-compose-full.yml restart vehicle-service

# Reconstruire et redémarrer un service
docker-compose -f docker-compose-full.yml up -d --build vehicle-service

# Voir l'état des services
docker-compose -f docker-compose-full.yml ps

# Supprimer tous les conteneurs et volumes
docker-compose -f docker-compose-full.yml down -v
```

### Gestion des logs

```bash
# Tous les logs en temps réel
docker-compose -f docker-compose-full.yml logs -f

# Logs d'un service spécifique
docker-compose -f docker-compose-full.yml logs -f vehicle-service

# Dernières 100 lignes
docker-compose -f docker-compose-full.yml logs --tail=100

# Logs depuis une date
docker-compose -f docker-compose-full.yml logs --since 2024-01-01T10:00:00
```

### Accès aux conteneurs

```bash
# Entrer dans un conteneur (bash)
docker exec -it gdldv-vehicle-service sh

# Exécuter une commande dans un conteneur
docker exec gdldv-vehicle-service ls -la

# Voir les processus dans un conteneur
docker top gdldv-vehicle-service
```

### Gestion des ressources

```bash
# Voir l'utilisation des ressources
docker stats

# Nettoyer les ressources non utilisées
docker system prune -a

# Nettoyer uniquement les volumes
docker volume prune

# Voir les images Docker
docker images

# Supprimer une image
docker rmi <image-id>
```

### Accès aux bases de données

```bash
# Se connecter à MySQL Vehicle
docker exec -it gdldv-mysql-vehicle mysql -u gdldv_user -p gdldv_vehicle_db

# Exporter une base de données
docker exec gdldv-mysql-vehicle mysqldump -u root -p gdldv_vehicle_db > backup.sql

# Importer une base de données
docker exec -i gdldv-mysql-vehicle mysql -u root -p gdldv_vehicle_db < backup.sql
```

---

## Monitoring et Logs

### URLs de Monitoring

| Service | URL | Description |
|---------|-----|-------------|
| **Eureka Dashboard** | http://localhost:8761 | Vue d'ensemble des services enregistrés |
| **API Gateway Health** | http://localhost:8000/actuator/health | État de santé du gateway |
| **Vehicle Service Swagger** | http://localhost:8001/swagger-ui.html | Documentation API Vehicle |
| **Reservation Service Swagger** | http://localhost:8002/swagger-ui.html | Documentation API Reservation |
| **User Service Swagger** | http://localhost:8003/swagger-ui.html | Documentation API User |
| **Rental Service Swagger** | http://localhost:8004/swagger-ui.html | Documentation API Rental |

### Health Checks

Tous les services exposent un endpoint de santé:

```bash
# Vérifier la santé de tous les services
curl http://localhost:8000/actuator/health  # Gateway
curl http://localhost:8001/actuator/health  # Vehicle
curl http://localhost:8002/actuator/health  # Reservation
curl http://localhost:8003/actuator/health  # User
curl http://localhost:8004/actuator/health  # Rental
curl http://localhost:8761/actuator/health  # Eureka
curl http://localhost:8888/actuator/health  # Config
```

### Scripts de monitoring

Créer un script `check-health.bat`:

```batch
@echo off
echo Checking GDLDV Services Health...
echo.

echo [1/7] Eureka Server:
curl -s http://localhost:8761/actuator/health
echo.

echo [2/7] Config Server:
curl -s http://localhost:8888/actuator/health
echo.

echo [3/7] API Gateway:
curl -s http://localhost:8000/actuator/health
echo.

echo [4/7] Vehicle Service:
curl -s http://localhost:8001/actuator/health
echo.

echo [5/7] Reservation Service:
curl -s http://localhost:8002/actuator/health
echo.

echo [6/7] User Service:
curl -s http://localhost:8003/actuator/health
echo.

echo [7/7] Rental Service:
curl -s http://localhost:8004/actuator/health
echo.
```

---

## Troubleshooting

### Problème: Service ne démarre pas

**Symptôme**: Un service reste en état "unhealthy" ou crash au démarrage

**Solutions**:
```bash
# 1. Vérifier les logs
docker-compose -f docker-compose-full.yml logs vehicle-service

# 2. Vérifier la mémoire disponible
docker stats

# 3. Augmenter la mémoire Docker Desktop (Settings → Resources)

# 4. Redémarrer le service
docker-compose -f docker-compose-full.yml restart vehicle-service

# 5. Rebuild complet si nécessaire
docker-compose -f docker-compose-full.yml up -d --build --force-recreate vehicle-service
```

### Problème: Erreur de connexion à la base de données

**Symptôme**: `Connection refused` ou `Access denied`

**Solutions**:
```bash
# 1. Vérifier que la DB est démarrée
docker-compose -f docker-compose-full.yml ps mysql-vehicle

# 2. Vérifier les credentials dans .env
cat .env | grep MYSQL

# 3. Se connecter manuellement à la DB
docker exec -it gdldv-mysql-vehicle mysql -u gdldv_user -p

# 4. Recréer le conteneur DB
docker-compose -f docker-compose-full.yml down mysql-vehicle
docker-compose -f docker-compose-full.yml up -d mysql-vehicle
```

### Problème: Port déjà utilisé

**Symptôme**: `Bind for 0.0.0.0:8001 failed: port is already allocated`

**Solutions Windows**:
```bash
# Trouver le processus utilisant le port
netstat -ano | findstr :8001

# Tuer le processus
taskkill /PID <PID> /F

# Ou modifier le port dans docker-compose-full.yml
ports:
  - "8011:8001"  # Utiliser 8011 au lieu de 8001
```

**Solutions Linux**:
```bash
# Trouver le processus
sudo lsof -i :8001

# Tuer le processus
sudo kill -9 <PID>
```

### Problème: Build échoue

**Symptôme**: Erreur pendant `docker-compose build`

**Solutions**:
```bash
# 1. Nettoyer le cache Maven local
rm -rf ~/.m2/repository

# 2. Build sans cache
docker-compose -f docker-compose-full.yml build --no-cache

# 3. Vérifier que Java 17 est bien utilisé
docker run --rm maven:3.9.5-eclipse-temurin-17 java -version

# 4. Build manuel d'un service
cd vehicle-service
docker build -t gdldv-vehicle-service .
```

### Problème: Services ne se voient pas entre eux

**Symptôme**: Feign client errors, service discovery fails

**Solutions**:
```bash
# 1. Vérifier que les services sont sur le même réseau
docker network inspect gdldv-network

# 2. Vérifier l'enregistrement Eureka
# Ouvrir http://localhost:8761

# 3. Ping entre conteneurs
docker exec gdldv-vehicle-service ping eureka-server

# 4. Vérifier la configuration Eureka dans chaque service
docker exec gdldv-vehicle-service cat /app/application.properties | grep eureka
```

### Problème: Performances lentes

**Solutions**:
```bash
# 1. Augmenter les ressources Docker Desktop
# Settings → Resources → Memory: 8 GB

# 2. Limiter les logs
# Dans docker-compose-full.yml, ajouter:
logging:
  driver: "json-file"
  options:
    max-size: "10m"
    max-file: "3"

# 3. Désactiver les services non utilisés
docker-compose -f docker-compose-full.yml stop rental-service
```

---

## Déploiement en Production

### Checklist de sécurité

- [ ] Changer tous les mots de passe par défaut
- [ ] Générer une JWT_SECRET forte (256 bits minimum)
- [ ] Activer HTTPS/TLS sur API Gateway
- [ ] Configurer un firewall
- [ ] Limiter les ports exposés
- [ ] Activer les logs de sécurité
- [ ] Configurer les backups automatiques
- [ ] Mettre en place un monitoring (Prometheus/Grafana)
- [ ] Configurer les alertes
- [ ] Désactiver les endpoints de débogage

### Fichier docker-compose-prod.yml

Créer un fichier séparé pour la production:

```yaml
version: '3.8'

services:
  # Configuration similaire à docker-compose-full.yml
  # avec les différences suivantes:

  vehicle-service:
    # ... configuration de base
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - JAVA_OPTS=-Xms512m -Xmx1024m
    deploy:
      resources:
        limits:
          cpus: '1.0'
          memory: 1024M
        reservations:
          cpus: '0.5'
          memory: 512M
      restart_policy:
        condition: on-failure
        delay: 5s
        max_attempts: 3
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "5"
```

### Configuration HTTPS

Ajouter un reverse proxy Nginx:

```yaml
nginx:
  image: nginx:alpine
  ports:
    - "443:443"
    - "80:80"
  volumes:
    - ./nginx/nginx.conf:/etc/nginx/nginx.conf
    - ./nginx/ssl:/etc/nginx/ssl
  depends_on:
    - api-gateway
  networks:
    - gdldv-network
```

### Backup automatique

Script `backup.sh`:

```bash
#!/bin/bash

DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/backups"

# Backup des bases de données
docker exec gdldv-mysql-vehicle mysqldump -u root -p$MYSQL_ROOT_PASSWORD gdldv_vehicle_db > $BACKUP_DIR/vehicle_$DATE.sql
docker exec gdldv-mysql-reservation mysqldump -u root -p$MYSQL_ROOT_PASSWORD gdldv_reservation_db > $BACKUP_DIR/reservation_$DATE.sql
docker exec gdldv-mysql-user mysqldump -u root -p$MYSQL_ROOT_PASSWORD gdldv_user_db > $BACKUP_DIR/user_$DATE.sql
docker exec gdldv-mysql-rental mysqldump -u root -p$MYSQL_ROOT_PASSWORD gdldv_rental_db > $BACKUP_DIR/rental_$DATE.sql

# Compression
tar -czf $BACKUP_DIR/gdldv_backup_$DATE.tar.gz $BACKUP_DIR/*.sql
rm $BACKUP_DIR/*.sql

# Garder seulement les 7 derniers backups
find $BACKUP_DIR -name "gdldv_backup_*.tar.gz" -mtime +7 -delete
```

### Monitoring avec Prometheus et Grafana

Ajouter au `docker-compose-prod.yml`:

```yaml
prometheus:
  image: prom/prometheus
  ports:
    - "9090:9090"
  volumes:
    - ./prometheus/prometheus.yml:/etc/prometheus/prometheus.yml
    - prometheus-data:/prometheus
  networks:
    - gdldv-network

grafana:
  image: grafana/grafana
  ports:
    - "3000:3000"
  environment:
    - GF_SECURITY_ADMIN_PASSWORD=admin
  volumes:
    - grafana-data:/var/lib/grafana
  networks:
    - gdldv-network
```

---

## Commandes de Production

### Déploiement

```bash
# Déploiement production
docker-compose -f docker-compose-prod.yml up -d

# Mise à jour rolling (sans downtime)
docker-compose -f docker-compose-prod.yml up -d --no-deps --build vehicle-service

# Scaling horizontal
docker-compose -f docker-compose-prod.yml up -d --scale vehicle-service=3
```

### Maintenance

```bash
# Backup manuel
./scripts/backup.sh

# Restauration
./scripts/restore.sh gdldv_backup_20250120_120000.tar.gz

# Mise à jour des configurations
docker-compose -f docker-compose-prod.yml restart config-server
```

---

## Support et Documentation

### Ressources
- **Documentation Spring Cloud**: https://spring.io/projects/spring-cloud
- **Docker Documentation**: https://docs.docker.com
- **Docker Compose**: https://docs.docker.com/compose

### Contacts
- **Email**: admin@gdldv.com
- **Repository**: https://github.com/Projet77/gdldv-microservices

---

## Changelog

### Version 1.0.0 (2025-01-20)
- ✅ Configuration Docker initiale
- ✅ 7 services microservices
- ✅ 4 bases de données MySQL
- ✅ Scripts de déploiement Windows/Linux
- ✅ Documentation complète

---

**Auteur**: GDLDV Team
**Date**: Janvier 2025
**Version**: 1.0.0
