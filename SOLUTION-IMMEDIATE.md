# ✅ Solution Immédiate - Correction de l'erreur Java

## Le problème

Votre IntelliJ utilise **Java 24** alors que le projet a besoin de **Java 17**.

```
❌ javac 24.0.1 was used to compile java sources
❌ java.lang.ExceptionInInitializerError
```

## ✅ La solution (5 minutes)

### Étape 1: Configurer le JDK du projet

1. **File → Project Structure** (ou Ctrl+Alt+Shift+S)

2. **Project Settings → Project**
   - Cliquer sur le menu déroulant **SDK**
   - Si vous voyez **17** dans la liste → Le sélectionner
   - Si **17** n'est pas dans la liste → Suivre l'étape 1.1

#### 1.1 Télécharger Java 17 (si nécessaire)
   - Dans le menu **SDK**, cliquer sur **Add SDK → Download JDK...**
   - **Version:** 17
   - **Vendor:** Oracle OpenJDK (ou Eclipse Temurin)
   - Cliquer **Download** et attendre

3. Une fois JDK 17 sélectionné:
   - **Language level:** 17 - Sealed types, always-strict floating-point semantics
   - Cliquer **Apply**

### Étape 2: Configurer le compilateur

1. **File → Settings** (ou Ctrl+Alt+S)

2. **Build, Execution, Deployment → Compiler → Java Compiler**
   - **Project bytecode version:** 17
   - Dans le tableau "Per-module bytecode version":
     - vehicle-service → **17**
     - reservation-service → **17**
     - user-service → **17**
     - api-gateway → **17**
   - Cliquer **Apply**

3. **Build, Execution, Deployment → Compiler → Annotation Processors**
   - ✅ Cocher **Enable annotation processing**
   - Cliquer **Apply** et **OK**

### Étape 3: Configurer Maven

1. **File → Settings → Build, Execution, Deployment → Build Tools → Maven**
   - **JRE for importer:** Use Project JDK **(17)**
   - Cliquer **Apply** et **OK**

### Étape 4: Recharger Maven

1. **View → Tool Windows → Maven** (ou cliquer sur l'onglet Maven à droite)
2. Cliquer sur l'icône **🔄 Reload All Maven Projects** (en haut à gauche)
3. Attendre que Maven télécharge les dépendances

### Étape 5: Nettoyer et recompiler

1. **Build → Clean Project**
2. Attendre la fin
3. **Build → Rebuild Project**

### Étape 6: Invalider les caches (Important!)

1. **File → Invalidate Caches / Restart**
2. Cocher **✅ Invalidate and Restart**
3. Attendre le redémarrage d'IntelliJ

## Vérification

Après le redémarrage:

### Test 1: Vérifier le JDK
1. **File → Project Structure → Project**
2. Vérifier: **SDK = 17**

### Test 2: Compiler le projet
1. **Build → Rebuild Project**
2. ✅ Aucune erreur ne doit apparaître

### Test 3: Vérifier Maven
Ouvrir le terminal IntelliJ (Alt+F12):
```bash
mvn -version
```

Résultat attendu:
```
Apache Maven 3.x.x
Maven home: ...
Java version: 17.0.x, vendor: Oracle Corporation
...
```

### Test 4: Compiler un service
Dans le terminal:
```bash
cd vehicle-service
mvn clean compile
```

Résultat attendu:
```
[INFO] BUILD SUCCESS
```

## Si ça marche pas

### Problème: "Cannot find JDK 17"

**Solution:**
1. Télécharger JDK 17 manuellement:
   - https://adoptium.net/temurin/releases/?version=17
   - Installer sur votre système
2. Dans IntelliJ:
   - File → Project Structure → SDKs
   - Cliquer **+** → Add JDK
   - Naviguer vers le dossier d'installation de JDK 17
   - Exemple: `C:\Program Files\Eclipse Adoptium\jdk-17.x.x`

### Problème: Maven utilise toujours Java 24

**Solution:**
1. Vérifier les variables d'environnement Windows:
   - Rechercher "Variables d'environnement"
   - Variable système `JAVA_HOME` → Doit pointer vers JDK 17
   - Exemple: `C:\Program Files\Java\jdk-17`
2. Redémarrer l'ordinateur
3. Relancer IntelliJ

### Problème: L'erreur persiste après tout

**Solution:**
1. Fermer IntelliJ complètement
2. Supprimer le dossier `.idea` du projet
3. Supprimer tous les dossiers `target/` dans chaque service
4. Rouvrir le projet dans IntelliJ
5. Refaire toutes les étapes ci-dessus

## Modifications effectuées dans le projet

Les fichiers suivants ont été automatiquement mis à jour pour Java 17:

✅ **pom.xml** (projet parent):
```xml
<maven.compiler.source>17</maven.compiler.source>
<maven.compiler.target>17</maven.compiler.target>
```

✅ **vehicle-service/pom.xml**:
```xml
<java.version>17</java.version>
<lombok.version>1.18.30</lombok.version>
```

✅ **reservation-service/pom.xml**: Pareil

✅ **user-service/pom.xml**: Pareil

✅ **api-gateway/pom.xml**: Pareil

## Pourquoi Java 17?

| Critère | Java 17 | Java 21 | Java 24 |
|---------|---------|---------|---------|
| Support LTS | ✅ Oui (jusqu'en 2029) | ✅ Oui (jusqu'en 2031) | ❌ Non (6 mois) |
| Spring Boot 3.3.0 | ✅ Parfait | ✅ Bon | ⚠️ Problèmes |
| Lombok 1.18.30 | ✅ Parfait | ✅ Bon | ❌ Incompatible |
| Stabilité | ✅ Excellente | ✅ Bonne | ⚠️ Instable |
| Production | ✅ Recommandé | ✅ OK | ❌ Non |

**Java 17 = Le meilleur choix pour ce projet!**

## Après la correction

Une fois que tout fonctionne:

1. ✅ Le projet compile sans erreur
2. ✅ Vous pouvez démarrer les services
3. ✅ Lombok fonctionne correctement
4. ✅ Maven utilise Java 17

**Vous êtes prêt à développer! 🚀**

## Prochaines étapes

1. Créer les 3 bases de données MySQL (vides)
2. Démarrer les services dans l'ordre:
   - Vehicle Service (8001)
   - User Service (8003)
   - Reservation Service (8002)
   - API Gateway (8000)
3. Vérifier les health checks
4. Tester les APIs via Swagger

**Consultez:** `DEMARRAGE-RAPIDE.md` pour la suite!

## Besoin d'aide?

- Guide détaillé: `FIX-JAVA-VERSION.md`
- Dépannage: `TROUBLESHOOTING.md`
- Documentation complète: `README.md`

Bonne chance! 💪
