# ⚙️ GUIDE COMPLET - application.properties POUR LES MICROSERVICES

## 📌 IMPORTANT: application.yml vs application.properties

**Tu dois CHOISIR l'un ou l'autre (pas les deux!):**

| Format | Extension | Exemple |
|--------|-----------|---------|
| **YAML** | `.yml` | `spring.datasource.url: jdbc:mysql://...` |
| **Properties** | `.properties` | `spring.datasource.url=jdbc:mysql://...` |

**Recommandation:** Utilise **application.properties** (plus simple)

---

## 🚗 1. VEHICLE SERVICE

### Fichier: `vehicle-service/src/main/resources/application.properties`

**Efface tout et mets ceci:**

```properties
# ===== CONFIGURATION APPLICATION =====
spring.application.name=vehicle-service
server.port=8001
server.servlet.context-path=/vehicle-service

# ===== DATABASE CONFIGURATION (MySQL WAMP) =====
spring.datasource.url=jdbc:mysql://localhost:3306/gdldv_vehicle_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# ===== JPA / HIBERNATE CONFIGURATION =====
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false

# ===== THYMELEAF CONFIGURATION =====
spring.thymeleaf.prefix=classpath:/templates/vehicle/
spring.thymeleaf.suffix=.html
spring.thymeleaf.cache=false

# ===== ACTUATOR (Health Checks) =====
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
management.health.defaults.enabled=true

# ===== SPRINGDOC OPENAPI (SWAGGER) =====
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true

# ===== LOGGING =====
logging.level.root=INFO
logging.level.com.gdldv=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
logging.level.org.springframework.web=DEBUG

# ===== SPRING BOOT DEVTOOLS =====
spring.devtools.restart.enabled=true
spring.devtools.livereload.enabled=true
```

---

## 📊 2. RESERVATION SERVICE

### Fichier: `reservation-service/src/main/resources/application.properties`

```properties
# ===== CONFIGURATION APPLICATION =====
spring.application.name=reservation-service
server.port=8002
server.servlet.context-path=/reservation-service

# ===== DATABASE CONFIGURATION (MySQL WAMP) =====
spring.datasource.url=jdbc:mysql://localhost:3306/gdldv_reservation_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# ===== JPA / HIBERNATE CONFIGURATION =====
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false

# ===== THYMELEAF CONFIGURATION =====
spring.thymeleaf.prefix=classpath:/templates/reservation/
spring.thymeleaf.suffix=.html
spring.thymeleaf.cache=false

# ===== ACTUATOR (Health Checks) =====
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
management.health.defaults.enabled=true

# ===== SPRINGDOC OPENAPI (SWAGGER) =====
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true

# ===== FEIGN CLIENT (appeler Vehicle Service) =====
feign.client.config.default.connectTimeout=5000
feign.client.config.default.readTimeout=5000
feign.client.config.vehicle-service.url=http://localhost:8001

# ===== LOGGING =====
logging.level.root=INFO
logging.level.com.gdldv=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.cloud.openfeign=DEBUG

# ===== SPRING BOOT DEVTOOLS =====
spring.devtools.restart.enabled=true
spring.devtools.livereload.enabled=true
```

---

## 👤 3. USER SERVICE

### Fichier: `user-service/src/main/resources/application.properties`

```properties
# ===== CONFIGURATION APPLICATION =====
spring.application.name=user-service
server.port=8003
server.servlet.context-path=/user-service

# ===== DATABASE CONFIGURATION (MySQL WAMP) =====
spring.datasource.url=jdbc:mysql://localhost:3306/gdldv_user_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# ===== JPA / HIBERNATE CONFIGURATION =====
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false

# ===== THYMELEAF CONFIGURATION =====
spring.thymeleaf.prefix=classpath:/templates/user/
spring.thymeleaf.suffix=.html
spring.thymeleaf.cache=false

# ===== ACTUATOR (Health Checks) =====
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
management.health.defaults.enabled=true

# ===== SPRINGDOC OPENAPI (SWAGGER) =====
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true

# ===== SECURITY / JWT CONFIGURATION =====
# (Tu configureras ça plus tard dans le code)
app.jwtSecret=your-secret-key-change-me-in-production
app.jwtExpirationMs=86400000

# ===== LOGGING =====
logging.level.root=INFO
logging.level.com.gdldv=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.security=DEBUG

# ===== SPRING BOOT DEVTOOLS =====
spring.devtools.restart.enabled=true
spring.devtools.livereload.enabled=true
```

---

## 🚪 4. API GATEWAY

### Fichier: `api-gateway/src/main/resources/application.properties`

```properties
# ===== CONFIGURATION APPLICATION =====
spring.application.name=api-gateway
server.port=8000

# ===== SPRING CLOUD GATEWAY ROUTES =====

# Vehicle Service Route
spring.cloud.gateway.routes[0].id=vehicle-service
spring.cloud.gateway.routes[0].uri=http://localhost:8001
spring.cloud.gateway.routes[0].predicates[0]=Path=/api/vehicles/**
spring.cloud.gateway.routes[0].filters[0]=StripPrefix=1

# Reservation Service Route
spring.cloud.gateway.routes[1].id=reservation-service
spring.cloud.gateway.routes[1].uri=http://localhost:8002
spring.cloud.gateway.routes[1].predicates[0]=Path=/api/reservations/**
spring.cloud.gateway.routes[1].filters[0]=StripPrefix=1

# User Service Route
spring.cloud.gateway.routes[2].id=user-service
spring.cloud.gateway.routes[2].uri=http://localhost:8003
spring.cloud.gateway.routes[2].predicates[0]=Path=/api/users/**,/api/auth/**
spring.cloud.gateway.routes[2].filters[0]=StripPrefix=1

# ===== ACTUATOR (Health Checks) =====
management.endpoints.web.exposure.include=health,info,routes,gateway
management.endpoint.health.show-details=always

# ===== SPRINGDOC OPENAPI (SWAGGER) =====
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true

# ===== LOGGING =====
logging.level.root=INFO
logging.level.com.gdldv=DEBUG
logging.level.org.springframework.cloud.gateway=DEBUG
logging.level.org.springframework.web=DEBUG
```

---

## 📋 EXPLICATION DES PROPRIÉTÉS

### 🔷 Configuration Application

```properties
# Nom du service (apparaît dans les logs)
spring.application.name=vehicle-service

# Port sur lequel le service écoute
server.port=8001

# Context path de l'application
server.servlet.context-path=/vehicle-service
# → Les URLs seront: http://localhost:8001/vehicle-service/...
```

### 🔷 Configuration Database (MySQL)

```properties
# URL de connexion MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/gdldv_vehicle_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
#                                                      ↑ Port MySQL (3306 par défaut)
#                                                                  ↑ Nom de la base

# Identifiants
spring.datasource.username=root
spring.datasource.password=      # Vide par défaut dans WAMP

# Driver JDBC
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### 🔷 Configuration JPA/Hibernate

```properties
# Stratégie pour créer les tables
spring.jpa.hibernate.ddl-auto=update
# Valeurs possibles:
# - create: crée les tables, supprime à chaque redémarrage
# - create-drop: crée, puis supprime à l'arrêt
# - update: crée les tables si absent, modifie sinon (👈 RECOMMANDÉ)
# - validate: valide seulement, ne crée pas

# Type de base de données
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Afficher les requêtes SQL dans les logs
spring.jpa.show-sql=true

# Formatter les requêtes SQL (plus lisible)
spring.jpa.properties.hibernate.format_sql=true

# Désactiver OpenInView (peut causer des problèmes)
spring.jpa.open-in-view=false
```

### 🔷 Configuration Thymeleaf

```properties
# Dossier des templates
spring.thymeleaf.prefix=classpath:/templates/vehicle/
# → Les fichiers HTML sont dans: src/main/resources/templates/vehicle/

# Extension des fichiers
spring.thymeleaf.suffix=.html
# → Les fichiers doivent finir par .html

# Cache les templates (désactiver en dev pour recharger automatiquement)
spring.thymeleaf.cache=false
```

### 🔷 Configuration Actuator

```properties
# Endpoints exposés (pour monitoring)
management.endpoints.web.exposure.include=health,info,metrics
# - health: état du service (/actuator/health)
# - info: informations du service (/actuator/info)
# - metrics: métriques de performance (/actuator/metrics)

# Montrer les détails du health check
management.endpoint.health.show-details=always
```

### 🔷 Configuration Swagger

```properties
# Chemin de la doc API en JSON
springdoc.api-docs.path=/v3/api-docs

# Chemin de l'interface Swagger UI
springdoc.swagger-ui.path=/swagger-ui.html

# Activer Swagger UI
springdoc.swagger-ui.enabled=true
```

### 🔷 Configuration Logging

```properties
# Niveau de log racine
logging.level.root=INFO

# Niveau pour le code de l'application
logging.level.com.gdldv=DEBUG

# Niveau pour les requêtes SQL Hibernate
logging.level.org.hibernate.SQL=DEBUG

# Niveau pour les paramètres SQL
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Niveau pour les requêtes Web
logging.level.org.springframework.web=DEBUG
```

### 🔷 Configuration DevTools

```properties
# Redémarrage automatique quand un fichier change
spring.devtools.restart.enabled=true

# Live reload (rafraîchir automatiquement le navigateur)
spring.devtools.livereload.enabled=true
```

### 🔷 Configuration Feign (Reservation Service)

```properties
# Timeout de connexion (5 secondes)
feign.client.config.default.connectTimeout=5000

# Timeout de lecture (5 secondes)
feign.client.config.default.readTimeout=5000

# URL du Vehicle Service (pour appeler depuis Reservation)
feign.client.config.vehicle-service.url=http://localhost:8001
```

### 🔷 Configuration JWT (User Service)

```properties
# Secret pour signer les tokens JWT
app.jwtSecret=your-secret-key-change-me-in-production

# Durée de validité du token (en millisecondes)
# 86400000 = 24 heures
app.jwtExpirationMs=86400000
```

### 🔷 Configuration Spring Cloud Gateway

```properties
# Route 1: Vehicle Service
spring.cloud.gateway.routes[0].id=vehicle-service
spring.cloud.gateway.routes[0].uri=http://localhost:8001
spring.cloud.gateway.routes[0].predicates[0]=Path=/api/vehicles/**
spring.cloud.gateway.routes[0].filters[0]=StripPrefix=1
# Exemple:
# Requête: http://localhost:8000/api/vehicles/1
# Devient: http://localhost:8001/vehicles/1

# Route 2: Reservation Service
spring.cloud.gateway.routes[1].id=reservation-service
spring.cloud.gateway.routes[1].uri=http://localhost:8002
spring.cloud.gateway.routes[1].predicates[0]=Path=/api/reservations/**
spring.cloud.gateway.routes[1].filters[0]=StripPrefix=1

# Route 3: User Service
spring.cloud.gateway.routes[2].id=user-service
spring.cloud.gateway.routes[2].uri=http://localhost:8003
spring.cloud.gateway.routes[2].predicates[0]=Path=/api/users/**,/api/auth/**
spring.cloud.gateway.routes[2].filters[0]=StripPrefix=1
```

---

## ✅ CHECKLIST

### Avant de lancer les services, vérifie:

#### Vehicle Service (`application.properties`)
- [ ] `spring.application.name=vehicle-service`
- [ ] `server.port=8001`
- [ ] `spring.datasource.url=...gdldv_vehicle_db...`
- [ ] `spring.datasource.username=root`
- [ ] `spring.datasource.password=` (vide)
- [ ] `spring.jpa.hibernate.ddl-auto=update`

#### Reservation Service (`application.properties`)
- [ ] `spring.application.name=reservation-service`
- [ ] `server.port=8002`
- [ ] `spring.datasource.url=...gdldv_reservation_db...`
- [ ] Feign config pour appeler Vehicle Service

#### User Service (`application.properties`)
- [ ] `spring.application.name=user-service`
- [ ] `server.port=8003`
- [ ] `spring.datasource.url=...gdldv_user_db...`
- [ ] JWT secret configuré

#### API Gateway (`application.properties`)
- [ ] `spring.application.name=api-gateway`
- [ ] `server.port=8000`
- [ ] Routes configurées vers les 3 services

---

## 🚀 APRÈS AVOIR CRÉÉ application.properties

### 1. Rafraîchir IntelliJ

```
Right-click sur le dossier resources
→ Maven
→ Reload project
```

### 2. Lancer le service

```
Run → Edit Configurations
Sélectionne le service
Clique Run ▶️
```

### 3. Vérifier dans les logs

```
Tu dois voir:
✅ Tomcat started on port(s): 8001
✅ Started VehicleServiceApplication in X.XXX seconds
```

---

## 🆚 Comparaison: application.properties vs application.yml

### application.properties
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gdldv_vehicle_db
spring.datasource.username=root
spring.jpa.hibernate.ddl-auto=update
```

### application.yml (équivalent)
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/gdldv_vehicle_db
    username: root
  jpa:
    hibernate:
      ddl-auto: update
```

**Les deux fonctionnent! Choisis l'une ou l'autre (pas les deux).**

---

## ⚠️ PROBLÈMES COURANTS

### Problème 1: "Cannot connect to database"
**Cause:** WAMP n'est pas lancé

**Solution:**
1. Ouvre WAMP Server
2. Vérifie que MySQL est vert 🟢
3. Redémarre le service Spring

### Problème 2: "Unknown database 'gdldv_vehicle_db'"
**Cause:** La base n'existe pas

**Solution:**
1. Ouvre phpMyAdmin: http://localhost/phpmyadmin
2. Crée la base: gdldv_vehicle_db

### Problème 3: "Access denied for user 'root'@'localhost'"
**Cause:** Mauvais password

**Solution:**
Dans application.properties:
```properties
spring.datasource.password=
# Laisse vide (c'est le default dans WAMP)
```

### Problème 4: Les tables ne se créent pas
**Cause:** ddl-auto n'est pas "update"

**Solution:**
```properties
spring.jpa.hibernate.ddl-auto=update
# Asure-toi que c'est bien "update" et pas "validate"
```

### Problème 5: "Port 8001 is already in use"
**Cause:** Un autre service utilise ce port

**Solution:**
1. Tue le process: `netstat -ano | findstr :8001`
2. Ou change le port: `server.port=8001` → `server.port=8011`

---

## 📝 RÉSUMÉ

### Tu dois créer 4 fichiers `application.properties`:

| Service | Fichier | Port | Base MySQL |
|---------|---------|------|------------|
| Vehicle | `vehicle-service/src/main/resources/application.properties` | 8001 | gdldv_vehicle_db |
| Reservation | `reservation-service/src/main/resources/application.properties` | 8002 | gdldv_reservation_db |
| User | `user-service/src/main/resources/application.properties` | 8003 | gdldv_user_db |
| Gateway | `api-gateway/src/main/resources/application.properties` | 8000 | (Pas de BD) |

**Copie/colle le contenu approprié pour chaque service et tu es bon!** ✅

---

**Besoin d'aide? Demande-moi!** 💪
