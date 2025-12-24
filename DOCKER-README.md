# 🚀 Déploiement Docker - Guide Rapide

## Quick Start

### 1. Prérequis
- Docker Desktop installé et en cours d'exécution
- 8 GB RAM minimum disponible
- Ports 8000-8761 et 3307-3310 libres

### 2. Configuration (2 minutes)

```bash
# 1. Copier le fichier d'environnement
copy .env.example .env

# 2. Éditer .env et configurer au minimum:
# - MYSQL_ROOT_PASSWORD
# - MYSQL_PASSWORD
# - JWT_SECRET
```

### 3. Déploiement (5-10 minutes)

#### Windows
```bash
cd "C:\Users\Abdou\Documents\Projet stage\Gestion des locations"
scripts\deploy.bat
```

#### Linux/Mac
```bash
cd ~/Gestion-des-locations
chmod +x scripts/deploy.sh
./scripts/deploy.sh
```

### 4. Vérification

Ouvrir dans votre navigateur:
- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8000
- **Swagger UI Vehicle**: http://localhost:8001/swagger-ui.html

---

## Structure des Fichiers Docker

```
📦 Gestion des locations/
├── 🐳 docker-compose-full.yml     # Configuration principale
├── 🔧 .env.example                # Template de configuration
├── 📝 DOCKER-DEPLOYMENT-GUIDE.md  # Guide complet (LIRE EN PREMIER)
│
├── 📁 scripts/                    # Scripts utiles
│   ├── deploy.bat                 # Déploiement Windows
│   ├── deploy.sh                  # Déploiement Linux/Mac
│   ├── stop.bat                   # Arrêter les services
│   ├── restart.bat                # Redémarrer
│   └── logs.bat                   # Voir les logs
│
├── 📁 db-init/                    # Init bases de données
│   ├── vehicle-init.sql
│   ├── reservation-init.sql
│   ├── user-init.sql
│   └── rental-init.sql
│
└── 📁 [service]/Dockerfile        # Dockerfile de chaque service
```

---

## Commandes Essentielles

### Démarrage
```bash
# Tout démarrer
docker-compose -f docker-compose-full.yml up -d

# Avec rebuild
docker-compose -f docker-compose-full.yml up -d --build
```

### Arrêt
```bash
# Arrêter tout
docker-compose -f docker-compose-full.yml down

# Arrêter + supprimer volumes (⚠️ perte de données)
docker-compose -f docker-compose-full.yml down -v
```

### Monitoring
```bash
# État des services
docker-compose -f docker-compose-full.yml ps

# Logs en temps réel
docker-compose -f docker-compose-full.yml logs -f

# Logs d'un service
docker-compose -f docker-compose-full.yml logs -f vehicle-service
```

### Redémarrage
```bash
# Redémarrer un service
docker-compose -f docker-compose-full.yml restart vehicle-service

# Rebuild et redémarrer
docker-compose -f docker-compose-full.yml up -d --build vehicle-service
```

---

## Services et Ports

| Service | Port | URL |
|---------|------|-----|
| 🌐 API Gateway | 8000 | http://localhost:8000 |
| 🔍 Eureka Server | 8761 | http://localhost:8761 |
| ⚙️ Config Server | 8888 | http://localhost:8888 |
| 🚗 Vehicle Service | 8001 | http://localhost:8001 |
| 📅 Reservation Service | 8002 | http://localhost:8002 |
| 👤 User Service | 8003 | http://localhost:8003 |
| 📋 Rental Service | 8004 | http://localhost:8004 |
| 🗄️ MySQL Vehicle | 3307 | localhost:3307 |
| 🗄️ MySQL Reservation | 3308 | localhost:3308 |
| 🗄️ MySQL User | 3309 | localhost:3309 |
| 🗄️ MySQL Rental | 3310 | localhost:3310 |

---

## Ordre de Démarrage

Les services doivent démarrer dans cet ordre:

1. **Bases de données** (MySQL × 4) → ~10 secondes
2. **Eureka Server** → ~30 secondes
3. **Config Server** → ~20 secondes
4. **API Gateway** → ~20 secondes
5. **Services métier** (Vehicle, Reservation, User) → ~30 secondes
6. **Rental Service** → ~20 secondes

**Temps total**: ~2-3 minutes

---

## Troubleshooting Rapide

### Problème: Port déjà utilisé
```bash
# Windows
netstat -ano | findstr :8001
taskkill /PID <PID> /F

# Linux
sudo lsof -i :8001
sudo kill -9 <PID>
```

### Problème: Service ne démarre pas
```bash
# Voir les logs
docker-compose -f docker-compose-full.yml logs vehicle-service

# Restart
docker-compose -f docker-compose-full.yml restart vehicle-service

# Rebuild
docker-compose -f docker-compose-full.yml up -d --build vehicle-service
```

### Problème: Erreur de connexion DB
```bash
# Vérifier que la DB est up
docker-compose -f docker-compose-full.yml ps mysql-vehicle

# Redémarrer la DB
docker-compose -f docker-compose-full.yml restart mysql-vehicle
```

### Problème: Mémoire insuffisante
```
Docker Desktop → Settings → Resources → Memory: 8 GB
```

---

## Configuration Minimale .env

```env
# Obligatoire à configurer
MYSQL_ROOT_PASSWORD=votre-mot-de-passe-root
MYSQL_PASSWORD=votre-mot-de-passe-user
JWT_SECRET=votre-cle-secrete-256-bits-minimum

# Optionnel (dev)
STRIPE_API_KEY=sk_test_votre_cle
MAIL_USERNAME=votre-email@gmail.com
MAIL_PASSWORD=votre-app-password
```

---

## Endpoints Utiles

### Health Checks
- http://localhost:8000/actuator/health (Gateway)
- http://localhost:8001/actuator/health (Vehicle)
- http://localhost:8002/actuator/health (Reservation)
- http://localhost:8003/actuator/health (User)
- http://localhost:8004/actuator/health (Rental)

### Documentation API (Swagger)
- http://localhost:8001/swagger-ui.html (Vehicle)
- http://localhost:8002/swagger-ui.html (Reservation)
- http://localhost:8003/swagger-ui.html (User)
- http://localhost:8004/swagger-ui.html (Rental)

### Service Discovery
- http://localhost:8761 (Eureka Dashboard)

---

## Next Steps

1. ✅ Lire le guide complet: `DOCKER-DEPLOYMENT-GUIDE.md`
2. ✅ Configurer `.env` avec vos vraies valeurs
3. ✅ Tester les endpoints API via Swagger
4. ✅ Consulter la section Production du guide complet
5. ✅ Configurer le monitoring (optionnel)

---

## Support

- **Guide complet**: Voir `DOCKER-DEPLOYMENT-GUIDE.md`
- **Repository**: https://github.com/Projet77/gdldv-microservices
- **Issues**: https://github.com/Projet77/gdldv-microservices/issues

---

**Version**: 1.0.0
**Date**: Janvier 2025
**Auteur**: GDLDV Team
