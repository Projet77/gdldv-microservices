import api from './api';

// ==================== TYPES ====================

export interface SuperAdminDashboard {
  systemHealth: SystemHealth;
  userStatistics: UserStatistics;
  auditStatistics: AuditStatistics;
  databaseStatistics: DatabaseStatistics;
  performanceMetrics: PerformanceMetrics;
}

export interface SystemHealth {
  services: ServiceStatus[];
  overallStatus: 'HEALTHY' | 'DEGRADED' | 'DOWN';
  lastCheck: string;
}

export interface ServiceStatus {
  name: string;
  status: 'UP' | 'DOWN';
  responseTime: number;
  lastCheck: string;
  url?: string;
}

export interface UserStatistics {
  totalUsers: number;
  activeUsers: number;
  inactiveUsers: number;
  newUsersToday: number;
  newUsersThisWeek: number;
  newUsersThisMonth: number;
  usersByRole: { [role: string]: number };
}

export interface AuditStatistics {
  totalLogs: number;
  recentLogs: AuditLog[];
  logsByAction: { [action: string]: number };
  logsByUser: { [userId: string]: number };
}

export interface AuditLog {
  id: number;
  userId: number;
  userName?: string;
  action: string;
  entityType: string;
  entityId: number;
  ipAddress: string;
  timestamp: string;
  details?: string;
}

export interface DatabaseStatistics {
  totalTables: number;
  totalRecords: number;
  databaseSize: string;
  tableStatistics: TableStatistic[];
}

export interface TableStatistic {
  tableName: string;
  recordCount: number;
  sizeInMB: number;
}

export interface PerformanceMetrics {
  averageResponseTime: number;
  requestsPerMinute: number;
  errorRate: number;
  cpuUsage: number;
  memoryUsage: number;
  diskUsage: number;
}

export interface SecurityMetrics {
  failedLoginAttempts: number;
  blockedIPs: string[];
  suspiciousActivities: SuspiciousActivity[];
  securityAlerts: SecurityAlert[];
  recentFailedLogins: FailedLogin[];
}

export interface SuspiciousActivity {
  id: number;
  type: string;
  userId?: number;
  ipAddress: string;
  timestamp: string;
  description: string;
  severity: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
}

export interface SecurityAlert {
  id: number;
  type: string;
  severity: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  message: string;
  timestamp: string;
  resolved: boolean;
}

export interface FailedLogin {
  id: number;
  username: string;
  ipAddress: string;
  timestamp: string;
  reason: string;
}

// ==================== SERVICE ====================

export const superAdminService = {
  /**
   * Récupérer le dashboard complet du Super Admin
   */
  getSuperAdminDashboard: async (): Promise<SuperAdminDashboard> => {
    console.log('🔒 [SuperAdminService] Fetching super admin dashboard...');
    const response = await api.get('/api/super-admin/dashboard');
    console.log('✅ [SuperAdminService] Dashboard loaded:', response.data);
    return response.data;
  },

  /**
   * Récupérer la santé du système
   */
  getSystemHealth: async (): Promise<SystemHealth> => {
    console.log('🏥 [SuperAdminService] Checking system health...');
    const response = await api.get('/api/super-admin/system-health');
    console.log('✅ [SuperAdminService] System health:', response.data);
    return response.data;
  },

  /**
   * Récupérer les statistiques utilisateurs
   */
  getUserStatistics: async (): Promise<UserStatistics> => {
    console.log('👥 [SuperAdminService] Fetching user statistics...');
    const response = await api.get('/api/super-admin/user-statistics');
    console.log('✅ [SuperAdminService] User statistics loaded:', response.data);
    return response.data;
  },

  /**
   * Récupérer les métriques de sécurité
   */
  getSecurityMetrics: async (): Promise<SecurityMetrics> => {
    console.log('🔐 [SuperAdminService] Fetching security metrics...');
    const response = await api.get('/api/super-admin/security-metrics');
    console.log('✅ [SuperAdminService] Security metrics loaded:', response.data);
    return response.data;
  },

  /**
   * Récupérer les statistiques d'audit
   */
  getAuditStatistics: async (): Promise<AuditStatistics> => {
    console.log('📋 [SuperAdminService] Fetching audit statistics...');
    const response = await api.get('/api/super-admin/audit-statistics');
    console.log('✅ [SuperAdminService] Audit statistics loaded:', response.data);
    return response.data;
  },

  /**
   * Récupérer les statistiques de la base de données
   */
  getDatabaseStatistics: async (): Promise<DatabaseStatistics> => {
    console.log('💾 [SuperAdminService] Fetching database statistics...');
    const response = await api.get('/api/super-admin/database-statistics');
    console.log('✅ [SuperAdminService] Database statistics loaded:', response.data);
    return response.data;
  },

  /**
   * Récupérer les métriques de performance
   */
  getPerformanceMetrics: async (): Promise<PerformanceMetrics> => {
    console.log('⚡ [SuperAdminService] Fetching performance metrics...');
    const response = await api.get('/api/super-admin/performance-metrics');
    console.log('✅ [SuperAdminService] Performance metrics loaded:', response.data);
    return response.data;
  },

  /**
   * Récupérer les logs d'audit avec pagination
   */
  getAuditLogs: async (page: number = 0, size: number = 20, sort: string = 'timestamp,desc'): Promise<{ content: AuditLog[], totalElements: number, totalPages: number }> => {
    console.log(`📜 [SuperAdminService] Fetching audit logs (page ${page}, size ${size})...`);
    const response = await api.get('/api/super-admin/audit-logs', {
      params: { page, size, sort }
    });
    console.log('✅ [SuperAdminService] Audit logs loaded:', response.data.content?.length || 0, 'logs');
    return response.data;
  },

  /**
   * Bloquer une adresse IP
   */
  blockIP: async (ipAddress: string, reason: string): Promise<void> => {
    console.log(`🚫 [SuperAdminService] Blocking IP ${ipAddress}...`);
    await api.post('/api/super-admin/block-ip', { ipAddress, reason });
    console.log('✅ [SuperAdminService] IP blocked successfully');
  },

  /**
   * Débloquer une adresse IP
   */
  unblockIP: async (ipAddress: string): Promise<void> => {
    console.log(`✅ [SuperAdminService] Unblocking IP ${ipAddress}...`);
    await api.post('/api/super-admin/unblock-ip', { ipAddress });
    console.log('✅ [SuperAdminService] IP unblocked successfully');
  },

  /**
   * Résoudre une alerte de sécurité
   */
  resolveSecurityAlert: async (alertId: number): Promise<void> => {
    console.log(`✔️ [SuperAdminService] Resolving security alert ${alertId}...`);
    await api.post(`/api/super-admin/resolve-alert/${alertId}`);
    console.log('✅ [SuperAdminService] Security alert resolved');
  },

  /**
   * Exporter les logs d'audit en CSV
   */
  exportAuditLogs: async (startDate?: string, endDate?: string): Promise<Blob> => {
    console.log('📥 [SuperAdminService] Exporting audit logs...');
    const response = await api.get('/api/super-admin/export-audit-logs', {
      params: { startDate, endDate },
      responseType: 'blob'
    });
    console.log('✅ [SuperAdminService] Audit logs exported');
    return response.data;
  },

  /**
   * Redémarrer un service (si disponible)
   */
  restartService: async (serviceName: string): Promise<void> => {
    console.log(`🔄 [SuperAdminService] Restarting service ${serviceName}...`);
    await api.post(`/api/super-admin/restart-service/${serviceName}`);
    console.log('✅ [SuperAdminService] Service restart initiated');
  },

  /**
   * Vider le cache système
   */
  clearCache: async (): Promise<void> => {
    console.log('🗑️ [SuperAdminService] Clearing system cache...');
    await api.post('/api/super-admin/clear-cache');
    console.log('✅ [SuperAdminService] Cache cleared');
  },
};

export default superAdminService;
