import api from './api';

// Types
export interface AdminDashboard {
  todayReservations: number;
  activeRentals: number;
  pendingPayments: number;
  totalUsers: number;
  todayRevenue: number;
  urgentTasks: UrgentTask[];
}

export interface UrgentTask {
  type: string;
  reservationId: number;
  description: string;
  priority: 'HIGH' | 'MEDIUM' | 'LOW';
}

export const adminService = {
  /**
   * Récupère le dashboard admin avec toutes les statistiques
   * @returns AdminDashboard avec KPIs et tâches urgentes
   */
  getAdminDashboard: async (): Promise<AdminDashboard> => {
    console.log('📊 [AdminService] Fetching admin dashboard...');
    const response = await api.get('/api/admin/dashboard');
    console.log('✅ [AdminService] Admin dashboard loaded:', response.data);
    return response.data;
  },

  /**
   * Approuve un remboursement pour une réservation
   * @param reservationId ID de la réservation
   */
  approveRefund: async (reservationId: number): Promise<void> => {
    console.log('💰 [AdminService] Approving refund for reservation:', reservationId);
    await api.post(`/api/admin/approve-refund/${reservationId}`);
    console.log('✅ [AdminService] Refund approved');
  },

  /**
   * Envoie un rappel à un client pour une réservation
   * @param reservationId ID de la réservation
   */
  sendReminderToCustomer: async (reservationId: number): Promise<void> => {
    console.log('📧 [AdminService] Sending reminder for reservation:', reservationId);
    await api.post(`/api/admin/send-reminder/${reservationId}`);
    console.log('✅ [AdminService] Reminder sent');
  },
};
