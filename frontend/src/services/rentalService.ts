import api from './api';

export const rentalService = {
    getAllRentals: async () => {
        const response = await api.get('/rentals');
        return response.data;
    },
};
