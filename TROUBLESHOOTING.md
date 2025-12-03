# Guide de Dépannage - GDLDV

Ce guide vous aide à résoudre les problèmes courants lors du développement du projet GDLDV.

## Erreurs de Compilation

### ❌ Erreur: `java.lang.ExceptionInInitializerError com.sun.tools.javac.code.TypeTag :: UNKNOWN`

**Cause:** Incompatibilité entre Lombok et la version Java utilisée

**Symptômes:**
```
java: java.lang.ExceptionInInitializerError
com.sun.tools.javac.code.TypeTag :: UNKNOWN
javac 24.0.1 was used to compile java sources
```

**Solution:**

Le projet a été configuré pour utiliser **Java 17** (LTS) qui est totalement compatible.

**Si vous voyez "javac 24.0.1" dans l'erreur:**

👉 **Consultez le guide complet:** `FIX-JAVA-VERSION.md`

**Solution rapide:**

1. File → Project Structure → Project
2. SDK: Sélectionner **17** (télécharger si nécessaire)
3. File → Settings → Compiler → Java Compiler
4. Project bytecode version: **17**
5. Reload Maven et recompiler

Si vous rencontrez toujours cette erreur:

#### 1. Vérifier la version de Lombok dans pom.xml

Chaque service doit avoir:
```xml
<properties>
    <java.version>21</java.version>
    <lombok.version>1.18.30</lombok.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>${lombok.version}</version>
        <optional>true</optional>
    </dependency>
</dependencies>
```

#### 2. Recharger Maven

Dans IntelliJ:
1. Ouvrir la fenêtre Maven (View → Tool Windows → Maven)
2. Cliquer sur 🔄 "Reload All Maven Projects"
3. Attendre la fin du téléchargement

#### 3. Nettoyer et recompiler

```bash
mvn clean install
```

Ou dans IntelliJ:
- Build → Rebuild Project

#### 4. Installer le plugin Lombok dans IntelliJ

1. File → Settings → Plugins
2. Chercher "Lombok"
3. Installer "Lombok Plugin"
4. Redémarrer IntelliJ

#### 5. Activer l'annotation processing

1. File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors
2. Cocher: ✅ Enable annotation processing
3. Cliquer Apply et OK

#### 6. Invalider les caches

Si le problème persiste:
1. File → Invalidate Caches / Restart
2. Cocher "Invalidate and Restart"

## Erreurs de Base de Données

### ❌ Erreur: `Unknown database 'gdldv_vehicle_db'`

**Cause:** La base de données n'existe pas

**Solution:**
1. Ouvrir phpMyAdmin: http://localhost/phpmyadmin
2. Créer la base: `gdldv_vehicle_db`
3. Interclassement: `utf8mb4_unicode_ci`
4. Répéter pour `gdldv_reservation_db` et `gdldv_user_db`

### ❌ Erreur: `Access denied for user 'root'@'localhost'`

**Cause:** Mauvais identifiants MySQL

**Solution:**

Vérifier dans `application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=
```

Par défaut, WAMP n'a pas de mot de passe pour root (laisser vide).

### ❌ Erreur: `Table 'vehicles' doesn't exist`

**Cause:** Hibernate n'a pas créé la table

**Solution:**

1. Vérifier `application.properties`:
```properties
spring.jpa.hibernate.ddl-auto=update  # Pas validate, pas none
```

2. Vérifier que l'entité a les bonnes annotations:
```java
@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // ...
}
```

3. Regarder les logs au démarrage pour voir les commandes SQL

### ❌ Erreur: `Communications link failure`

**Cause:** WAMP Server n'est pas démarré ou MySQL n'est pas actif

**Solution:**
1. Démarrer WAMP Server
2. Vérifier que l'icône est verte
3. Vérifier que MySQL est démarré (vert 🟢)

## Erreurs de Port

### ❌ Erreur: `Port 8001 is already in use`

**Cause:** Un autre processus utilise ce port

**Solution:**

#### Option 1: Trouver et tuer le processus

Windows:
```bash
netstat -ano | findstr :8001
taskkill /PID <PID> /F
```

#### Option 2: Changer le port

Dans `application.properties`:
```properties
server.port=8011  # Au lieu de 8001
```

## Erreurs Maven

### ❌ Erreur: `Cannot resolve symbol`

**Cause:** Les dépendances Maven ne sont pas téléchargées

**Solution:**

1. Recharger Maven:
   - View → Tool Windows → Maven
   - Cliquer 🔄 Reload All Maven Projects

2. Si ça ne marche pas, nettoyer le cache Maven:
```bash
mvn dependency:purge-local-repository
mvn clean install
```

### ❌ Erreur: `Failed to execute goal`

**Cause:** Erreur de compilation ou dépendances manquantes

**Solution:**

1. Nettoyer et recompiler:
```bash
mvn clean install -U
```

Le flag `-U` force la mise à jour des dépendances.

## Erreurs Spring Boot

### ❌ Erreur: `Failed to configure a DataSource`

**Cause:** Configuration de la base de données incorrecte

**Solution:**

Vérifier `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gdldv_vehicle_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### ❌ Erreur: `Whitelabel Error Page`

**Cause:** Pas de contrôleur pour cette route

**Solution:**

Vérifier que:
1. Le contrôleur existe
2. Le contrôleur a `@RestController` ou `@Controller`
3. Le mapping est correct: `@GetMapping("/vehicles")`
4. Le context-path est pris en compte dans l'URL

Exemple:
- Service: http://localhost:8001/vehicle-service/vehicles
- Gateway: http://localhost:8000/api/vehicles/vehicles

### ❌ Erreur: `Bean creation failed`

**Cause:** Problème d'injection de dépendance

**Solution:**

Vérifier que:
1. Les classes ont les bonnes annotations (`@Service`, `@Repository`, `@Component`)
2. Les constructeurs utilisent `@RequiredArgsConstructor` (Lombok) ou `@Autowired`
3. Pas de dépendances circulaires

## Erreurs Feign Client

### ❌ Erreur: `Connection refused` (Reservation Service → Vehicle Service)

**Cause:** Vehicle Service n'est pas démarré ou URL incorrecte

**Solution:**

1. Démarrer Vehicle Service AVANT Reservation Service
2. Vérifier `application.properties` du Reservation Service:
```properties
feign.client.config.vehicle-service.url=http://localhost:8001
```

3. Vérifier que Vehicle Service est accessible:
   - http://localhost:8001/vehicle-service/actuator/health

## Erreurs Spring Security (User Service)

### ❌ Erreur: `401 Unauthorized` sur tous les endpoints

**Cause:** Spring Security bloque par défaut

**Solution:**

Vérifier `SecurityConfig.java`:
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/actuator/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .requestMatchers("/users/**", "/auth/**").permitAll()
            .anyRequest().authenticated()
        );
    return http.build();
}
```

## Erreurs IntelliJ

### ❌ Les annotations Lombok ne fonctionnent pas

**Solution:**

1. Installer le plugin Lombok:
   - File → Settings → Plugins
   - Chercher "Lombok"
   - Installer et redémarrer

2. Activer annotation processing:
   - File → Settings → Compiler → Annotation Processors
   - ✅ Enable annotation processing

### ❌ Projet ne se compile pas dans IntelliJ mais fonctionne avec Maven

**Solution:**

1. File → Project Structure
2. Project → SDK: Vérifier que c'est Java 21
3. Modules → Vérifier que tous les modules sont détectés
4. File → Invalidate Caches / Restart

## Erreurs API Gateway

### ❌ Erreur: `503 Service Unavailable` via Gateway

**Cause:** Le service cible n'est pas accessible

**Solution:**

1. Vérifier que tous les services sont démarrés:
   - Vehicle Service: http://localhost:8001/vehicle-service/actuator/health
   - Reservation Service: http://localhost:8002/reservation-service/actuator/health
   - User Service: http://localhost:8003/user-service/actuator/health

2. Vérifier les routes dans `application.properties` du Gateway:
```properties
spring.cloud.gateway.routes[0].id=vehicle-service
spring.cloud.gateway.routes[0].uri=http://localhost:8001
spring.cloud.gateway.routes[0].predicates[0]=Path=/api/vehicles/**
spring.cloud.gateway.routes[0].filters[0]=StripPrefix=1
```

### ❌ Erreur: `404 Not Found` via Gateway

**Cause:** Mauvais mapping de routes

**Solution:**

Vérifier le pattern d'URL:
- Gateway attend: `/api/vehicles/...`
- Gateway redirige vers: `http://localhost:8001/vehicle-service/...`

Test:
```bash
# Direct (doit marcher)
curl http://localhost:8001/vehicle-service/vehicles

# Via Gateway (doit marcher aussi)
curl http://localhost:8000/api/vehicles/vehicles
```

## Logs et Debugging

### Activer les logs détaillés

Dans `application.properties`:
```properties
# Logs SQL
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Logs Spring
logging.level.org.springframework.web=DEBUG

# Logs de votre application
logging.level.com.gdldv=DEBUG

# Logs Gateway
logging.level.org.springframework.cloud.gateway=DEBUG

# Logs Feign
logging.level.org.springframework.cloud.openfeign=DEBUG
```

### Voir les requêtes HTTP

Dans IntelliJ:
1. Run → Edit Configurations
2. Sélectionner votre service
3. VM options: `-Dlogging.level.org.springframework.web=DEBUG`

## Commandes utiles

### Vérifier que Java 21 est utilisé
```bash
java -version
```

### Nettoyer complètement le projet
```bash
mvn clean
rm -rf target/
rm -rf ~/.m2/repository/com/gdldv/
```

### Vérifier les dépendances Maven
```bash
mvn dependency:tree
```

### Tester la connexion MySQL
```bash
mysql -u root -p -h localhost
SHOW DATABASES;
USE gdldv_vehicle_db;
SHOW TABLES;
```

### Voir les processus Java en cours
```bash
jps -l
```

## Checklist de démarrage

Avant de démarrer les services, vérifier:

- [ ] WAMP Server est démarré (icône verte)
- [ ] MySQL est actif dans WAMP
- [ ] Les 3 bases de données existent
- [ ] Les dépendances Maven sont téléchargées
- [ ] Le plugin Lombok est installé
- [ ] Annotation processing est activé
- [ ] JDK 21 est configuré dans le projet
- [ ] Aucune erreur de compilation
- [ ] Tous les pom.xml ont Lombok version 1.18.30

## Ordre de démarrage recommandé

1. **Vehicle Service** (8001) - Pas de dépendances
2. **User Service** (8003) - Pas de dépendances
3. **Reservation Service** (8002) - Dépend de Vehicle Service
4. **API Gateway** (8000) - Point d'entrée

## Besoin d'aide supplémentaire?

Si le problème persiste:

1. Regarder les logs complets dans la console IntelliJ
2. Chercher l'erreur exacte dans les logs
3. Vérifier que tous les fichiers sont correctement sauvegardés
4. Redémarrer IntelliJ
5. Redémarrer WAMP Server

## Ressources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Lombok Documentation](https://projectlombok.org/)
- [Hibernate Documentation](https://hibernate.org/orm/documentation/)
- [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)
