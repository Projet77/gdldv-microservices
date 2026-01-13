import api from './api';

export const vehicleService = {
    getAllVehicles: async () => {
        const response = await api.get('/api/v1/vehicles');
        return response.data;
    },

    getVehicleById: async (id: number) => {
        const response = await api.get(`/api/v1/vehicles/${id}`);
        return response.data;
    },

    /**
     * Récupère les véhicules filtrés par statut
     * @param status Statut du véhicule (AVAILABLE, RENTED, MAINTENANCE, etc.)
     */
    getVehiclesByStatus: async (status: string) => {
        console.log('🚗 [VehicleService] Fetching vehicles by status:', status);
        const response = await api.get(`/api/v1/vehicles/status/${status}`);
        return response.data;
    },

    /**
     * Récupère les véhicules filtrés par catégorie
     * @param category Catégorie du véhicule
     */
    getVehiclesByCategory: async (category: string) => {
        console.log('🚗 [VehicleService] Fetching vehicles by category:', category);
        const response = await api.get(`/api/v1/vehicles/category/${category}`);
        return response.data;
    },

    /**
     * Récupère les véhicules les plus populaires/tendance
     * @param count Nombre de véhicules à récupérer (défaut: 10)
     */
    getTrendingVehicles: async (count: number = 10) => {
        console.log('📈 [VehicleService] Fetching trending vehicles, count:', count);
        const response = await api.get(`/api/v1/vehicles/recommendations/trending?count=${count}`);
        return response.data;
    },

    /**
     * Récupère les véhicules les mieux notés
     */
    getTopRatedVehicles: async () => {
        console.log('⭐ [VehicleService] Fetching top-rated vehicles');
        const response = await api.get('/api/v1/vehicles/search/top-rated');
        return response.data;
    },

    /**
     * Récupère les véhicules les plus populaires
     */
    getPopularVehicles: async () => {
        console.log('🔥 [VehicleService] Fetching popular vehicles');
        const response = await api.get('/api/v1/vehicles/search/popular');
        return response.data;
    },

    /**
     * Crée un nouveau véhicule
     * @param data Données du véhicule à créer
     */
    createVehicle: async (data: any) => {
        console.log('➕ [VehicleService] Creating vehicle:', data.brand, data.model);
        const response = await api.post('/api/v1/vehicles', data);
        console.log('✅ [VehicleService] Vehicle created with ID:', response.data.id);
        return response.data;
    },

    /**
     * Met à jour un véhicule existant
     * @param id ID du véhicule
     * @param data Nouvelles données du véhicule
     */
    updateVehicle: async (id: number, data: any) => {
        console.log('✏️ [VehicleService] Updating vehicle:', id);
        const response = await api.put(`/api/v1/vehicles/${id}`, data);
        console.log('✅ [VehicleService] Vehicle updated');
        return response.data;
    },

    /**
     * Supprime un véhicule
     * @param id ID du véhicule à supprimer
     */
    deleteVehicle: async (id: number) => {
        console.log('🗑️ [VehicleService] Deleting vehicle:', id);
        await api.delete(`/api/v1/vehicles/${id}`);
        console.log('✅ [VehicleService] Vehicle deleted');
    },
};
