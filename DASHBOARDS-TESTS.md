# 🧪 TESTS DES DASHBOARDS - GUIDE COMPLET

**Date**: 24 Décembre 2025
**Service**: user-service (port 8003)

---

## 🎯 PRÉ-REQUIS

### 1. Services démarrés

```bash
# Démarrer config-server (port 8888)
cd config-server
mvn spring-boot:run

# Démarrer eureka-server (port 8761)
cd eureka-server
mvn spring-boot:run

# Démarrer user-service (port 8003)
cd user-service
mvn spring-boot:run
```

### 2. Obtenir un JWT Token

#### Option A: Via endpoint de login
```bash
curl -X POST http://localhost:8003/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "client@example.com",
    "password": "password123"
  }'
```

**Réponse:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "email": "client@example.com",
    "role": "CLIENT"
  }
}
```

Copier le `token` pour les requêtes suivantes.

#### Option B: Via Swagger UI
1. Ouvrir http://localhost:8003/swagger-ui.html
2. Aller dans "Auth Controller"
3. POST /api/auth/login
4. Copier le token de la réponse

---

## 📊 TESTS CLIENT DASHBOARD

### Variable d'environnement (remplacer par votre token)
```bash
export JWT_TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
export USER_ID=1
```

### 1. Dashboard complet
```bash
curl -X GET "http://localhost:8003/api/client/dashboard?userId=${USER_ID}" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -H "Accept: application/json" | jq
```

**Attendu:**
```json
{
  "userId": 1,
  "firstName": "Jean",
  "lastName": "Dupont",
  "membershipBadge": "VIP",
  "totalRentals": 25,
  "totalSpent": 1254000.0,
  "currentRentals": [...],
  "recentHistory": [...],
  "favorites": [...]
}
```

### 2. Réservations actives
```bash
curl -X GET "http://localhost:8003/api/client/active-rentals?userId=${USER_ID}" \
  -H "Authorization: Bearer ${JWT_TOKEN}" | jq
```

### 3. Historique des locations
```bash
curl -X GET "http://localhost:8003/api/client/rental-history?userId=${USER_ID}" \
  -H "Authorization: Bearer ${JWT_TOKEN}" | jq
```

### 4. Véhicules favoris
```bash
curl -X GET "http://localhost:8003/api/client/favorites?userId=${USER_ID}" \
  -H "Authorization: Bearer ${JWT_TOKEN}" | jq
```

### 5. Statistiques personnelles
```bash
curl -X GET "http://localhost:8003/api/client/statistics?userId=${USER_ID}" \
  -H "Authorization: Bearer ${JWT_TOKEN}" | jq
```

**Attendu:**
```json
{
  "totalRentals": 25,
  "totalSpent": 1254000.0,
  "averageSpentPerRental": 50160.0,
  "averageDuration": 4.2,
  "favoriteCategory": "SUV",
  "membershipBadge": "VIP"
}
```

---

## 🚗 TESTS AGENT DASHBOARD

### Variable d'environnement
```bash
export AGENT_JWT_TOKEN="..." # Token d'un utilisateur AGENT
export AGENT_ID=2
```

### 1. Dashboard complet
```bash
curl -X GET "http://localhost:8003/api/agent/dashboard?agentId=${AGENT_ID}" \
  -H "Authorization: Bearer ${AGENT_JWT_TOKEN}" | jq
```

**Attendu:**
```json
{
  "agentId": 2,
  "agentName": "Agent #2",
  "todayCheckOuts": 5,
  "todayCheckIns": 3,
  "pendingCheckOuts": 8,
  "pendingCheckIns": 4,
  "todayRevenue": 150000.0,
  "pendingCheckOutQueue": [...],
  "alerts": [...]
}
```

### 2. File d'attente check-out
```bash
curl -X GET "http://localhost:8003/api/agent/pending-checkouts?agentId=${AGENT_ID}" \
  -H "Authorization: Bearer ${AGENT_JWT_TOKEN}" | jq
```

### 3. File d'attente check-in
```bash
curl -X GET "http://localhost:8003/api/agent/pending-checkins?agentId=${AGENT_ID}" \
  -H "Authorization: Bearer ${AGENT_JWT_TOKEN}" | jq
```

### 4. Alertes
```bash
curl -X GET "http://localhost:8003/api/agent/alerts?agentId=${AGENT_ID}" \
  -H "Authorization: Bearer ${AGENT_JWT_TOKEN}" | jq
```

**Attendu:**
```json
[
  {
    "type": "LATE_RETURN",
    "message": "Retard de restitution: CONF-ABC123",
    "reservationId": 15,
    "priority": "HIGH",
    "createdAt": "2025-12-24T14:30:00"
  }
]
```

### 5. Réservations du jour
```bash
curl -X GET "http://localhost:8003/api/agent/today-reservations?agentId=${AGENT_ID}" \
  -H "Authorization: Bearer ${AGENT_JWT_TOKEN}" | jq
```

### 6. Transactions complétées
```bash
curl -X GET "http://localhost:8003/api/agent/completed-today?agentId=${AGENT_ID}" \
  -H "Authorization: Bearer ${AGENT_JWT_TOKEN}" | jq
```

---

## 📈 TESTS MANAGER DASHBOARD

### Variable d'environnement
```bash
export MANAGER_JWT_TOKEN="..." # Token d'un utilisateur MANAGER
```

### 1. Dashboard complet
```bash
curl -X GET "http://localhost:8003/api/manager/dashboard" \
  -H "Authorization: Bearer ${MANAGER_JWT_TOKEN}" | jq
```

**Attendu:**
```json
{
  "todayRevenue": 450000.0,
  "todayRevenueTarget": 500000.0,
  "todayRentals": 12,
  "fleetUtilizationRate": 64.0,
  "customerSatisfaction": 4.6,
  "revenueVsYesterday": 12.5,
  "staffPerformance": [...],
  "criticalIncidents": [...],
  "revenueTrend": [...],
  "topVehicles": [...],
  "topClients": [...]
}
```

### 2. KPIs du jour
```bash
curl -X GET "http://localhost:8003/api/manager/kpis" \
  -H "Authorization: Bearer ${MANAGER_JWT_TOKEN}" | jq
```

**Attendu:**
```json
{
  "todayRevenue": 450000.0,
  "todayRevenueTarget": 500000.0,
  "todayRentals": 12,
  "todayRentalsTarget": 15,
  "fleetUtilizationRate": 64.0,
  "fleetUtilizationTarget": 70.0,
  "customerSatisfaction": 4.6,
  "customerSatisfactionTarget": 4.5,
  "lateReturnRate": 2.5,
  "lateReturnTarget": 2.0
}
```

### 3. Performance de l'équipe
```bash
curl -X GET "http://localhost:8003/api/manager/staff-performance" \
  -H "Authorization: Bearer ${MANAGER_JWT_TOKEN}" | jq
```

### 4. Incidents critiques
```bash
curl -X GET "http://localhost:8003/api/manager/critical-incidents" \
  -H "Authorization: Bearer ${MANAGER_JWT_TOKEN}" | jq
```

### 5. Tendances (30 jours)
```bash
curl -X GET "http://localhost:8003/api/manager/trends" \
  -H "Authorization: Bearer ${MANAGER_JWT_TOKEN}" | jq
```

**Attendu:**
```json
{
  "revenueTrend": [
    { "date": "2025-11-24T00:00:00", "value": 420000.0 },
    { "date": "2025-11-25T00:00:00", "value": 455000.0 },
    ...
  ],
  "rentalsTrend": [...],
  "satisfactionTrend": [...]
}
```

### 6. Top performers
```bash
curl -X GET "http://localhost:8003/api/manager/top-performers" \
  -H "Authorization: Bearer ${MANAGER_JWT_TOKEN}" | jq
```

**Attendu:**
```json
{
  "topVehicles": [
    {
      "vehicleId": 5,
      "name": "Toyota Camry",
      "rentalsCount": 25,
      "revenue": 625000.0,
      "averageRating": 4.8
    }
  ],
  "topClients": [
    {
      "clientId": 12,
      "name": "Client #12",
      "rentalsCount": 15,
      "totalSpent": 750000.0,
      "averageRating": 4.9
    }
  ]
}
```

### 7. État de la flotte
```bash
curl -X GET "http://localhost:8003/api/manager/fleet-status" \
  -H "Authorization: Bearer ${MANAGER_JWT_TOKEN}" | jq
```

**Attendu:**
```json
{
  "totalVehicles": 50,
  "availableVehicles": 32,
  "rentedVehicles": 12,
  "maintenanceVehicles": 4,
  "outOfServiceVehicles": 2
}
```

---

## 🛡️ TESTS SUPER_ADMIN DASHBOARD

### Variable d'environnement
```bash
export SUPERADMIN_JWT_TOKEN="..." # Token d'un utilisateur SUPER_ADMIN
```

### 1. Dashboard complet
```bash
curl -X GET "http://localhost:8003/api/super-admin/dashboard" \
  -H "Authorization: Bearer ${SUPERADMIN_JWT_TOKEN}" | jq
```

**Attendu:**
```json
{
  "systemHealth": {...},
  "userStatistics": {...},
  "databaseStatistics": {...},
  "auditStatistics": {...},
  "systemConfiguration": {...},
  "securityMetrics": {...},
  "performanceMetrics": {...}
}
```

### 2. Santé du système
```bash
curl -X GET "http://localhost:8003/api/super-admin/system-health" \
  -H "Authorization: Bearer ${SUPERADMIN_JWT_TOKEN}" | jq
```

**Attendu:**
```json
{
  "services": {
    "user-service": {
      "name": "User Service",
      "status": "UP",
      "port": 8003,
      "uptime": 99.98,
      "lastCheck": "2025-12-24T14:35:00"
    },
    "vehicle-service": {...},
    "eureka-server": {...}
  },
  "overallUptime": 99.98,
  "daysRunning": 3,
  "currentAlerts": 0
}
```

### 3. Statistiques utilisateurs
```bash
curl -X GET "http://localhost:8003/api/super-admin/user-statistics" \
  -H "Authorization: Bearer ${SUPERADMIN_JWT_TOKEN}" | jq
```

**Attendu:**
```json
{
  "totalUsers": 524,
  "usersByRole": {
    "CLIENT": 500,
    "AGENT": 12,
    "MANAGER": 2,
    "ADMIN": 5,
    "SUPER_ADMIN": 2
  },
  "activeUsers30Days": 287,
  "inactiveUsers": 237,
  "newUsersThisMonth": 35,
  "activityRate": 54.8
}
```

### 4. Statistiques base de données
```bash
curl -X GET "http://localhost:8003/api/super-admin/database-statistics" \
  -H "Authorization: Bearer ${SUPERADMIN_JWT_TOKEN}" | jq
```

### 5. Statistiques d'audit
```bash
curl -X GET "http://localhost:8003/api/super-admin/audit-statistics" \
  -H "Authorization: Bearer ${SUPERADMIN_JWT_TOKEN}" | jq
```

### 6. Configuration système
```bash
curl -X GET "http://localhost:8003/api/super-admin/system-configuration" \
  -H "Authorization: Bearer ${SUPERADMIN_JWT_TOKEN}" | jq
```

**Attendu:**
```json
{
  "applicationName": "GDLDV",
  "version": "1.0.0",
  "environment": "PRODUCTION",
  "timezone": "Africa/Dakar",
  "paymentConfig": {
    "provider": "STRIPE",
    "currency": "XOF",
    "active": true,
    "webhooksActive": true
  },
  "securityConfig": {
    "sslEnabled": true,
    "jwtConfigured": true,
    "jwtExpirationHours": 24,
    "rateLimitingEnabled": true
  }
}
```

### 7. Métriques de sécurité
```bash
curl -X GET "http://localhost:8003/api/super-admin/security-metrics" \
  -H "Authorization: Bearer ${SUPERADMIN_JWT_TOKEN}" | jq
```

### 8. Métriques de performance
```bash
curl -X GET "http://localhost:8003/api/super-admin/performance-metrics" \
  -H "Authorization: Bearer ${SUPERADMIN_JWT_TOKEN}" | jq
```

**Attendu:**
```json
{
  "averageResponseTime": 150.0,
  "requestsPerMinute": 45,
  "cpuUsage": 35.5,
  "memoryUsage": 62.3,
  "activeConnections": 12
}
```

---

## 🔐 TESTS DE SÉCURITÉ

### Test 1: Accès sans token (doit échouer)
```bash
curl -X GET "http://localhost:8003/api/client/dashboard?userId=1" \
  -H "Accept: application/json"
```

**Attendu: 401 Unauthorized**

### Test 2: CLIENT essaie d'accéder au dashboard MANAGER (doit échouer)
```bash
curl -X GET "http://localhost:8003/api/manager/dashboard" \
  -H "Authorization: Bearer ${JWT_TOKEN}" # Token CLIENT
```

**Attendu: 403 Forbidden**

### Test 3: AGENT essaie d'accéder au dashboard SUPER_ADMIN (doit échouer)
```bash
curl -X GET "http://localhost:8003/api/super-admin/dashboard" \
  -H "Authorization: Bearer ${AGENT_JWT_TOKEN}" # Token AGENT
```

**Attendu: 403 Forbidden**

---

## 📱 TESTS VIA POSTMAN

### Import de la collection

Créer une nouvelle collection Postman avec ces endpoints:

```
GDLDV Dashboards
├── Client Dashboard
│   ├── GET Dashboard complet
│   ├── GET Réservations actives
│   ├── GET Historique
│   ├── GET Favoris
│   └── GET Statistiques
├── Agent Dashboard
│   ├── GET Dashboard complet
│   ├── GET Pending Checkouts
│   ├── GET Pending Checkins
│   ├── GET Alertes
│   ├── GET Réservations du jour
│   └── GET Transactions complétées
├── Manager Dashboard
│   ├── GET Dashboard complet
│   ├── GET KPIs
│   ├── GET Performance équipe
│   ├── GET Incidents
│   ├── GET Tendances
│   ├── GET Top performers
│   └── GET État flotte
└── Super Admin Dashboard
    ├── GET Dashboard complet
    ├── GET Santé système
    ├── GET Stats utilisateurs
    ├── GET Stats BDD
    ├── GET Audit
    ├── GET Configuration
    ├── GET Sécurité
    └── GET Performance
```

### Variables d'environnement Postman

```
base_url: http://localhost:8003
jwt_token_client: {{votre_token_client}}
jwt_token_agent: {{votre_token_agent}}
jwt_token_manager: {{votre_token_manager}}
jwt_token_superadmin: {{votre_token_superadmin}}
user_id: 1
agent_id: 2
```

---

## 🐛 DÉPANNAGE

### Erreur 401 Unauthorized
- Vérifier que le JWT token est valide
- Vérifier que le token n'est pas expiré
- Vérifier le header Authorization

### Erreur 403 Forbidden
- Vérifier que l'utilisateur a le bon rôle
- Exemple: seuls MANAGER et ADMIN peuvent accéder à /api/manager/*

### Erreur 404 Not Found
- Vérifier que user-service est démarré sur le port 8003
- Vérifier l'URL (pas de faute de frappe)

### Données vides []
- Normal si la base de données est vide
- Créer des données de test via les endpoints de création

### Erreur 500 Internal Server Error
- Vérifier les logs du service: `tail -f user-service/logs/application.log`
- Vérifier la connexion à la base de données

---

## 📊 SWAGGER UI

Alternative plus visuelle pour tester:

```
http://localhost:8003/swagger-ui.html
```

1. Cliquer sur "Authorize" (en haut à droite)
2. Entrer: `Bearer {votre_token}`
3. Cliquer sur "Authorize"
4. Tester les endpoints directement dans l'interface

---

## ✅ CHECKLIST DE TESTS

### CLIENT Dashboard
- [ ] Dashboard complet retourne les bonnes données
- [ ] Réservations actives sont filtrées par userId
- [ ] Historique est trié par date décroissante
- [ ] Statistiques sont calculées correctement
- [ ] Badge membership est correct (Nouveau/Régulier/VIP)

### AGENT Dashboard
- [ ] Dashboard retourne les tâches du jour
- [ ] Alertes de retard sont détectées
- [ ] Files d'attente sont correctes
- [ ] Transactions complétées sont listées

### MANAGER Dashboard
- [ ] KPIs sont calculés correctement
- [ ] Tendances couvrent 30 jours
- [ ] Top performers sont triés
- [ ] Comparaisons vs hier/mois précédent

### SUPER_ADMIN Dashboard
- [ ] Santé système affiche tous les services
- [ ] Statistiques utilisateurs par rôle
- [ ] Métriques de sécurité
- [ ] Configuration système complète

### Sécurité
- [ ] Pas d'accès sans token
- [ ] Rôles respectés
- [ ] Pas d'accès inter-rôles non autorisé

---

**Tous les dashboards sont prêts à être testés ! 🎉**
