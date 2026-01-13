import api from './api';

const mapRole = (backendRole: string): string => {
    // Backend roles are like "ROLE_SUPER_ADMIN", frontend expects "SUPER_ADMIN"
    return backendRole.replace('ROLE_', '');
};

export const authService = {
    login: async (credentials: any) => {
        console.log('🔐 Tentative de connexion avec:', credentials.email);

        const response = await api.post('/api/auth/login', credentials);
        if (response.data.token) {
            localStorage.setItem('token', response.data.token);
            // Backend returns flat structure: { token, email, roles: string[], ... }
            const userData = {
                id: response.data.id,
                email: response.data.email,
                firstName: response.data.firstName,
                lastName: response.data.lastName,
                token: response.data.token,
                role: response.data.roles && response.data.roles.length > 0
                    ? mapRole(response.data.roles[0]) // Roles are strings strings ["ROLE_ADMIN"]
                    : 'CLIENT',
                name: `${response.data.firstName || ''} ${response.data.lastName || ''}`.trim()
            };
            localStorage.setItem('user', JSON.stringify(userData));
        }
        return response.data;
    },

    register: async (userData: any) => {
        const response = await api.post('/api/auth/register', userData);
        if (response.data.token) {
            localStorage.setItem('token', response.data.token);
            // Backend returns flat structure
            const userDataWithRole = {
                id: response.data.id,
                email: response.data.email,
                firstName: response.data.firstName,
                lastName: response.data.lastName,
                token: response.data.token,
                role: response.data.roles && response.data.roles.length > 0
                    ? mapRole(response.data.roles[0])
                    : 'CLIENT',
                name: `${response.data.firstName || ''} ${response.data.lastName || ''}`.trim()
            };
            localStorage.setItem('user', JSON.stringify(userDataWithRole));
        }
        return response.data;
    },

    logout: () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = '/login';
    },

    getCurrentUser: () => {
        const userStr = localStorage.getItem('user');
        if (userStr) return JSON.parse(userStr);
        return null;
    },
};
