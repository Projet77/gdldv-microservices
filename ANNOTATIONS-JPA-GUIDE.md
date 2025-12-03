# Guide des Annotations JPA - Création Automatique des Tables

## Comment ça fonctionne?

Le projet utilise **Hibernate** (implémentation de JPA) pour créer automatiquement les tables de la base de données à partir des classes Java annotées.

## Configuration nécessaire

### Dans application.properties:

```properties
# Stratégie de création des tables
spring.jpa.hibernate.ddl-auto=update

# Afficher les requêtes SQL générées
spring.jpa.show-sql=true

# Formatter les requêtes SQL
spring.jpa.properties.hibernate.format_sql=true
```

### Valeurs possibles pour `ddl-auto`:

| Valeur | Description | Utilisation |
|--------|-------------|-------------|
| `create` | Supprime et recrée les tables à chaque démarrage | Développement initial |
| `create-drop` | Crée les tables au démarrage, les supprime à l'arrêt | Tests |
| `update` | Crée ou met à jour les tables sans perte de données | **Recommandé pour développement** |
| `validate` | Valide que les tables correspondent aux entités | Production |
| `none` | Aucune action automatique | Production avec scripts SQL |

## Annotations utilisées dans le projet

### 1. Vehicle Service - Entity: Vehicle

```java
@Entity                          // Indique que c'est une entité JPA
@Table(name = "vehicles")        // Nom de la table dans la BD
@Data                            // Lombok: génère getters/setters
@NoArgsConstructor              // Lombok: constructeur vide
@AllArgsConstructor             // Lombok: constructeur avec tous les champs
public class Vehicle {

    @Id                                        // Clé primaire
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-incrémentation
    private Long id;

    @NotBlank(message = "La marque est obligatoire")    // Validation
    @Column(nullable = false)                            // Colonne non-nulle
    private String brand;

    @NotBlank(message = "Le modèle est obligatoire")
    @Column(nullable = false)
    private String model;

    @NotBlank(message = "L'immatriculation est obligatoire")
    @Column(unique = true, nullable = false)   // Colonne unique
    private String licensePlate;

    @Positive(message = "Le kilométrage doit être positif")
    private Long mileage;

    @Positive(message = "Le prix par jour doit être positif")
    @Column(nullable = false)
    private Double dailyPrice;

    private String category;

    @Column(columnDefinition = "VARCHAR(255) DEFAULT 'AVAILABLE'")  // Type SQL personnalisé
    private String status;
}
```

**Table SQL générée automatiquement:**
```sql
CREATE TABLE vehicles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    brand VARCHAR(255) NOT NULL,
    model VARCHAR(255) NOT NULL,
    license_plate VARCHAR(255) UNIQUE NOT NULL,
    mileage BIGINT,
    daily_price DOUBLE NOT NULL,
    category VARCHAR(255),
    status VARCHAR(255) DEFAULT 'AVAILABLE'
);
```

### 2. Reservation Service - Entity: Reservation

```java
@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "L'ID du véhicule est obligatoire")
    @Column(nullable = false)
    private Long vehicleId;

    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    @Column(nullable = false)
    private Long userId;

    @NotNull(message = "La date de début est obligatoire")
    @Future(message = "La date de début doit être dans le futur")
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull(message = "La date de fin est obligatoire")
    @Future(message = "La date de fin doit être dans le futur")
    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Double totalPrice;

    @Column(columnDefinition = "VARCHAR(255) DEFAULT 'PENDING'")
    private String status;

    @Column(length = 500)    // VARCHAR(500)
    private String notes;
}
```

**Table SQL générée:**
```sql
CREATE TABLE reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicle_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    total_price DOUBLE NOT NULL,
    status VARCHAR(255) DEFAULT 'PENDING',
    notes VARCHAR(500)
);
```

### 3. User Service - Entity: User

```java
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Column(nullable = false)
    private String firstName;

    @NotBlank(message = "Le prénom est obligatoire")
    @Column(nullable = false)
    private String lastName;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    @Column(nullable = false)
    private String password;

    @Column(length = 20)
    private String phoneNumber;

    @Column(length = 500)
    private String address;

    @Column(nullable = false)
    private String role = "USER";

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false, updatable = false)   // Non modifiable après création
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @PreUpdate   // Callback avant mise à jour
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
```

**Table SQL générée:**
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    address VARCHAR(500),
    role VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);
```

## Annotations JPA importantes

### Annotations de classe
- `@Entity` - Marque la classe comme une entité JPA
- `@Table(name = "...")` - Spécifie le nom de la table
- `@Data` - (Lombok) Génère getters, setters, toString, equals, hashCode
- `@NoArgsConstructor` - (Lombok) Génère un constructeur vide
- `@AllArgsConstructor` - (Lombok) Génère un constructeur avec tous les champs

### Annotations de champ
- `@Id` - Marque le champ comme clé primaire
- `@GeneratedValue(strategy = GenerationType.IDENTITY)` - Auto-incrémentation
- `@Column(nullable = false)` - Colonne non-nulle
- `@Column(unique = true)` - Contrainte d'unicité
- `@Column(length = 500)` - Longueur maximale
- `@Column(columnDefinition = "...")` - Définition SQL personnalisée

### Annotations de validation
- `@NotNull` - Le champ ne peut pas être null
- `@NotBlank` - Le champ ne peut pas être vide (pour String)
- `@Email` - Valide le format email
- `@Size(min, max)` - Longueur du champ
- `@Positive` - Valeur positive uniquement
- `@Future` - Date dans le futur

### Annotations de callback
- `@PrePersist` - Avant insertion en BD
- `@PreUpdate` - Avant mise à jour en BD
- `@PreRemove` - Avant suppression en BD
- `@PostLoad` - Après chargement depuis la BD

## Processus de création automatique

### 1. Au démarrage du service:

```
Application démarre
    ↓
Hibernate scanne les classes @Entity
    ↓
Analyse les annotations (@Column, @Id, etc.)
    ↓
Compare avec la structure de la BD
    ↓
Génère les commandes SQL (CREATE/ALTER)
    ↓
Exécute les commandes SQL
    ↓
Tables créées/mises à jour
    ↓
Application prête
```

### 2. Logs générés:

```
Hibernate:
    create table vehicles (
       id bigint not null auto_increment,
        brand varchar(255) not null,
        model varchar(255) not null,
        license_plate varchar(255) not null,
        mileage bigint,
        daily_price double not null,
        category varchar(255),
        status varchar(255) default 'AVAILABLE',
        primary key (id)
    ) engine=InnoDB

Hibernate:
    alter table vehicles
       add constraint UK_license_plate unique (license_plate)
```

## Vérification des tables créées

### Via phpMyAdmin:

1. Ouvrir http://localhost/phpmyadmin
2. Sélectionner la base `gdldv_vehicle_db`
3. Voir la table `vehicles` créée automatiquement
4. Cliquer sur "Structure" pour voir les colonnes

### Via les logs IntelliJ:

Rechercher dans les logs:
```
Hibernate: create table vehicles
Hibernate: create table reservations
Hibernate: create table users
```

## Ajouter une nouvelle colonne

### Étape 1: Modifier l'entité

```java
@Entity
@Table(name = "vehicles")
public class Vehicle {
    // ... champs existants ...

    // NOUVEAU CHAMP
    @Column(length = 50)
    private String color;  // Couleur du véhicule
}
```

### Étape 2: Redémarrer le service

Hibernate détecte le nouveau champ et génère:
```sql
ALTER TABLE vehicles ADD COLUMN color VARCHAR(50);
```

### Étape 3: Vérifier

La colonne `color` apparaît automatiquement dans la table!

## Bonnes pratiques

### ✅ À FAIRE:

1. **Toujours utiliser `@Column(nullable = false)` pour les champs obligatoires**
   ```java
   @Column(nullable = false)
   private String brand;
   ```

2. **Définir les contraintes d'unicité**
   ```java
   @Column(unique = true)
   private String licensePlate;
   ```

3. **Spécifier la longueur des champs texte**
   ```java
   @Column(length = 500)
   private String notes;
   ```

4. **Utiliser des valeurs par défaut**
   ```java
   @Column(columnDefinition = "VARCHAR(255) DEFAULT 'AVAILABLE'")
   private String status;
   ```

5. **Nommer explicitement les tables**
   ```java
   @Table(name = "vehicles")  // Nom clair et en minuscules
   ```

### ❌ À ÉVITER:

1. **Ne pas utiliser `ddl-auto=create` en production** (perte de données!)

2. **Ne pas oublier `@Column(nullable = false)` sur les champs requis**

3. **Éviter les noms de colonnes réservés SQL** (order, user, group, etc.)

4. **Ne pas modifier une colonne existante sans précaution** (risque de perte de données)

## Migration en production

Pour la production, utiliser des scripts SQL avec Flyway ou Liquibase:

```properties
# application-prod.properties
spring.jpa.hibernate.ddl-auto=validate  # Ne modifie plus la BD
```

Puis gérer les migrations avec Flyway:
```
src/main/resources/db/migration/
├── V1__create_vehicles_table.sql
├── V2__create_users_table.sql
└── V3__create_reservations_table.sql
```

## Résumé

✅ **Avantages:**
- Pas besoin d'écrire du SQL manuellement
- Synchronisation automatique code ↔ BD
- Facilite le développement rapide
- Les changements de modèle se répercutent automatiquement

⚠️ **Limitations:**
- Ne crée PAS les bases de données (juste les tables)
- Pas adapté pour la production
- Migrations complexes nécessitent des scripts SQL
- Risque de perte de données avec `ddl-auto=create`

🎯 **Pour ce projet:**
- Créer manuellement les 3 bases de données vides
- Les tables seront créées automatiquement au démarrage
- Utiliser `ddl-auto=update` en développement
- Les annotations JPA gèrent tout le reste!
