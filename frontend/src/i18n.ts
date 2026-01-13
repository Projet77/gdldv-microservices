import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
// We don't use the detector plugin automatically here because we want to sync with our custom localStorage 'user' object manually or via a simple detector wrapper if needed. 
// But simpler: let's just read localStorage 'user' directly for the initial state.

const getUserLanguage = () => {
    try {
        const userStr = localStorage.getItem('user');
        if (userStr) {
            const user = JSON.parse(userStr);
            return user.language || 'FR';
        }
    } catch (e) {
        console.error("Error reading user language", e);
    }
    return 'FR';
};

const resources = {
    FR: {
        translation: {
            // Dashboard Layout
            "dashboard": "Tableau de bord",
            "my_reservations": "Mes Réservations",
            "history": "Historique",
            "favorites": "Favoris",
            "profile": "Mon Profil",
            "overview": "Aperçu",
            "fleet": "Flotte",
            "clients": "Clients",
            "maintenance": "Maintenance",
            "reports": "Rapports",
            "agent_home": "Accueil Agent",
            "operations": "Départs / Retours",
            "performance": "Performance",
            "team": "Équipe",
            "incidents": "Incidents",
            "finance": "Finances",
            "system_view": "Vue Système",
            "users": "Utilisateurs",
            "security": "Sécurité",
            "configuration": "Configuration",
            "logout": "Déconnexion",
            "search": "Rechercher...",
            "hello": "Bonjour",
            "account": "Compte",
            "view_profile": "Voir le profil",

            // SuperAdminConfig Page
            "config_title": "Configuration",
            "config_subtitle": "Personnalisez votre expérience et les paramètres système.",
            "personal_prefs": "Préférences Personnelles",
            "language": "Langue",
            "currency": "Devise",
            "save_prefs": "Enregistrer les préférences",
            "saving": "Sauvegarde...",
            "loyalty_program": "Programme de Fidélité",
            "loyalty_desc": "Les clients ayant effectué plus de <1>5 réservations</1> sont considérés comme fidèles.",
            "discount_percent": "Réduction (%)",
            "discount_hint": "Ce pourcentage sera automatiquement déduit du total pour les clients fidèles.",
            "update_loyalty": "Mettre à jour la réduction",
            "updating": "Mise à jour...",
            "success_prefs": "Préférences sauvegardées !",
            "success_loyalty": "Configuration fidélité mise à jour !",
            "error_save": "Erreur lors de la sauvegarde."
        }
    },
    EN: {
        translation: {
            "dashboard": "Dashboard",
            "my_reservations": "My Reservations",
            "history": "History",
            "favorites": "Favorites",
            "profile": "My Profile",
            "overview": "Overview",
            "fleet": "Fleet",
            "clients": "Clients",
            "maintenance": "Maintenance",
            "reports": "Reports",
            "agent_home": "Agent Home",
            "operations": "Departures / Returns",
            "performance": "Performance",
            "team": "Team",
            "incidents": "Incidents",
            "finance": "Finance",
            "system_view": "System View",
            "users": "Users",
            "security": "Security",
            "configuration": "Configuration",
            "logout": "Logout",
            "search": "Search...",
            "hello": "Hello",
            "account": "Account",
            "view_profile": "View Profile",

            "config_title": "Configuration",
            "config_subtitle": "Customize your experience and system settings.",
            "personal_prefs": "Personal Preferences",
            "language": "Language",
            "currency": "Currency",
            "save_prefs": "Save Preferences",
            "saving": "Saving...",
            "loyalty_program": "Loyalty Program",
            "loyalty_desc": "Clients with more than <1>5 reservations</1> are considered loyal.",
            "discount_percent": "Discount (%)",
            "discount_hint": "This percentage will be automatically deducted for loyal clients.",
            "update_loyalty": "Update Discount",
            "updating": "Updating...",
            "success_prefs": "Preferences saved!",
            "success_loyalty": "Loyalty config updated!",
            "error_save": "Error saving preferences."
        }
    },
    ES: {
        translation: {
            "dashboard": "Panel de control",
            "my_reservations": "Mis Reservas",
            "history": "Historial",
            "favorites": "Favoritos",
            "profile": "Mi Perfil",
            "overview": "Resumen",
            "fleet": "Flota",
            "clients": "Clientes",
            "maintenance": "Mantenimiento",
            "reports": "Informes",
            "agent_home": "Inicio Agente",
            "operations": "Salidas / Retornos",
            "performance": "Rendimiento",
            "team": "Equipo",
            "incidents": "Incidentes",
            "finance": "Finanzas",
            "system_view": "Vista del Sistema",
            "users": "Usuarios",
            "security": "Seguridad",
            "configuration": "Configuración",
            "logout": "Cerrar Sesión",
            "search": "Buscar...",
            "hello": "Hola",
            "account": "Cuenta",
            "view_profile": "Ver Perfil",

            "config_title": "Configuración",
            "config_subtitle": "Personaliza tu experiencia y configuración del sistema.",
            "personal_prefs": "Preferencias Personales",
            "language": "Idioma",
            "currency": "Moneda",
            "save_prefs": "Guardar Preferencias",
            "saving": "Guardando...",
            "loyalty_program": "Programa de Lealtad",
            "loyalty_desc": "Los clientes con más de <1>5 reservas</1> se consideran leales.",
            "discount_percent": "Descuento (%)",
            "discount_hint": "Este porcentaje se deducirá automáticamente para clientes leales.",
            "update_loyalty": "Actualizar Descuento",
            "updating": "Actualizando...",
            "success_prefs": "¡Preferencias guardadas!",
            "success_loyalty": "¡Configuración de lealtad actualizada!",
            "error_save": "Error al guardar."
        }
    }
};

i18n
    .use(initReactI18next)
    .init({
        resources,
        lng: getUserLanguage(), // Initialize with user preference
        fallbackLng: "FR",
        interpolation: {
            escapeValue: false // react already safes from xss
        }
    });

export default i18n;
