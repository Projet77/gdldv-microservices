import api from './api';

export const vehicleService = {
    getAllVehicles: async () => {
        const response = await api.get('/vehicles');
        return response.data;
    },

    getVehicleById: async (id: number) => {
        const response = await api.get(`/vehicles/${id}`);
        return response.data;
    },
};
