# Correction du problème Java 24 → Java 17

## Problème identifié

Votre système utilise **Java 24** mais le projet a besoin de **Java 17** (version LTS stable).

Message d'erreur:
```
javac 24.0.1 was used to compile java sources
java.lang.ExceptionInInitializerError
com.sun.tools.javac.code.TypeTag :: UNKNOWN
```

## Solution: Configurer IntelliJ pour utiliser Java 17

### Étape 1: Télécharger Java 17 (si pas déjà installé)

**Option A: Via IntelliJ (Recommandé)**
1. File → Project Structure (Ctrl+Alt+Shift+S)
2. Platform Settings → SDKs
3. Cliquer sur le **+** (Add New SDK)
4. Sélectionner **Download JDK...**
5. Choisir:
   - **Version: 17**
   - **Vendor: Oracle OpenJDK** ou **Eclipse Temurin (AdoptOpenJDK)**
6. Cliquer **Download**
7. Attendre la fin du téléchargement

**Option B: Téléchargement manuel**
- Oracle JDK 17: https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
- Eclipse Temurin 17: https://adoptium.net/temurin/releases/?version=17

### Étape 2: Configurer le JDK du projet

1. **File → Project Structure** (Ctrl+Alt+Shift+S)

2. **Project Settings → Project**
   - SDK: Sélectionner **17** (ou **java version "17.x.x"**)
   - Language level: **17 - Sealed types, always-strict floating-point semantics**
   - Cliquer **Apply**

3. **Project Settings → Modules**
   - Sélectionner chaque module (vehicle-service, reservation-service, etc.)
   - Pour chaque module:
     - Sources → Language level: **17**
     - Dependencies → Module SDK: **Project SDK (17)**
   - Cliquer **Apply**

4. **Platform Settings → SDKs**
   - Vérifier que JDK 17 est bien présent et configuré
   - Supprimer JDK 24 si vous ne l'utilisez pas ailleurs (optionnel)

5. Cliquer **OK** pour fermer

### Étape 3: Configurer le compilateur Java

1. **File → Settings** (Ctrl+Alt+S)

2. **Build, Execution, Deployment → Compiler → Java Compiler**
   - Project bytecode version: **17**
   - Target bytecode version pour chaque module:
     - vehicle-service: **17**
     - reservation-service: **17**
     - user-service: **17**
     - api-gateway: **17**
   - Cliquer **Apply**

3. **Build, Execution, Deployment → Compiler → Annotation Processors**
   - ✅ Enable annotation processing (pour Lombok)
   - Cliquer **Apply** et **OK**

### Étape 4: Vérifier la configuration Maven

1. **File → Settings → Build, Execution, Deployment → Build Tools → Maven**
   - Maven home directory: Utiliser Maven inclus dans IntelliJ
   - JRE for importer: **Use Project JDK (17)**
   - Cliquer **Apply** et **OK**

### Étape 5: Recharger Maven

1. Ouvrir la fenêtre Maven (View → Tool Windows → Maven)
2. Cliquer sur **🔄 Reload All Maven Projects**
3. Attendre que Maven télécharge les dépendances

### Étape 6: Nettoyer et recompiler

**Option A: Via IntelliJ**
1. Build → Clean Project
2. Build → Rebuild Project

**Option B: Via Terminal dans IntelliJ**
```bash
mvn clean install
```

### Étape 7: Invalider les caches (important!)

1. **File → Invalidate Caches / Restart**
2. Cocher:
   - ✅ Invalidate and Restart
3. Attendre le redémarrage d'IntelliJ

### Étape 8: Vérifier la configuration

Après le redémarrage:

1. **File → Project Structure → Project**
   - Vérifier: SDK = **17**

2. **Créer un fichier Java test:**
   - Clic droit sur un package
   - New → Java Class → Test
   - Écrire: `System.out.println(System.getProperty("java.version"));`
   - Run → Le résultat doit être **17.x.x**

3. **Compiler le projet:**
   - Build → Rebuild Project
   - Aucune erreur ne doit apparaître

## Vérification complète

### Dans le terminal IntelliJ (Alt+F12):

```bash
# Vérifier la version Java utilisée par Maven
mvn -version

# Doit afficher:
# Apache Maven 3.x.x
# Java version: 17.x.x, vendor: Oracle Corporation (ou Eclipse Adoptium)
# Default locale: en_US, platform encoding: UTF-8
# OS name: "windows 11", version: "...", arch: "amd64"
```

### Compiler chaque service:

```bash
# Vehicle Service
cd vehicle-service
mvn clean compile
# Doit se terminer par: BUILD SUCCESS

# Reservation Service
cd ../reservation-service
mvn clean compile
# Doit se terminer par: BUILD SUCCESS

# User Service
cd ../user-service
mvn clean compile
# Doit se terminer par: BUILD SUCCESS

# API Gateway
cd ../api-gateway
mvn clean compile
# Doit se terminer par: BUILD SUCCESS
```

## Si l'erreur persiste

### Vérifier les variables d'environnement (Windows):

1. **Rechercher:** Variables d'environnement
2. **Variables système:**
   - `JAVA_HOME` doit pointer vers JDK 17
   - Exemple: `C:\Program Files\Java\jdk-17`
3. **Path:**
   - `%JAVA_HOME%\bin` doit être présent
4. **Redémarrer l'ordinateur** après modification

### Vérifier dans le terminal Windows (PowerShell):

```bash
# Vérifier la version Java système
java -version

# Doit afficher:
# java version "17.0.x" ...
# Java(TM) SE Runtime Environment ...
# Java HotSpot(TM) 64-Bit Server VM ...
```

Si ce n'est pas Java 17:
1. Modifier `JAVA_HOME` pour pointer vers JDK 17
2. Redémarrer l'ordinateur
3. Relancer IntelliJ

### Si IntelliJ ne trouve pas JDK 17:

1. **File → Project Structure → SDKs**
2. Cliquer sur **+** → **Add JDK**
3. Naviguer vers:
   - Windows: `C:\Program Files\Java\jdk-17` (ou similaire)
   - Ou: `C:\Users\<Votre-Utilisateur>\.jdks\openjdk-17.x.x`
4. Sélectionner le dossier JDK 17
5. Cliquer **OK**

## Résumé des modifications effectuées

Les fichiers suivants ont été mis à jour pour utiliser Java 17:

### 1. pom.xml (projet parent)
```xml
<properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
</properties>
```

### 2-5. Tous les services (vehicle, reservation, user, gateway)
```xml
<properties>
    <java.version>17</java.version>
    <lombok.version>1.18.30</lombok.version>
</properties>
```

## Pourquoi Java 17 et pas Java 21 ou 24?

| Version | Type | Support | Compatibilité |
|---------|------|---------|---------------|
| Java 17 | **LTS** | Jusqu'en 2029 | ✅ Excellente avec Spring Boot 3.3.0 |
| Java 21 | **LTS** | Jusqu'en 2031 | ✅ Bonne mais nécessite Lombok récent |
| Java 24 | Non-LTS | 6 mois | ⚠️ Peut causer des problèmes |

**Java 17 est le choix recommandé pour:**
- Spring Boot 3.3.0
- Lombok 1.18.30
- Stabilité en production
- Support à long terme

## Après la correction

Une fois configuré correctement:

1. ✅ Plus d'erreur `ExceptionInInitializerError`
2. ✅ Le projet compile sans erreur
3. ✅ Tous les services démarrent correctement
4. ✅ Lombok fonctionne parfaitement

## Checklist finale

Avant de lancer les services:

- [ ] JDK 17 est installé
- [ ] IntelliJ Project SDK = 17
- [ ] IntelliJ Compiler bytecode version = 17
- [ ] Maven JRE = Project JDK (17)
- [ ] Annotation processing activé
- [ ] Maven dependencies rechargées
- [ ] Projet nettoyé et recompilé (mvn clean install)
- [ ] Caches IntelliJ invalidés
- [ ] Aucune erreur de compilation
- [ ] mvn -version affiche Java 17

Si tous les points sont cochés ✅, votre projet est prêt!

## Commandes de test

```bash
# Compiler tout le projet
mvn clean install

# Lancer un service
cd vehicle-service
mvn spring-boot:run

# Vérifier que le service démarre
# Doit afficher:
# Started VehicleServiceApplication in X.XXX seconds
```

Bonne compilation! 🚀
