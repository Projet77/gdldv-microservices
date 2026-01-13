import api from './api';

// Types
export interface ManagerDashboard {
  todayReservations: number;
  activeRentals: number;
  pendingApprovals: number;
  teamMembers: number;
  weeklyRevenue: number;
  monthlyRevenue: number;
  completionRate: number;
  avgResponseTime: number;
  recentActivity: RecentActivity[];
  topPerformers: TeamMember[];
}

export interface RecentActivity {
  id: number;
  type: string;
  description: string;
  timestamp: string;
  status: string;
}

export interface TeamMember {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: string;
  active: boolean;
  totalReservations?: number;
  rating?: number;
}

export const managerService = {
  /**
   * Récupère le dashboard manager avec toutes les statistiques
   */
  getManagerDashboard: async (): Promise<ManagerDashboard> => {
    console.log('📊 [ManagerService] Fetching manager dashboard...');
    const response = await api.get('/api/manager/dashboard');
    console.log('✅ [ManagerService] Manager dashboard loaded:', response.data);
    return response.data;
  },

  /**
   * Récupère les membres de l'équipe
   */
  getTeamMembers: async (): Promise<TeamMember[]> => {
    console.log('👥 [ManagerService] Fetching team members...');
    const response = await api.get('/api/manager/team');
    console.log('✅ [ManagerService] Team members loaded:', response.data);
    return response.data;
  },

  /**
   * Approuve une réservation
   */
  approveReservation: async (reservationId: number): Promise<void> => {
    console.log('✅ [ManagerService] Approving reservation:', reservationId);
    await api.patch(`/api/manager/reservations/${reservationId}/approve`);
    console.log('✅ [ManagerService] Reservation approved');
  },

  /**
   * Rejette une réservation
   */
  rejectReservation: async (reservationId: number, reason: string): Promise<void> => {
    console.log('❌ [ManagerService] Rejecting reservation:', reservationId);
    await api.patch(`/api/manager/reservations/${reservationId}/reject`, { reason });
    console.log('✅ [ManagerService] Reservation rejected');
  },

  /**
   * Assigne une réservation à un agent
   */
  assignReservation: async (reservationId: number, agentId: number): Promise<void> => {
    console.log('👤 [ManagerService] Assigning reservation to agent:', { reservationId, agentId });
    await api.patch(`/api/manager/reservations/${reservationId}/assign`, { agentId });
    console.log('✅ [ManagerService] Reservation assigned');
  },

  /**
   * Génère un rapport
   */
  generateReport: async (startDate: string, endDate: string): Promise<any> => {
    console.log('📄 [ManagerService] Generating report:', { startDate, endDate });
    const response = await api.post('/api/manager/reports/generate', { startDate, endDate });
    console.log('✅ [ManagerService] Report generated');
    return response.data;
  },
};
