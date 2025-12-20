# 🎯 CONFIG SERVER - IMPLÉMENTATION COMPLÈTE

## ✅ CE QUI A ÉTÉ CRÉÉ

### 1. Config Server (config-server/)
```
config-server/
├── pom.xml                          ✅ Créé
├── src/main/java/com/gdldv/configserver/
│   └── ConfigServerApplication.java ✅ Créé (@EnableConfigServer)
└── src/main/resources/
    └── application.properties       ✅ Créé (pointe vers config-repo)
```

### 2. Config Repository (config-repo/)
```
config-repo/
├── .git/                            ✅ Initialisé
├── README.md                        ✅ Créé
├── application.properties           ✅ Créé (commun)
├── application-dev.properties       ✅ Créé (dev)
├── user-service-dev.properties      ✅ Créé
├── vehicle-service-dev.properties   ✅ Créé
├── reservation-service-dev.properties ✅ Créé
├── rental-service-dev.properties    ✅ Créé
└── api-gateway-dev.properties       ✅ Créé
```

---

## 🚀 PROCHAINES ÉTAPES

### ÉTAPE 1: Démarrer le Config Server

```bash
cd config-server
mvn clean install
mvn spring-boot:run
```

Le Config Server démarre sur **http://localhost:8888**

### ÉTAPE 2: Tester le Config Server

Ouvrir dans un navigateur ou avec curl:

```bash
# 1. Config commune
curl http://localhost:8888/application/default | jq

# 2. Config dev globale
curl http://localhost:8888/application/dev | jq

# 3. Config user-service en dev
curl http://localhost:8888/user-service/dev | jq

# 4. Config vehicle-service en dev
curl http://localhost:8888/vehicle-service/dev | jq

# 5. Config api-gateway en dev
curl http://localhost:8888/api-gateway/dev | jq
```

**Résultat attendu:** Vous devriez voir les configurations en JSON avec toutes les properties fusionnées (application.properties + application-dev.properties + service-dev.properties).

---

## 📋 ÉTAPE 3: Modifier les Microservices

Pour chaque microservice (user-service, vehicle-service, etc.), suivre ces étapes:

### 3.1: Ajouter les dépendances au pom.xml

```xml
<!-- Ajouter AVANT </dependencies> -->

<!-- Spring Cloud Config Client -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>

<!-- Spring Cloud Bootstrap -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bootstrap</artifactId>
</dependency>
```

### 3.2: Créer bootstrap.properties

**Fichier: src/main/resources/bootstrap.properties** (nouveau fichier)

```properties
# ===== BOOTSTRAP CONFIGURATION =====
# Ce fichier se charge AVANT application.properties

# Nom de l'application (doit correspondre au fichier dans config-repo)
spring.application.name=user-service

# URL du Config Server
spring.cloud.config.uri=http://localhost:8888

# Profil actif (dev, prod, test)
spring.profiles.active=dev

# Fail fast si Config Server indisponible
spring.cloud.config.fail-fast=false

# Retry si Config Server indisponible
spring.cloud.config.retry.max-attempts=3
spring.cloud.config.retry.initial-interval=1000
```

### 3.3: Simplifier application.properties

**Garder UNIQUEMENT les overrides locaux si nécessaire:**

```properties
# ===== LOCAL OVERRIDES (optionnel) =====
# Ce fichier est maintenant presque vide
# Toute la config vient du Config Server

# Laisser vide ou ajouter des overrides spécifiques à cette instance
```

### 3.4: Ajouter @RefreshScope aux Controllers

**Exemple: UserController.java**

```java
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RestController
@RequestMapping("/api/users")
@RefreshScope  // ← IMPORTANT
public class UserController {
    // ...
}
```

---

## 🔄 ORDRE DE DÉMARRAGE

**IMPORTANT:** Démarrer dans cet ordre précis!

```
1. Eureka Server      (port 8761)  ← Discovery
2. Config Server      (port 8888)  ← Configurations
3. API Gateway        (port 8000)  ← Routes
4. User Service       (port 8003)
5. Vehicle Service    (port 8001)
6. Reservation Service (port 8002)
7. Rental Service     (port 8004)
```

---

## 🧪 TESTER LA CONFIGURATION DYNAMIQUE

### Test 1: Vérifier que le service reçoit la config

```bash
# Démarrer user-service
cd user-service
mvn spring-boot:run

# Dans les logs, chercher:
# "Fetching config from server at: http://localhost:8888"
# "Located property source: [applicationConfig...]"
```

### Test 2: Voir les propriétés chargées

```bash
curl http://localhost:8003/user-service/actuator/env | jq
```

### Test 3: Modifier une config sans redémarrage

```bash
# 1. Modifier config-repo/user-service-dev.properties
# Exemple: changer app.jwtExpirationMs

# 2. Commit dans config-repo
cd config-repo
git add .
git commit -m "Update JWT expiration"

# 3. Rafraîchir le microservice
curl -X POST http://localhost:8003/user-service/actuator/refresh

# ✅ La nouvelle config est appliquée SANS redémarrage!
```

---

## 📊 ARCHITECTURE FINALE

```
┌──────────────────────────────────┐
│   config-repo/ (Git)             │
│   Fichiers .properties           │
└────────────┬─────────────────────┘
             │
             ▼
    ┌────────────────────┐
    │  Config Server     │
    │  localhost:8888    │
    └────────┬───────────┘
             │
    ┌────────┼────────────────────┐
    │        │                    │
    ▼        ▼                    ▼
┌─────────┐ ┌─────────┐  ┌──────────┐
│User     │ │Vehicle  │  │API       │
│Service  │ │Service  │  │Gateway   │
│:8003    │ │:8001    │  │:8000     │
│         │ │         │  │          │
│bootstrap│ │bootstrap│  │bootstrap │
└─────────┘ └─────────┘  └──────────┘
```

---

## 🔧 COMMANDES UTILES

```bash
# Voir toutes les configs disponibles
curl http://localhost:8888/actuator/env | jq

# Chiffrer un secret
curl -X POST http://localhost:8888/encrypt -d "my-secret-password"

# Déchiffrer un secret
curl -X POST http://localhost:8888/decrypt -d "{cipher}xxxxxxxxx"

# Rafraîchir TOUS les services (nécessite Spring Cloud Bus)
curl -X POST http://localhost:8888/actuator/bus-refresh
```

---

## ✅ CHECKLIST

```
CONFIG SERVER:
  [✅] Config Server créé
  [✅] pom.xml configuré
  [✅] @EnableConfigServer ajouté
  [✅] application.properties configuré
  [✅] config-repo initialisé avec git
  [✅] Fichiers .properties créés
  [ ] Config Server démarré et testé

MICROSERVICES:
  [ ] Dépendances Config Client ajoutées (chaque service)
  [ ] bootstrap.properties créé (chaque service)
  [ ] application.properties simplifié (chaque service)
  [ ] @RefreshScope ajouté aux Controllers
  [ ] Services démarrés et testés
  [ ] Rafraîchissement dynamique testé
```

---

## 📚 PROCHAINES AMÉLIORATIONS (Optionnel)

### 1. Utiliser GitHub pour config-repo (Production)

```bash
# Créer un repo GitHub
gh repo create gdldv-config-repo --private

# Pousser config-repo
cd config-repo
git remote add origin https://github.com/VotreUsername/gdldv-config-repo.git
git push -u origin master

# Modifier config-server/application.properties
spring.cloud.config.server.git.uri=https://github.com/VotreUsername/gdldv-config-repo.git
```

### 2. Ajouter Spring Cloud Bus (Rafraîchissement global)

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
```

### 3. Créer les configurations de production

```
config-repo/
├── application-prod.properties
├── user-service-prod.properties
├── vehicle-service-prod.properties
└── ...
```

---

## 🆘 TROUBLESHOOTING

### Problème 1: "Connection refused to Config Server"

```
Solution:
1. Vérifier que Config Server est démarré (port 8888)
2. Vérifier spring.cloud.config.uri dans bootstrap.properties
3. Utiliser spring.cloud.config.fail-fast=false pour continuer sans Config Server
```

### Problème 2: "Could not locate PropertySource"

```
Solution:
1. Vérifier que spring.application.name correspond au nom du fichier
2. Vérifier que config-repo est un repo git initialisé
3. Vérifier les logs du Config Server
```

### Problème 3: "Properties not refreshing"

```
Solution:
1. Vérifier que @RefreshScope est présent
2. Commit les changements dans config-repo
3. Appeler POST /actuator/refresh
4. Vérifier que management.endpoints.web.exposure.include=* est présent
```

---

**Config Server prêt à être testé!** 🎉

Commencez par démarrer le Config Server et tester les endpoints avant de modifier les microservices.
