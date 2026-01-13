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

    /**
     * Récupère toutes les réservations (Admin)
     */
    getAllReservations: async () => {
        console.log('📋 [ReservationService] Fetching all reservations');
        const response = await api.get('/api/reservations');
        console.log('✅ [ReservationService] Loaded', response.data.length, 'reservations');
        return response.data;
    },

    /**
     * Récupère une réservation par son ID
     * @param id ID de la réservation
     */
    getReservationById: async (id: number) => {
        console.log('🔍 [ReservationService] Fetching reservation:', id);
        const response = await api.get(`/api/reservations/${id}`);
        return response.data;
    },

    /**
     * Récupère les réservations filtrées par statut
     * @param status Statut de la réservation (PENDING, CONFIRMED, ACTIVE, COMPLETED, CANCELLED)
     */
    getReservationsByStatus: async (status: string) => {
        console.log('📋 [ReservationService] Fetching reservations by status:', status);
        const response = await api.get(`/api/reservations/status/${status}`);
        return response.data;
    },

    /**
     * Récupère les réservations d'un utilisateur spécifique
     * @param userId ID de l'utilisateur
     */
    getReservationsByUser: async (userId: number) => {
        console.log('👤 [ReservationService] Fetching reservations for user:', userId);
        const response = await api.get(`/api/reservations/user/${userId}`);
        return response.data;
    },

    /**
     * Récupère les réservations pour un véhicule spécifique
     * @param vehicleId ID du véhicule
     */
    getReservationsByVehicle: async (vehicleId: number) => {
        console.log('🚗 [ReservationService] Fetching reservations for vehicle:', vehicleId);
        const response = await api.get(`/api/reservations/vehicle/${vehicleId}`);
        return response.data;
    },

    /**
     * Confirme une réservation
     * @param id ID de la réservation
     */
    confirmReservation: async (id: number) => {
        console.log('✅ [ReservationService] Confirming reservation:', id);
        const response = await api.patch(`/api/reservations/${id}/confirm`);
        console.log('✅ [ReservationService] Reservation confirmed');
        return response.data;
    },

    /**
     * Démarre une réservation (Remise véhicule)
     * @param id ID de la réservation
     */
    startReservation: async (id: number) => {
        console.log('🚀 [ReservationService] Starting reservation:', id);
        const response = await api.patch(`/api/reservations/${id}/start`);
        console.log('✅ [ReservationService] Reservation started');
        return response.data;
    },

    /**
     * Annule une réservation
     * @param id ID de la réservation
     */
    cancelReservation: async (id: number) => {
        console.log('❌ [ReservationService] Cancelling reservation:', id);
        const response = await api.patch(`/api/reservations/${id}/cancel`);
        console.log('✅ [ReservationService] Reservation cancelled');
        return response.data;
    },

    /**
     * Marque une réservation comme complétée
     * @param id ID de la réservation
     */
    completeReservation: async (id: number) => {
        console.log('🏁 [ReservationService] Completing reservation:', id);
        const response = await api.patch(`/api/reservations/${id}/complete`);
        console.log('✅ [ReservationService] Reservation completed');
        return response.data;
    },
};
