# Notes sur la Gestion des Bases de Données

## Important à comprendre

### 🗄️ Bases de données vs Tables

| Élément | Créé par | Comment |
|---------|----------|---------|
| **Bases de données** | **Manuellement** | phpMyAdmin ou MySQL CLI |
| **Tables** | **Automatiquement** | Annotations JPA + Hibernate |

### Pourquoi créer les bases manuellement?

Hibernate (JPA) **ne peut pas** créer les bases de données MySQL.
Il peut seulement créer/modifier les **tables** à l'intérieur d'une base existante.

## Processus complet

### 1️⃣ Créer les bases (une seule fois)

**Via phpMyAdmin:**
```
1. Ouvrir http://localhost/phpmyadmin
2. Cliquer "Nouvelle base de données"
3. Nom: gdldv_vehicle_db
4. Interclassement: utf8mb4_unicode_ci
5. Cliquer "Créer"
6. Répéter pour gdldv_reservation_db et gdldv_user_db
```

**Via MySQL CLI:**
```sql
mysql -u root -p

CREATE DATABASE gdldv_vehicle_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE gdldv_reservation_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE gdldv_user_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

SHOW DATABASES LIKE 'gdldv%';
EXIT;
```

### 2️⃣ Les tables se créent automatiquement

Une fois les bases créées, au premier démarrage de chaque service:

**Vehicle Service démarre (8001)**
```
✅ Connexion à gdldv_vehicle_db
✅ Hibernate scanne Vehicle.java (@Entity)
✅ Génère: CREATE TABLE vehicles (...)
✅ Table 'vehicles' créée automatiquement
```

**Reservation Service démarre (8002)**
```
✅ Connexion à gdldv_reservation_db
✅ Hibernate scanne Reservation.java (@Entity)
✅ Génère: CREATE TABLE reservations (...)
✅ Table 'reservations' créée automatiquement
```

**User Service démarre (8003)**
```
✅ Connexion à gdldv_user_db
✅ Hibernate scanne User.java (@Entity)
✅ Génère: CREATE TABLE users (...)
✅ Table 'users' créée automatiquement
```

## Configuration dans application.properties

```properties
# URL de la base (doit exister!)
spring.datasource.url=jdbc:mysql://localhost:3306/gdldv_vehicle_db

# Hibernate crée/met à jour les tables automatiquement
spring.jpa.hibernate.ddl-auto=update

# Affiche les requêtes SQL générées
spring.jpa.show-sql=true
```

## Ce que fait `ddl-auto=update`

```
Au démarrage du service:
1. Connexion à la base de données
2. Vérification des tables existantes
3. Comparaison avec les entités Java (@Entity)
4. Si table manquante → CREATE TABLE
5. Si colonne manquante → ALTER TABLE ADD COLUMN
6. Si colonne modifiée → ALTER TABLE MODIFY COLUMN
7. Logs des opérations SQL
```

## Vérification après démarrage

### Dans phpMyAdmin:

**gdldv_vehicle_db**
```
├─ vehicles (créée par Hibernate)
│  ├─ id (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
│  ├─ brand (VARCHAR 255, NOT NULL)
│  ├─ model (VARCHAR 255, NOT NULL)
│  ├─ license_plate (VARCHAR 255, UNIQUE, NOT NULL)
│  ├─ mileage (BIGINT)
│  ├─ daily_price (DOUBLE, NOT NULL)
│  ├─ category (VARCHAR 255)
│  └─ status (VARCHAR 255, DEFAULT 'AVAILABLE')
```

**gdldv_reservation_db**
```
├─ reservations (créée par Hibernate)
│  ├─ id (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
│  ├─ vehicle_id (BIGINT, NOT NULL)
│  ├─ user_id (BIGINT, NOT NULL)
│  ├─ start_date (DATE, NOT NULL)
│  ├─ end_date (DATE, NOT NULL)
│  ├─ total_price (DOUBLE, NOT NULL)
│  ├─ status (VARCHAR 255, DEFAULT 'PENDING')
│  └─ notes (VARCHAR 500)
```

**gdldv_user_db**
```
├─ users (créée par Hibernate)
│  ├─ id (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
│  ├─ first_name (VARCHAR 255, NOT NULL)
│  ├─ last_name (VARCHAR 255, NOT NULL)
│  ├─ email (VARCHAR 255, UNIQUE, NOT NULL)
│  ├─ password (VARCHAR 255, NOT NULL)
│  ├─ phone_number (VARCHAR 20)
│  ├─ address (VARCHAR 500)
│  ├─ role (VARCHAR 255, NOT NULL)
│  ├─ active (BOOLEAN, NOT NULL)
│  ├─ created_at (DATETIME, NOT NULL)
│  └─ updated_at (DATETIME)
```

### Dans les logs IntelliJ:

Rechercher:
```
Hibernate: create table vehicles (
Hibernate: create table reservations (
Hibernate: create table users (
```

## Ajouter une nouvelle table

### 1. Créer une nouvelle entité:

```java
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long reservationId;
    private Double amount;
    private LocalDateTime paymentDate;
}
```

### 2. Redémarrer le service:

```
✅ Hibernate détecte la nouvelle @Entity
✅ Génère: CREATE TABLE payments (...)
✅ Table créée automatiquement
```

## Modifier une table existante

### 1. Ajouter un champ dans l'entité:

```java
@Entity
@Table(name = "vehicles")
public class Vehicle {
    // ... champs existants ...

    // NOUVEAU CHAMP
    @Column(length = 50)
    private String color;
}
```

### 2. Redémarrer le service:

```
✅ Hibernate détecte le nouveau champ
✅ Génère: ALTER TABLE vehicles ADD COLUMN color VARCHAR(50)
✅ Colonne ajoutée automatiquement
```

## Réinitialiser les tables

Si vous voulez repartir de zéro:

### Option 1: Supprimer les tables dans phpMyAdmin
```
1. Sélectionner la table
2. Cliquer "Supprimer"
3. Redémarrer le service
4. Les tables sont recréées vides
```

### Option 2: Changer temporairement ddl-auto
```properties
# Dans application.properties
spring.jpa.hibernate.ddl-auto=create  # ATTENTION: supprime toutes les données!
```
```
1. Démarrer le service
2. Tables recréées vides
3. Remettre ddl-auto=update
4. Redémarrer
```

## Erreurs courantes

### ❌ "Unknown database 'gdldv_vehicle_db'"

**Cause:** La base de données n'existe pas

**Solution:**
```sql
CREATE DATABASE gdldv_vehicle_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### ❌ "Table 'vehicles' doesn't exist"

**Cause:** Hibernate n'a pas créé la table

**Vérifier:**
1. `spring.jpa.hibernate.ddl-auto=update` dans application.properties
2. La classe Vehicle a bien `@Entity` et `@Table`
3. Regarder les logs au démarrage
4. Vérifier qu'il n'y a pas d'erreur SQL dans les logs

### ❌ Les colonnes ne se créent pas

**Solution:**
```properties
# Vérifier la configuration
spring.jpa.hibernate.ddl-auto=update  # Pas validate, pas none
spring.jpa.show-sql=true              # Pour voir les requêtes SQL
```

## Résumé

| Action | Méthode | Fichier |
|--------|---------|---------|
| Créer les BDs | Manuelle (phpMyAdmin) | - |
| Créer les tables | Automatique (Hibernate) | Entity classes |
| Ajouter une colonne | Automatique (Hibernate) | Modifier l'entité |
| Supprimer une colonne | Manuelle (SQL) | - |
| Modifier une contrainte | Manuelle (SQL) | - |

**En résumé:**
- ✅ **Vous créez:** Les bases de données (3 fois, au début)
- ✅ **Hibernate crée:** Toutes les tables et colonnes automatiquement
- ✅ **Avantage:** Pas besoin d'écrire du SQL pour les tables!

Pour plus de détails, consultez: `ANNOTATIONS-JPA-GUIDE.md`
