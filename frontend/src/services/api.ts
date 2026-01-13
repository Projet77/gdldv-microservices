import axios from 'axios';

// Hardcode Gateway URL to prevent misconfiguration
const API_BASE_URL = 'http://localhost:8000';

console.log('🚀 API Configuration:');
console.log('  - API_BASE_URL used:', API_BASE_URL);

// Créer une instance axios avec configuration
const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000, // Augmenté à 30 secondes car le backend est lent
  headers: {
    'Content-Type': 'application/json',
  },
});

// Intercepteur pour ajouter le token JWT
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      console.log('🔑 [API] Attaching Token:', token.substring(0, 15) + '...');
      config.headers.Authorization = `Bearer ${token}`;
    } else {
      console.warn('⚠️ [API] No Token found in localStorage');
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Intercepteur pour gérer les erreurs
api.interceptors.response.use(
  (response) => {
    console.log('✅ [API] Response OK:', response.config.url);
    return response;
  },
  (error) => {
    const status = error.response?.status;
    const url = error.config?.url;

    console.error('❌ [API] Request Failed:', {
      url,
      status,
      message: error.response?.data?.message || error.message,
      data: error.response?.data // Log full data part to catch custom error fields like "error": "msg"
    });

    // Ne déconnecter que pour les erreurs d'authentification sur les endpoints protégés
    // Éviter de déconnecter si c'est juste un endpoint qui n'existe pas encore
    if (status === 401) {
      const isAuthEndpoint = url?.includes('/api/auth/');

      // Si c'est un endpoint d'auth qui échoue, c'est un vrai problème d'auth
      if (isAuthEndpoint) {
        console.warn('⚠️ [API] Auth endpoint failed - logging out');
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = '/login';
      } else {
        // Pour les autres endpoints, logger mais laisser la page gérer l'erreur
        console.warn('⚠️ [API] 401 on protected endpoint:', url);
        console.warn('⚠️ Token may be invalid or endpoint requires different permissions');

        // Vérifier si le token existe encore
        const token = localStorage.getItem('token');
        if (!token) {
          console.error('❌ [API] No token found - redirecting to login');
          window.location.href = '/login';
        }
      }
    }

    return Promise.reject(error);
  }
);

export default api;
