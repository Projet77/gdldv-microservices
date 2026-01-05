import api from './api';

const mapRole = (backendRole: string): string => {
    // Backend roles are like "ROLE_SUPER_ADMIN", frontend expects "SUPER_ADMIN"
    return backendRole.replace('ROLE_', '');
};

export const authService = {
    login: async (credentials: any) => {
        console.log('🔐 Tentative de connexion avec:', credentials.email);
        console.log('🌐 API Base URL:', import.meta.env.VITE_API_URL || 'http://localhost:8003/user-service');
        console.log('📡 Full URL:', `${import.meta.env.VITE_API_URL || 'http://localhost:8003/user-service'}/api/auth/login`);

        const response = await api.post('/api/auth/login', credentials);
        if (response.data.token) {
            localStorage.setItem('token', response.data.token);
            const user = response.data.user;

            // Store user data with primary role
            const userData = {
                ...user,
                token: response.data.token,
                role: user.roles && user.roles.length > 0
                    ? mapRole(user.roles[0].name) // Roles are objects now {id, name}
                    : 'CLIENT',
                name: `${user.firstName || ''} ${user.lastName || ''}`.trim()
            };
            localStorage.setItem('user', JSON.stringify(userData));
        }
        return response.data;
    },

    register: async (userData: any) => {
        const response = await api.post('/api/auth/register', userData);
        if (response.data.token) {
            localStorage.setItem('token', response.data.token);
            const user = response.data.user;

            // Store user data with primary role
            const userDataWithRole = {
                ...user,
                token: response.data.token,
                role: user.roles && user.roles.length > 0
                    ? mapRole(user.roles[0].name)
                    : 'CLIENT',
                name: `${user.firstName || ''} ${user.lastName || ''}`.trim()
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
