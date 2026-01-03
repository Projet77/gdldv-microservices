import api from './api';

export const reservationService = {
    createReservation: async (data: any) => {
        const response = await api.post('/reservations', data);
        return response.data;
    },

    getMyReservations: async () => {
        const response = await api.get('/reservations/my');
        return response.data;
    },
};
