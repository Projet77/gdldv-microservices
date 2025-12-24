import api from './api';

export interface ClientDashboard {
  userId: number;
  firstName: string;
  lastName: string;
  email: string;
  memberSince: string;
  membershipBadge: string;
  averageRating: number;
  activeRentals: number;
  totalRentals: number;
  totalSpent: number;
  averageSpentPerRental: number;
  averageDuration: number;
  favoriteCategory: string;
  currentRentals: ActiveRental[];
  recentHistory: RentalHistory[];
  favorites: FavoriteVehicle[];
  recentPayments: Payment[];
  monthlyTotal: number;
  yearlyTotal: number;
}

export interface ActiveRental {
  reservationId: number;
  confirmationNumber: string;
  vehicleId: number;
  vehicleName: string;
  vehicleImage: string;
  startDate: string;
  endDate: string;
  pickupLocation: string;
  returnLocation: string;
  dailyPrice: number;
  totalPrice: number;
  status: string;
  daysRemaining: number;
  hoursRemaining: number;
}

export interface RentalHistory {
  reservationId: number;
  confirmationNumber: string;
  vehicleName: string;
  startDate: string;
  endDate: string;
  totalPrice: number;
  status: string;
  rating: number;
  review: string;
}

export interface FavoriteVehicle {
  vehicleId: number;
  name: string;
  image: string;
  dailyPrice: number;
  averageRating: number;
  available: boolean;
}

export interface Payment {
  paymentId: number;
  paymentDate: string;
  description: string;
  amount: number;
  status: string;
  confirmationNumber: string;
}

// CLIENT Dashboard
export const getClientDashboard = async (userId: number): Promise<ClientDashboard> => {
  const response = await api.get(`/api/client/dashboard?userId=${userId}`);
  return response.data;
};

export const getActiveRentals = async (userId: number): Promise<ActiveRental[]> => {
  const response = await api.get(`/api/client/active-rentals?userId=${userId}`);
  return response.data;
};

export const getRentalHistory = async (userId: number): Promise<RentalHistory[]> => {
  const response = await api.get(`/api/client/rental-history?userId=${userId}`);
  return response.data;
};

export const getFavorites = async (userId: number): Promise<FavoriteVehicle[]> => {
  const response = await api.get(`/api/client/favorites?userId=${userId}`);
  return response.data;
};

export const getStatistics = async (userId: number) => {
  const response = await api.get(`/api/client/statistics?userId=${userId}`);
  return response.data;
};

// AGENT Dashboard
export const getAgentDashboard = async (agentId: number) => {
  const response = await api.get(`/api/agent/dashboard?agentId=${agentId}`);
  return response.data;
};

// MANAGER Dashboard
export const getManagerDashboard = async () => {
  const response = await api.get('/api/manager/dashboard');
  return response.data;
};

export const getManagerKPIs = async () => {
  const response = await api.get('/api/manager/kpis');
  return response.data;
};

// SUPER_ADMIN Dashboard
export const getSuperAdminDashboard = async () => {
  const response = await api.get('/api/super-admin/dashboard');
  return response.data;
};

export const getSystemHealth = async () => {
  const response = await api.get('/api/super-admin/system-health');
  return response.data;
};
