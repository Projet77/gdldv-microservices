import api from './api';

export const userService = {
    getAllUsers: async () => {
        const response = await api.get('/users');
        return response.data;
    },

    getUserById: async (id: number) => {
        const response = await api.get(`/users/${id}`);
        return response.data;
    },

    updateProfile: async (id: number, data: any) => {
        const response = await api.put(`/users/${id}`, data);
        return response.data;
    },
};
