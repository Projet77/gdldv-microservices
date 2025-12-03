# 📋 FICHE COMPLÈTE - DÉMARRER LE PROJET MICROSERVICES DANS INTELLIJ

**Projet:** Système de Gestion des Locations De Voitures (GDLDV)  
**Framework:** Spring Boot 3.3.0  
**Base de données:** MySQL (WAMP)  
**Architecture:** Microservices (3 services)  
**IDE:** IntelliJ IDEA  

---

## 🎯 OBJECTIF FINAL

À la fin de cette fiche, tu auras:
- ✅ 4 microservices Spring Boot créés
- ✅ MySQL configuré avec WAMP
- ✅ Tous les services lancés et testés
- ✅ Prêt à commencer le développement

---

## 📅 PLAN DE TRAVAIL

```
JOUR 1: Préparation et création (2-3 heures)
├─ Étape 1: Préparer l'environnement
├─ Étape 2: Créer les 4 projets Spring Boot
├─ Étape 3: Configurer IntelliJ
└─ Étape 4: Télécharger et organiser

JOUR 2: Configuration (2 heures)
├─ Étape 5: Créer les bases MySQL
├─ Étape 6: Configurer les services
├─ Étape 7: Ajouter les fichiers config
└─ Étape 8: Tester la connexion BD

JOUR 3: Démarrage (1 heure)
├─ Étape 9: Lancer les services dans IntelliJ
├─ Étape 10: Vérifier que tout marche
└─ Étape 11: Commencer le développement
```

---

## 📋 ÉTAPE 1: PRÉPARER L'ENVIRONNEMENT

### 1.1 Vérifier les prérequis

**Avant de commencer, tu DOIS avoir:**

```
✅ IntelliJ IDEA Community ou Pro
   - Télécharge ici: https://www.jetbrains.com/idea/download/
   - Version: 2023.3 ou plus récente

✅ JDK 21 (ou 17)
   - Déjà installé généralement
   - Vérifier: File → Project Structure → Project → SDK

✅ Maven
   - Généralement inclus avec IntelliJ
   - Vérifier: IntelliJ → Preferences → Maven

✅ Git
   - Télécharge: https://git-scm.com/download/win

✅ WAMP Server
   - Télécharge: http://www.wampserver.com/
   - DOIT être lancé et actif (tous les services verts 🟢)

✅ Navigateur web
   - Pour accéder à phpMyAdmin et Swagger
```

### 1.2 Créer le dossier du projet

```bash
# Crée le dossier parent
mkdir C:\Users\Abdou\Documents\gdldv-project
cd C:\Users\Abdou\Documents\gdldv-project

# Initialiser Git
git init
git config user.name "Abdou"
git config user.email "Al.b3@zig.univ.sn"

# Créer .gitignore
cat > .gitignore << EOF
# IDE
.idea/
*.iml
*.iws
*.ipr
.DS_Store

# Build
target/
build/
*.class
*.jar
*.war
*.ear

# Maven
.m2/
dependency-reduced-pom.xml

# Logs
*.log

# Environment
.env
.env.local

# Spring Boot
application.properties
application.yml

# System
Thumbs.db
EOF

# Commit initial
git add .
git commit -m "Initial commit: Empty microservices project"
```

---

## 🚀 ÉTAPE 2: CRÉER LES 4 PROJETS SPRING BOOT

**Tu vas créer 4 projets sur Spring Initializr, puis les télécharger.**

### 2.1 Créer Vehicle Service

**URL:** https://start.spring.io/

**Configuration:**
```
Project:        Maven
Language:       Java
Spring Boot:    3.3.0
Group:          com.gdldv
Artifact:       vehicle-service
Name:           Vehicle Service
Package name:   com.gdldv.vehicle
Java:           21
Packaging:      Jar
```

**Ajouter les dépendances (clique "ADD DEPENDENCIES"):**

```
1. Spring Web
2. Spring Data JPA
3. MySQL Driver
4. Lombok
5. Spring Boot DevTools
6. Thymeleaf
7. Spring Boot Actuator
8. Validation
9. SpringDoc OpenAPI Starter WebMVC UI
```

**Générer:**
- Clique **[GENERATE]**
- Télécharge vehicle-service.zip
- Décompresse dans: `C:\Users\Abdou\Documents\gdldv-project\vehicle-service\`

### 2.2 Créer Reservation Service

**Même configuration que Vehicle Service + 1 dépendance:**

```
Ajouter aussi: Spring Cloud OpenFeign
```

**Télécharge et décompresse dans:** `gdldv-project\reservation-service\`

### 2.3 Créer User Service

**Même configuration que Vehicle Service + 2 dépendances:**

```
Ajouter aussi:
1. Spring Security
2. JJWT (JSON Web Tokens)
```

**Télécharge et décompresse dans:** `gdldv-project\user-service\`

### 2.4 Créer API Gateway

**Configuration spéciale:**

```
Project:        Maven
Language:       Java
Spring Boot:    3.3.0
Group:          com.gdldv
Artifact:       api-gateway
Name:           API Gateway
Package name:   com.gdldv.gateway
Java:           21
Packaging:      Jar
```

**Dépendances:**

```
1. Spring Cloud Gateway
2. Spring Cloud LoadBalancer
3. Spring Cloud OpenFeign
4. Spring Boot Actuator
5. SpringDoc OpenAPI Starter WebMVC UI
6. Lombok (optionnel)
```

**Télécharge et décompresse dans:** `gdldv-project\api-gateway\`

---

## 🖥️ ÉTAPE 3: CONFIGURER INTELLIJ

### 3.1 Ouvrir le projet parent

```
File → Open
Sélectionne: C:\Users\Abdou\Documents\gdldv-project
Clique: OK
```

### 3.2 Créer un projet Maven parent

À la racine du projet (`gdldv-project/`), crée un **pom.xml**:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.gdldv</groupId>
    <artifactId>gdldv-project</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>

    <name>GDLDV - Système de Gestion des Locations De Voitures</name>
    <description>Microservices pour gestion de locations de voitures</description>

    <modules>
        <module>vehicle-service</module>
        <module>reservation-service</module>
        <module>user-service</module>
        <module>api-gateway</module>
    </modules>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <spring-boot.version>3.3.0</spring-boot.version>
        <spring-cloud.version>2023.0.0</spring-cloud.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

</project>
```

**Créer le fichier:**
1. Clique droit sur le dossier gdldv-project
2. New → File
3. Nomme-le: pom.xml
4. Colle le contenu ci-dessus

### 3.3 Configurer IntelliJ pour Maven

```
File → Settings → Build, Execution, Deployment → Maven
┌─────────────────────────────────┐
│ Maven home directory:           │
│ C:\Program Files\JetBrains\...\ │
│ (généralement auto-détecté)     │
│                                 │
│ User settings file: [Auto]      │
│ Local repository: [Auto]        │
└─────────────────────────────────┘
```

Clique **Apply** et **OK**

### 3.4 Rafraîchir Maven

```
View → Tool Windows → Maven
Clique sur: 🔄 (Reload projects)
```

IntelliJ devrait reconnaître les 4 modules:
```
gdldv-project
├─ vehicle-service
├─ reservation-service
├─ user-service
└─ api-gateway
```

---

## 📥 ÉTAPE 4: VÉRIFIER LA STRUCTURE

Ton projet doit ressembler à:

```
gdldv-project/
├── pom.xml                          ← Fichier parent (que tu viens de créer)
├── .gitignore
├── .git/
│
├── vehicle-service/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/gdldv/vehicle/
│   │   │   │       └── VehicleServiceApplication.java
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       ├── application.properties
│   │   │       └── templates/
│   │   └── test/
│   ├── pom.xml
│   └── mvnw
│
├── reservation-service/
│   ├── src/
│   ├── pom.xml
│   └── mvnw
│
├── user-service/
│   ├── src/
│   ├── pom.xml
│   └── mvnw
│
└── api-gateway/
    ├── src/
    ├── pom.xml
    └── mvnw
```

---

## 🗄️ ÉTAPE 5: CRÉER LES BASES MYSQL

### 5.1 Vérifier que WAMP est actif

1. **Ouvre WAMP Server**
2. **Vérifie que tout est vert 🟢:**
   ```
   WAMP Server
   ├─ Apache 🟢
   ├─ MySQL 🟢
   └─ PHP 🟢
   ```

### 5.2 Créer les bases de données

**Ouvre phpMyAdmin:**
```
http://localhost/phpmyadmin
```

**Identifiants:**
```
Username: root
Password: (vide - juste Enter)
```

**Créer les 3 bases:**

1. **Clique "New"** (en haut à gauche)
2. **Database name:** `gdldv_vehicle_db`
3. **Collation:** `utf8mb4_unicode_ci`
4. **Clique "Create"**

**Répète pour:**
- `gdldv_reservation_db`
- `gdldv_user_db`

**Vérification (dans phpMyAdmin):**
```
Databases:
├─ gdldv_vehicle_db       ✅
├─ gdldv_reservation_db   ✅
├─ gdldv_user_db          ✅
├─ mysql
└─ ...
```

---

## ⚙️ ÉTAPE 6: CONFIGURER CHAQUE SERVICE

### 6.1 Vehicle Service

**Ouvre:** `vehicle-service/src/main/resources/application.yml`

**Remplace le contenu par:**

```yaml
spring:
  application:
    name: vehicle-service
  
  datasource:
    url: jdbc:mysql://localhost:3306/gdldv_vehicle_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root
    password: 
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
        format_sql: true
    show-sql: true
    open-in-view: false
  
  thymeleaf:
    prefix: classpath:/templates/vehicle/
    suffix: .html
    cache: false

server:
  port: 8001
  servlet:
    context-path: /vehicle-service

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always

springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    enabled: true

logging:
  level:
    root: INFO
    com.gdldv: DEBUG
    org.hibernate.SQL: DEBUG
```

**Sauvegarde:** Ctrl+S

### 6.2 Reservation Service

**Ouvre:** `reservation-service/src/main/resources/application.yml`

**Remplace par:**

```yaml
spring:
  application:
    name: reservation-service
  
  datasource:
    url: jdbc:mysql://localhost:3306/gdldv_reservation_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root
    password: 
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
        format_sql: true
    show-sql: true
    open-in-view: false
  
  thymeleaf:
    prefix: classpath:/templates/reservation/
    suffix: .html
    cache: false

server:
  port: 8002
  servlet:
    context-path: /reservation-service

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always

springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    enabled: true

logging:
  level:
    root: INFO
    com.gdldv: DEBUG
```

### 6.3 User Service

**Ouvre:** `user-service/src/main/resources/application.yml`

**Remplace par:**

```yaml
spring:
  application:
    name: user-service
  
  datasource:
    url: jdbc:mysql://localhost:3306/gdldv_user_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root
    password: 
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
        format_sql: true
    show-sql: true
    open-in-view: false
  
  thymeleaf:
    prefix: classpath:/templates/user/
    suffix: .html
    cache: false

server:
  port: 8003
  servlet:
    context-path: /user-service

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always

springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    enabled: true

logging:
  level:
    root: INFO
    com.gdldv: DEBUG
```

### 6.4 API Gateway

**Ouvre:** `api-gateway/src/main/resources/application.yml`

**Remplace par:**

```yaml
spring:
  application:
    name: api-gateway
  
  cloud:
    gateway:
      routes:
        - id: vehicle-service
          uri: http://localhost:8001
          predicates:
            - Path=/api/vehicles/**
          filters:
            - StripPrefix=1
        
        - id: reservation-service
          uri: http://localhost:8002
          predicates:
            - Path=/api/reservations/**
          filters:
            - StripPrefix=1
        
        - id: user-service
          uri: http://localhost:8003
          predicates:
            - Path=/api/users/**,/api/auth/**
          filters:
            - StripPrefix=1

server:
  port: 8000

management:
  endpoints:
    web:
      exposure:
        include: health,info,routes,gateway

springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    enabled: true

logging:
  level:
    root: INFO
    com.gdldv: DEBUG
```

---

## 📦 ÉTAPE 7: MODIFIER LES pom.xml (MYSQL)

### 7.1 Vehicle Service pom.xml

**Ouvre:** `vehicle-service/pom.xml`

**Cherche la section `<dependencies>`**

**Remplace:**
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

**Par:**
```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

**Sauvegarde:** Ctrl+S

### 7.2 Reservation Service pom.xml

**Même changement que Vehicle Service**

**PLUS ajoute au `<dependencies>`:**

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

### 7.3 User Service pom.xml

**Même changement que Vehicle Service**

**PLUS ajoute au `<dependencies>`:**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
```

### 7.4 API Gateway pom.xml

**Pas de changement (pas de PostgreSQL/MySQL)**

---

## 🧪 ÉTAPE 8: TESTER LA CONNEXION BD

### 8.1 Charger les dépendances Maven

```
IntelliJ:
Right-click sur vehicle-service/pom.xml
→ Maven
→ Reload project
```

Répète pour les 4 projets.

**IntelliJ va télécharger les dépendances (peut prendre 2-3 min)**

### 8.2 Vérifier qu'il n'y a pas d'erreurs

```
View → Tool Windows → Problems
```

Si tu vois des erreurs rouges:
```
❌ Cannot resolve symbol 'mysql' → Rechargez Maven
❌ Cannot find symbol 'JpaRepository' → Attends la fin du téléchargement
```

---

## 🚀 ÉTAPE 9: LANCER LES SERVICES DANS INTELLIJ

### 9.1 Créer des Run Configurations

**Pour chaque service, crée une configuration:**

#### Vehicle Service:

1. **Run → Edit Configurations**
2. **Clique "+ New"**
3. **Sélectionne "Maven"**
4. **Remplis:**
   ```
   Name: Vehicle Service
   Working directory: $PROJECT_DIR$/vehicle-service
   Command line: clean spring-boot:run
   ```
5. **Clique OK**

#### Répète pour:
- **Reservation Service** (dans reservation-service/)
- **User Service** (dans user-service/)
- **API Gateway** (dans api-gateway/)

### 9.2 Lancer Vehicle Service

1. **Sélectionne la config:** "Vehicle Service" (dropdown en haut)
2. **Clique le bouton "Run" (play ▶️)**
3. **Attend les logs:**

```
...
Tomcat started on port(s): 8001 (http)
Started VehicleServiceApplication in 5.234 seconds
```

**✅ Vehicle Service est lancé!**

### 9.3 Lancer les autres services

**Dans des onglets/fenêtres séparés:**

1. **Sélectionne "Reservation Service"** → Clique Run ▶️
2. **Sélectionne "User Service"** → Clique Run ▶️
3. **Sélectionne "API Gateway"** → Clique Run ▶️

**Tu dois voir 4 services lancés:**
```
✅ Vehicle Service:     Tomcat started on port(s): 8001
✅ Reservation Service: Tomcat started on port(s): 8002
✅ User Service:        Tomcat started on port(s): 8003
✅ API Gateway:         Tomcat started on port(s): 8000
```

---

## ✅ ÉTAPE 10: VÉRIFIER QUE TOUT MARCHE

### 10.1 Tester les services

**Ouvre un navigateur:**

#### Vehicle Service:
```
http://localhost:8001/actuator/health
Résultat attendu: {"status":"UP"}
```

#### Reservation Service:
```
http://localhost:8002/actuator/health
Résultat attendu: {"status":"UP"}
```

#### User Service:
```
http://localhost:8003/actuator/health
Résultat attendu: {"status":"UP"}
```

#### API Gateway:
```
http://localhost:8000/actuator/health
Résultat attendu: {"status":"UP"}
```

### 10.2 Accéder à Swagger

**Chaque service a sa doc Swagger:**

```
Vehicle:    http://localhost:8001/swagger-ui.html
Reservation: http://localhost:8002/swagger-ui.html
User:       http://localhost:8003/swagger-ui.html
Gateway:    http://localhost:8000/swagger-ui.html
```

### 10.3 Vérifier les tables créées

**Ouvre phpMyAdmin:**
```
http://localhost/phpmyadmin
```

**Va dans chaque BD:**
```
gdldv_vehicle_db:
├─ (tables seront créées quand tu créeras les entités)

gdldv_reservation_db:
├─ (tables seront créées quand tu créeras les entités)

gdldv_user_db:
├─ (tables seront créées quand tu créeras les entités)
```

---

## 🎯 ÉTAPE 11: COMMENCER LE DÉVELOPPEMENT

### 11.1 Créer la première entité (Vehicle)

**Ouvre:** `vehicle-service/src/main/java/com/gdldv/vehicle/`

**Crée un nouveau package:** `entity`

**Créer le fichier:** `Vehicle.java`

```java
package com.gdldv.vehicle.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "La marque est obligatoire")
    @Column(nullable = false)
    private String brand;
    
    @NotBlank(message = "Le modèle est obligatoire")
    @Column(nullable = false)
    private String model;
    
    @NotBlank(message = "L'immatriculation est obligatoire")
    @Column(unique = true, nullable = false)
    private String licensePlate;
    
    @Positive(message = "Le kilométrage doit être positif")
    private Long mileage;
    
    @Positive(message = "Le prix par jour doit être positif")
    @Column(nullable = false)
    private Double dailyPrice;
    
    private String category; // SUV, Berline, Monospace, etc.
    
    @Column(columnDefinition = "VARCHAR(255) DEFAULT 'AVAILABLE'")
    private String status; // AVAILABLE, RENTED, MAINTENANCE
}
```

**Sauvegarde:** Ctrl+S

### 11.2 Relancer Vehicle Service

1. **Arrête Vehicle Service** (clique le bouton Stop ⏹️)
2. **Relance-le** (clique Run ▶️)
3. **Vérifie les logs:**

```
Hibernate: CREATE TABLE vehicles (...)
Tomcat started on port(s): 8001
```

### 11.3 Vérifier la table créée

**Ouvre phpMyAdmin:**
```
gdldv_vehicle_db
├─ vehicles ✅ (table créée!)
```

**Bravo! Tu peux maintenant créer ton projet!** 🎉

---

## 📋 CHECKLIST FINALE

### Avant de commencer le développement:

- [ ] WAMP Server est actif (tous les services verts 🟢)
- [ ] 3 bases MySQL créées (gdldv_vehicle_db, gdldv_reservation_db, gdldv_user_db)
- [ ] 4 projets Spring Boot créés
- [ ] Les 4 application.yml sont configurés (MySQL URLs correctes)
- [ ] Les pom.xml sont modifiés (MySQL driver, dépendances)
- [ ] Les 4 services lancent sans erreur
- [ ] Les endpoints /actuator/health retournent "UP"
- [ ] Les Swagger UI sont accessibles
- [ ] Les tables Vehicle créée dans phpMyAdmin

---

## 🔧 DÉPANNAGE RAPIDE

### "Connection refused"
**Cause:** WAMP n'est pas lancé ou MySQL n'est pas actif
**Solution:** Ouvre WAMP Server et vérifie que MySQL est vert

### "Unknown database"
**Cause:** Les bases n'existent pas
**Solution:** Crée-les dans phpMyAdmin

### "Cannot resolve symbol 'mysql'"
**Cause:** Maven n'a pas téléchargé les dépendances
**Solution:** Right-click pom.xml → Maven → Reload project

### "Tomcat started on port 8001 but service not responding"
**Cause:** Erreur au démarrage (vérifie les logs)
**Solution:** Regarde les erreurs rouges dans la console de IntelliJ

---

## 🎓 PROCHAINES ÉTAPES

Après cette fiche, tu peux:

1. **Créer les autres entités** (User, Reservation, etc.)
2. **Créer les Repositories** (JpaRepository)
3. **Créer les Services** (logique métier)
4. **Créer les Controllers** (endpoints REST)
5. **Créer les DTOs** (objets de transfert)
6. **Écrire les tests unitaires**

---

## 📚 RESSOURCES IMPORTANTES

| Ressource | URL |
|-----------|-----|
| **phpMyAdmin** | http://localhost/phpmyadmin |
| **Vehicle Swagger** | http://localhost:8001/swagger-ui.html |
| **Reservation Swagger** | http://localhost:8002/swagger-ui.html |
| **User Swagger** | http://localhost:8003/swagger-ui.html |
| **Gateway Swagger** | http://localhost:8000/swagger-ui.html |
| **Spring Boot Docs** | https://spring.io/projects/spring-boot |
| **Spring Data JPA** | https://spring.io/projects/spring-data-jpa |

---

## ✨ RÉSUMÉ DES ÉTAPES

```
ÉTAPE 1: Préparation ✅
ÉTAPE 2: Créer 4 projets Spring Boot ✅
ÉTAPE 3: Configurer IntelliJ ✅
ÉTAPE 4: Vérifier structure ✅
ÉTAPE 5: Créer 3 bases MySQL ✅
ÉTAPE 6: Configurer application.yml ✅
ÉTAPE 7: Modifier pom.xml (MySQL) ✅
ÉTAPE 8: Tester connexion ✅
ÉTAPE 9: Lancer les services ✅
ÉTAPE 10: Vérifier que tout marche ✅
ÉTAPE 11: Créer première entité ✅

PRÊT À DÉVELOPPER! 🚀
```

---

**Bonne chance avec ton projet!** 💪🎉

Pour toute question, réfère-toi aux guides complets créés:
- GUIDE_MICROSERVICES_3_PERSONNES.md
- CREATION_MICROSERVICES_SPRING_INITIALIZR.md
- MYSQL_WAMPSERVER_MICROSERVICES.md
- DOCUMENTATION_JIRA_COMPLETE.md
