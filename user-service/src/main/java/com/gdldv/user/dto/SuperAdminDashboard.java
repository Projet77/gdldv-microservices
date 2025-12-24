package com.gdldv.user.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO pour le dashboard SUPER_ADMIN
 * Administration système et infrastructure
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuperAdminDashboard {
    // Santé du système
    private SystemHealth systemHealth;

    // Statistiques utilisateurs
    private UserStatistics userStatistics;

    // Base de données
    private DatabaseStatistics databaseStatistics;

    // Audit et logs
    private AuditStatistics auditStatistics;

    // Configuration système
    private SystemConfiguration systemConfiguration;

    // Sécurité
    private SecurityMetrics securityMetrics;

    // Performance
    private PerformanceMetrics performanceMetrics;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SystemHealth {
        private Map<String, ServiceStatus> services; // user-service, vehicle-service, etc.
        private Double overallUptime; // pourcentage
        private Integer daysRunning;
        private Integer currentAlerts;
        private LocalDateTime lastMaintenance;
        private Boolean maintenanceSuccess;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class ServiceStatus {
            private String name;
            private String status; // UP, DOWN, DEGRADED
            private Integer port;
            private Double uptime;
            private LocalDateTime lastCheck;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserStatistics {
        private Long totalUsers;
        private Map<String, Long> usersByRole; // CLIENT: 500, AGENT: 12, etc.
        private Long activeUsers30Days;
        private Long inactiveUsers;
        private Integer newUsersThisMonth;
        private Integer deletedUsersThisMonth;
        private Double activityRate; // pourcentage
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DatabaseStatistics {
        private Map<String, TableStats> tables;
        private Long totalDiskSpaceUsed; // en MB
        private Long totalDiskSpaceAvailable;
        private Double diskUsagePercentage;
        private String status; // OK, WARNING, CRITICAL
        private LocalDateTime lastBackup;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class TableStats {
            private String tableName;
            private Long recordCount;
            private Long sizeInMB;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AuditStatistics {
        private Integer actionsToday;
        private Integer actionsThisMonth;
        private List<AuditLogEntry> recentLogs;
        private Integer failedLoginAttempts30Days;
        private List<SecurityAlert> securityAlerts;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class AuditLogEntry {
            private LocalDateTime timestamp;
            private Long userId;
            private String action;
            private String details;
            private String ipAddress;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class SecurityAlert {
            private String type; // UNAUTHORIZED_ACCESS, SUSPICIOUS_ACTIVITY, DATA_BREACH
            private String description;
            private LocalDateTime detectedAt;
            private String severity; // CRITICAL, HIGH, MEDIUM, LOW
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SystemConfiguration {
        private String applicationName;
        private String version;
        private String environment; // PRODUCTION, STAGING, DEVELOPMENT
        private String timezone;
        private PaymentConfig paymentConfig;
        private EmailConfig emailConfig;
        private SecurityConfig securityConfig;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class PaymentConfig {
            private String provider; // STRIPE
            private String currency;
            private Boolean active;
            private Boolean webhooksActive;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class EmailConfig {
            private String smtpServer;
            private Boolean connected;
            private Integer emailsSentThisMonth;
            private Double bounceRate;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class SecurityConfig {
            private Boolean sslEnabled;
            private Boolean jwtConfigured;
            private Integer jwtExpirationHours;
            private Boolean rateLimitingEnabled;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SecurityMetrics {
        private Integer failedLoginAttempts;
        private Integer suspiciousActivities;
        private Integer blockedIPs;
        private List<String> recentThreats;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PerformanceMetrics {
        private Double averageResponseTime; // ms
        private Integer requestsPerMinute;
        private Double cpuUsage; // pourcentage
        private Double memoryUsage; // pourcentage
        private Integer activeConnections;
    }
}
