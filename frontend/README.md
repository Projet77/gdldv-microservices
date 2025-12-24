# GDLDV Frontend

Frontend React pour les dashboards GDLDV.

## 🚀 Démarrage rapide

### Installation
```bash
npm install
```

### Développement
```bash
npm run dev
```

L'application sera accessible sur `http://localhost:3000`

### Build production
```bash
npm run build
```

### Preview production
```bash
npm run preview
```

## 📁 Structure

```
frontend/
├── src/
│   ├── pages/           # Pages React
│   │   ├── ClientDashboard.tsx
│   │   ├── AgentDashboard.tsx (TODO)
│   │   ├── ManagerDashboard.tsx (TODO)
│   │   └── SuperAdminDashboard.tsx (TODO)
│   ├── services/        # Services API
│   │   ├── api.ts
│   │   └── dashboardService.ts
│   ├── App.tsx
│   ├── main.tsx
│   └── index.css
├── index.html
├── package.json
├── vite.config.ts
├── tailwind.config.js
└── tsconfig.json
```

## 🎨 Technologies

- **React 18** - Framework UI
- **TypeScript** - Typage statique
- **Vite** - Build tool ultra-rapide
- **TailwindCSS** - CSS utility-first
- **Axios** - HTTP client
- **React Router** - Routing
- **Recharts** - Graphiques (pour tendances)
- **Heroicons** - Icônes

## 🔐 Authentification

Le token JWT est stocké dans `localStorage` et automatiquement ajouté aux headers via l'intercepteur axios.

## 📡 API

Les appels API sont proxifiés via Vite vers `http://localhost:8003` en développement.

## 🎯 Dashboards

### Implémentés
- ✅ Client Dashboard - Complet

### À implémenter
- ⏳ Agent Dashboard
- ⏳ Manager Dashboard
- ⏳ Super Admin Dashboard

## 🛠️ Développement

### Ajouter un nouveau dashboard

1. Créer le composant dans `src/pages/`
2. Ajouter les types dans `src/services/dashboardService.ts`
3. Ajouter la route dans `App.tsx`
4. Tester !
