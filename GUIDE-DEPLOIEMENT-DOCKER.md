# 🐳 Guide de Déploiement Docker - GDLDV

## 📋 Table des matières
1. [Prérequis](#prérequis)
2. [Architecture Docker](#architecture-docker)
3. [Configuration](#configuration)
4. [Déploiement complet](#déploiement-complet)
5. [Gestion des services](#gestion-des-services)
6. [Vérification et tests](#vérification-et-tests)
7. [Dépannage](#dépannage)
8. [Production](#production)

---

## 🎯 Prérequis

### Logiciels requis

- **Docker Desktop** (version 20.10+)
  - Windows: https://www.docker.com/products/docker-desktop
  - Configurer au moins **8 GB de RAM** pour Docker
  - Activer WSL 2 (recommandé pour Windows)

- **Git** (pour cloner le projet)
- **Un éditeur de texte** (pour modifier .env si nécessaire)

### Vérification de l'installation

```bash
# Vérifier Docker
docker --version
docker-compose --version

# Vérifier que Docker Desktop est démarré
docker ps
```

---

## 🏗️ Architecture Docker

### Services déployés

Le système GDLDV comprend **13 conteneurs** :

#### **Bases de données (4)**
- `mysql-vehicle` - Port 3307
- `mysql-reservation` - Port 3308
- `mysql-user` - Port 3309
- `mysql-rental` - Port 3310

#### **Cache (1)**
- `redis` - Port 6379

#### **Infrastructure (2)**
- `eureka-server` - Port 8761 (Service Discovery)
- `config-server` - Port 8888 (Configuration centralisée)

#### **Microservices Business (5)**
- `vehicle-service` - Port 8001
- `reservation-service` - Port 8002
- `user-service` - Port 8003
- `rental-service` - Port 8004
- `api-gateway` - Port 8000

#### **Frontend (1)**
- `frontend` - Port 3000 (React + Vite)

### Volumes persistants
- `gdldv-mysql-vehicle-data`
- `gdldv-mysql-reservation-data`
- `gdldv-mysql-user-data`
- `gdldv-mysql-rental-data`
- `gdldv-redis-data`

---

## ⚙️ Configuration

### 1. Fichier .env

Le fichier `.env` existe déjà à la racine du projet. Personnalisez-le selon vos besoins :

```bash
# Éditer le fichier .env
notepad .env
# ou
code .env
```

#### Variables importantes à configurer :

```env
# MySQL (garder les valeurs par défaut pour le développement)
MYSQL_ROOT_PASSWORD=root
MYSQL_USER=gdldv_user
MYSQL_PASSWORD=gdldv_password

# JWT (IMPORTANT: Changez en production !)
JWT_SECRET=your-secret-key-change-me-in-production-gdldv-2025-MUST-BE-AT-LEAST-256-BITS

# Stripe (optionnel pour le développement)
STRIPE_API_KEY=sk_test_votre_cle_ici
STRIPE_PUBLIC_KEY=pk_test_votre_cle_ici

# Email (optionnel, pour les notifications)
MAIL_USERNAME=votre-email@gmail.com
MAIL_PASSWORD=votre-app-password-gmail
```

### 2. Configuration spécifique

Aucune modification n'est nécessaire pour un déploiement de développement local.

---

## 🚀 Déploiement complet

### Option 1 : Démarrage automatique (Recommandé)

#### Windows

Double-cliquez sur le fichier :
```
start-all.bat
```

Ou en ligne de commande :
```bash
.\start-all.bat
```

#### Ligne de commande manuelle

```bash
# 1. Démarrer Docker Desktop (attendez qu'il soit prêt)

# 2. Lancer tous les services
docker-compose -f docker-compose-full.yml up -d

# 3. Voir les logs en temps réel
docker-compose -f docker-compose-full.yml logs -f
```

### Option 2 : Démarrage progressif

Pour mieux comprendre le processus ou en cas de problème :

```bash
# 1. Bases de données et Redis
docker-compose -f docker-compose-full.yml up -d mysql-vehicle mysql-reservation mysql-user mysql-rental redis

# Attendre 30 secondes que les bases soient prêtes
timeout 30

# 2. Services d'infrastructure
docker-compose -f docker-compose-full.yml up -d eureka-server config-server

# Attendre 60 secondes
timeout 60

# 3. Services métier
docker-compose -f docker-compose-full.yml up -d vehicle-service user-service reservation-service rental-service

# Attendre 60 secondes
timeout 60

# 4. API Gateway
docker-compose -f docker-compose-full.yml up -d api-gateway

# Attendre 30 secondes
timeout 30

# 5. Frontend
docker-compose -f docker-compose-full.yml up -d frontend
```

### Temps de démarrage estimé

- **Premier démarrage** (avec build): 10-15 minutes
- **Démarrages suivants**: 3-5 minutes

---

## 🎮 Gestion des services

### Commandes essentielles

```bash
# Voir l'état de tous les conteneurs
docker-compose -f docker-compose-full.yml ps

# Voir les logs de tous les services
docker-compose -f docker-compose-full.yml logs -f

# Voir les logs d'un service spécifique
docker-compose -f docker-compose-full.yml logs -f user-service

# Redémarrer un service
docker-compose -f docker-compose-full.yml restart user-service

# Arrêter tous les services
docker-compose -f docker-compose-full.yml stop

# Arrêter et supprimer les conteneurs (garder les données)
docker-compose -f docker-compose-full.yml down

# Tout supprimer (conteneurs + volumes - ATTENTION: perte de données!)
docker-compose -f docker-compose-full.yml down -v

# Reconstruire un service après modification du code
docker-compose -f docker-compose-full.yml up -d --build user-service
```

### Arrêter les services

```bash
# Windows - Script automatique
.\stop-all.bat

# Ou manuellement
docker-compose -f docker-compose-full.yml down
```

---

## ✅ Vérification et tests

### 1. Vérifier que tous les conteneurs sont démarrés

```bash
docker-compose -f docker-compose-full.yml ps
```

Vous devriez voir 13 conteneurs avec le statut `Up` ou `Up (healthy)`.

### 2. URLs à tester

| Service | URL | Description |
|---------|-----|-------------|
| **Frontend** | http://localhost:3000 | Interface utilisateur React |
| **API Gateway** | http://localhost:8000 | Point d'entrée principal |
| **Eureka Dashboard** | http://localhost:8761 | Vue d'ensemble des services |
| **Vehicle Service** | http://localhost:8001/swagger-ui.html | API véhicules |
| **Reservation Service** | http://localhost:8002/swagger-ui.html | API réservations |
| **User Service** | http://localhost:8003/user-service/login | Page de connexion |
| **Rental Service** | http://localhost:8004/swagger-ui.html | API locations |

### 3. Vérifier Eureka

Accédez à http://localhost:8761 et vérifiez que tous les services sont enregistrés :
- ✅ VEHICLE-SERVICE
- ✅ RESERVATION-SERVICE
- ✅ USER-SERVICE
- ✅ RENTAL-SERVICE
- ✅ API-GATEWAY

### 4. Tests fonctionnels

```bash
# Tester l'API Gateway
curl http://localhost:8000/actuator/health

# Tester le Vehicle Service via l'API Gateway
curl http://localhost:8000/api/vehicles

# Vérifier Redis
docker exec -it gdldv-redis redis-cli ping
# Devrait retourner: PONG
```

---

## 🔧 Dépannage

### Problème : Un service ne démarre pas

```bash
# 1. Voir les logs du service
docker-compose -f docker-compose-full.yml logs service-name

# 2. Vérifier les dépendances (bases de données)
docker-compose -f docker-compose-full.yml ps

# 3. Redémarrer le service
docker-compose -f docker-compose-full.yml restart service-name
```

### Problème : Erreur "port already in use"

```bash
# Trouver quel processus utilise le port
netstat -ano | findstr :8003

# Arrêter le processus ou changer le port dans docker-compose
```

### Problème : Erreur de mémoire / Build échoue

```bash
# Augmenter la mémoire allouée à Docker Desktop:
# Docker Desktop → Settings → Resources → Memory: 8 GB minimum

# Nettoyer Docker
docker system prune -a --volumes
```

### Problème : Base de données vide après redémarrage

Les données sont persistées dans les volumes Docker. Pour vérifier :

```bash
# Lister les volumes
docker volume ls | findstr gdldv

# Inspecter un volume
docker volume inspect gdldv-mysql-user-data
```

### Problème : Redis connection refused

```bash
# Vérifier que Redis est démarré
docker ps | findstr redis

# Redémarrer Redis
docker-compose -f docker-compose-full.yml restart redis

# Vérifier les logs
docker-compose -f docker-compose-full.yml logs redis
```

### Logs et diagnostics

```bash
# Logs de tous les services (dernières 100 lignes)
docker-compose -f docker-compose-full.yml logs --tail=100

# Statistiques de ressources
docker stats

# Inspecter un conteneur
docker inspect gdldv-user-service

# Accéder au shell d'un conteneur
docker exec -it gdldv-user-service sh
```

---

## 🏭 Production

### Checklist avant déploiement en production

- [ ] **Modifier le fichier .env** :
  ```env
  SPRING_PROFILES_ACTIVE=prod
  MYSQL_ROOT_PASSWORD=<mot-de-passe-fort-aleatoire>
  JWT_SECRET=<cle-secrete-256-bits-securisee>
  ```

- [ ] **Activer HTTPS** sur Nginx (frontend)
- [ ] **Configurer un reverse proxy** (Nginx/Traefik) devant l'API Gateway
- [ ] **Sauvegardes automatiques** des volumes MySQL
- [ ] **Monitoring** (Prometheus + Grafana)
- [ ] **Logs centralisés** (ELK Stack)
- [ ] **Configurer les limites de ressources** dans docker-compose
- [ ] **Secrets management** (Docker Secrets ou Vault)

### Exemple de configuration production

```yaml
# Dans docker-compose-full.yml, ajouter pour chaque service:
deploy:
  resources:
    limits:
      cpus: '1'
      memory: 1G
    reservations:
      cpus: '0.5'
      memory: 512M
  restart_policy:
    condition: on-failure
    max_attempts: 3
```

---

## 📊 Monitoring

### Healthchecks

Tous les services ont des healthchecks configurés. Pour voir leur statut :

```bash
docker-compose -f docker-compose-full.yml ps
```

### Vérifier les ressources utilisées

```bash
# Temps réel
docker stats

# Espace disque utilisé
docker system df
```

---

## 📝 Résumé des commandes

```bash
# Démarrage
docker-compose -f docker-compose-full.yml up -d

# Voir les logs
docker-compose -f docker-compose-full.yml logs -f

# État des services
docker-compose -f docker-compose-full.yml ps

# Redémarrer un service
docker-compose -f docker-compose-full.yml restart <service>

# Rebuild après modifications
docker-compose -f docker-compose-full.yml up -d --build <service>

# Arrêter
docker-compose -f docker-compose-full.yml down

# Nettoyer complètement
docker-compose -f docker-compose-full.yml down -v
docker system prune -a
```

---

## 🆘 Support

En cas de problème :

1. Consultez les logs : `docker-compose -f docker-compose-full.yml logs -f`
2. Vérifiez la section [Dépannage](#dépannage)
3. Vérifiez que Docker Desktop a suffisamment de ressources (8GB RAM minimum)
4. Redémarrez Docker Desktop si nécessaire

---

## ✅ Checklist de vérification finale

- [ ] Docker Desktop est démarré
- [ ] Fichier `.env` est configuré
- [ ] 13 conteneurs sont `Up (healthy)`
- [ ] http://localhost:8761 affiche tous les services
- [ ] http://localhost:3000 affiche le frontend
- [ ] http://localhost:8003/user-service/login affiche la page de connexion
- [ ] Redis répond : `docker exec -it gdldv-redis redis-cli ping`

---

**🎉 Félicitations ! Votre plateforme GDLDV est opérationnelle !**

Accédez à http://localhost:3000 pour commencer à utiliser l'application.
