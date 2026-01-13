import api from './api';

export const rentalService = {
    getAllRentals: async () => {
        const response = await api.get('/api/rentals');
        return response.data;
    },
};
