# 🚀 Démarrage Rapide - GDLDV avec Docker

## ⚡ En 3 étapes

### 1️⃣ Vérifier les prérequis

```bash
# Docker Desktop doit être démarré
docker ps
```

Si erreur → Démarrez Docker Desktop et attendez qu'il soit prêt (icône verte).

---

### 2️⃣ Lancer tous les services

**Windows :**
```bash
.\start-all.bat
```

**Ou manuellement :**
```bash
docker-compose -f docker-compose-full.yml up -d
```

⏱️ **Première fois** : 10-15 minutes (téléchargement + build)
⏱️ **Ensuite** : 3-5 minutes

---

### 3️⃣ Vérifier que tout fonctionne

Attendez 5 minutes puis ouvrez :

- ✅ **Frontend** : http://localhost:3000
- ✅ **Eureka Dashboard** : http://localhost:8761
- ✅ **Page de connexion** : http://localhost:8003/user-service/login

---

## 🎯 Commandes utiles

```bash
# Voir les logs
docker-compose -f docker-compose-full.yml logs -f

# Voir l'état des services
docker-compose -f docker-compose-full.yml ps

# Arrêter tout
docker-compose -f docker-compose-full.yml down
```

---

## ❌ En cas de problème

### Le build échoue ?
```bash
# Nettoyer et recommencer
docker-compose -f docker-compose-full.yml down -v
docker system prune -a
docker-compose -f docker-compose-full.yml up -d --build
```

### Un service ne démarre pas ?
```bash
# Voir les logs du service
docker-compose -f docker-compose-full.yml logs user-service
```

### Erreur de mémoire ?
- Docker Desktop → Settings → Resources → **Memory: 8 GB minimum**

---

## 📚 Documentation complète

Pour plus de détails → [GUIDE-DEPLOIEMENT-DOCKER.md](./GUIDE-DEPLOIEMENT-DOCKER.md)

---

**🎉 C'est tout ! Votre application est prête.**
