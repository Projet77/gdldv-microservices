import api from './api';

export const userService = {
    /**
     * Récupère tous les utilisateurs (Admin)
     */
    getAllUsers: async () => {
        console.log('👥 [UserService] Fetching all users');
        const response = await api.get('/api/users');
        console.log('✅ [UserService] Loaded', response.data.length, 'users');
        return response.data.data;
    },

    /**
     * Récupère un utilisateur par son ID
     * @param id ID de l'utilisateur
     */
    getUserById: async (id: number) => {
        console.log('🔍 [UserService] Fetching user:', id);
        const response = await api.get(`/api/users/${id}`);
        return response.data;
    },

    /**
     * Met à jour le profil d'un utilisateur
     * @param id ID de l'utilisateur
     * @param data Nouvelles données du profil
     */
    updateProfile: async (id: number, data: any) => {
        console.log('✏️ [UserService] Updating user profile:', id);
        const response = await api.put(`/api/users/${id}`, data);
        console.log('✅ [UserService] Profile updated');
        return response.data;
    },

    /**
     * Récupère les utilisateurs filtrés par rôle
     * @param role Rôle (CLIENT, ADMIN, AGENT, MANAGER, SUPER_ADMIN)
     */
    getUsersByRole: async (role: string) => {
        console.log('👥 [UserService] Fetching users by role:', role);
        const response = await api.get(`/api/users/role/${role}`);
        return response.data;
    },

    /**
     * Récupère les utilisateurs archivés/désactivés
     */
    getArchivedUsers: async () => {
        console.log('🗃️ [UserService] Fetching archived users');
        const response = await api.get('/api/users/archived');
        return response.data;
    },

    /**
     * Crée un nouvel utilisateur (Admin)
     * @param data Données de l'utilisateur à créer
     */
    createUser: async (data: any) => {
        console.log('➕ [UserService] Creating user:', data.email);
        const response = await api.post('/api/users', data);
        console.log('✅ [UserService] User created with ID:', response.data.id);
        return response.data;
    },

    /**
     * Met à jour un utilisateur (Admin)
     * @param id ID de l'utilisateur
     * @param data Nouvelles données de l'utilisateur
     */
    updateUser: async (id: number, data: any) => {
        console.log('✏️ [UserService] Updating user:', id);
        const response = await api.put(`/api/users/${id}`, data);
        console.log('✅ [UserService] User updated');
        return response.data;
    },

    /**
     * Active un utilisateur
     * @param id ID de l'utilisateur
     */
    activateUser: async (id: number) => {
        console.log('✅ [UserService] Activating user:', id);
        const response = await api.patch(`/api/users/${id}/activate`);
        console.log('✅ [UserService] User activated');
        return response.data;
    },

    /**
     * Désactive/Supprime un utilisateur
     * @param id ID de l'utilisateur
     */
    deleteUser: async (id: number) => {
        console.log('🗑️ [UserService] Deleting user:', id);
        await api.delete(`/api/users/${id}`);
        console.log('✅ [UserService] User deleted');
    },
};
