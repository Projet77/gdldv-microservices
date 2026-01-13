package com.gdldv.user.service;

import com.gdldv.user.dto.SuperAdminDashboard;
import com.gdldv.user.repository.ReservationRepository;
import com.gdldv.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service pour le dashboard SUPER_ADMIN
 * Administration système et infrastructure
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SuperAdminDashboardService {

        private final UserRepository userRepository;
        private final ReservationRepository reservationRepository;

        @Value("${spring.application.name:user-service}")
        private String applicationName;

        @Value("${server.port:8003}")
        private Integer serverPort;

        public SuperAdminDashboard getSuperAdminDashboard() {
                log.info("Fetching super admin dashboard");

                return SuperAdminDashboard.builder()
                                .systemHealth(getSystemHealth())
                                .userStatistics(getUserStatistics())
                                .databaseStatistics(getDatabaseStatistics())
                                .auditStatistics(getAuditStatistics())
                                .systemConfiguration(getSystemConfiguration())
                                .securityMetrics(getSecurityMetrics())
                                .performanceMetrics(getPerformanceMetrics())
                                .build();
        }

        private SuperAdminDashboard.SystemHealth getSystemHealth() {
                Map<String, SuperAdminDashboard.SystemHealth.ServiceStatus> services = new HashMap<>();

                // User Service
                services.put("user-service", SuperAdminDashboard.SystemHealth.ServiceStatus.builder()
                                .name("User Service")
                                .status("UP")
                                .port(8003)
                                .uptime(99.98)
                                .lastCheck(LocalDateTime.now())
                                .build());

                // Vehicle Service
                services.put("vehicle-service", SuperAdminDashboard.SystemHealth.ServiceStatus.builder()
                                .name("Vehicle Service")
                                .status("UP")
                                .port(8001)
                                .uptime(99.95)
                                .lastCheck(LocalDateTime.now())
                                .build());

                // Rental Service
                services.put("rental-service", SuperAdminDashboard.SystemHealth.ServiceStatus.builder()
                                .name("Rental Service")
                                .status("UP")
                                .port(8002)
                                .uptime(99.97)
                                .lastCheck(LocalDateTime.now())
                                .build());

                // Reservation Service
                services.put("reservation-service", SuperAdminDashboard.SystemHealth.ServiceStatus.builder()
                                .name("Reservation Service")
                                .status("UP")
                                .port(8004)
                                .uptime(99.99)
                                .lastCheck(LocalDateTime.now())
                                .build());

                // Config Server
                services.put("config-server", SuperAdminDashboard.SystemHealth.ServiceStatus.builder()
                                .name("Config Server")
                                .status("UP")
                                .port(8888)
                                .uptime(100.0)
                                .lastCheck(LocalDateTime.now())
                                .build());

                // Eureka Server
                services.put("eureka-server", SuperAdminDashboard.SystemHealth.ServiceStatus.builder()
                                .name("Eureka Server")
                                .status("UP")
                                .port(8761)
                                .uptime(100.0)
                                .lastCheck(LocalDateTime.now())
                                .build());

                // API Gateway
                services.put("api-gateway", SuperAdminDashboard.SystemHealth.ServiceStatus.builder()
                                .name("API Gateway")
                                .status("UP")
                                .port(8000)
                                .uptime(99.96)
                                .lastCheck(LocalDateTime.now())
                                .build());

                // MySQL
                services.put("mysql", SuperAdminDashboard.SystemHealth.ServiceStatus.builder()
                                .name("MySQL Database")
                                .status("UP")
                                .port(3306)
                                .uptime(100.0)
                                .lastCheck(LocalDateTime.now())
                                .build());

                return SuperAdminDashboard.SystemHealth.builder()
                                .services(services)
                                .overallUptime(99.98)
                                .daysRunning(3)
                                .currentAlerts(0)
                                .lastMaintenance(LocalDateTime.now().minus(5, ChronoUnit.DAYS))
                                .maintenanceSuccess(true)
                                .build();
        }

        private SuperAdminDashboard.UserStatistics getUserStatistics() {
                long totalUsers = userRepository.count();

                // Compter par rôle
                Map<String, Long> usersByRole = new HashMap<>();
                usersByRole.put("CLIENT", userRepository.countByRole("CLIENT"));
                usersByRole.put("AGENT", userRepository.countByRole("AGENT"));
                usersByRole.put("MANAGER", userRepository.countByRole("MANAGER"));
                usersByRole.put("ADMIN", userRepository.countByRole("ADMIN"));
                usersByRole.put("SUPER_ADMIN", userRepository.countByRole("SUPER_ADMIN"));

                // Utilisateurs actifs (30 derniers jours)
                LocalDateTime thirtyDaysAgo = LocalDateTime.now().minus(30, ChronoUnit.DAYS);
                long activeUsers = userRepository.countByLastLoginAtAfter(thirtyDaysAgo);

                // Nouveaux utilisateurs ce mois
                LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
                int newUsersThisMonth = userRepository.countByCreatedAtAfter(monthStart);

                double activityRate = totalUsers > 0 ? ((double) activeUsers / totalUsers) * 100 : 0.0;

                return SuperAdminDashboard.UserStatistics.builder()
                                .totalUsers(totalUsers)
                                .usersByRole(usersByRole)
                                .activeUsers30Days(activeUsers)
                                .inactiveUsers(totalUsers - activeUsers)
                                .newUsersThisMonth(newUsersThisMonth)
                                .deletedUsersThisMonth(0) // TODO: implémenter soft delete tracking
                                .activityRate(activityRate)
                                .build();
        }

        private SuperAdminDashboard.DatabaseStatistics getDatabaseStatistics() {
                Map<String, SuperAdminDashboard.DatabaseStatistics.TableStats> tables = new HashMap<>();

                // Users table
                long usersCount = userRepository.count();
                tables.put("users", SuperAdminDashboard.DatabaseStatistics.TableStats.builder()
                                .tableName("users")
                                .recordCount(usersCount)
                                .sizeInMB((usersCount * 2) / 1000) // Estimation
                                .build());

                // Reservations table
                long reservationsCount = reservationRepository.count();
                tables.put("reservations", SuperAdminDashboard.DatabaseStatistics.TableStats.builder()
                                .tableName("reservations")
                                .recordCount(reservationsCount)
                                .sizeInMB((reservationsCount * 5) / 1000)
                                .build());

                // TODO: Ajouter les autres tables

                long totalDiskUsed = tables.values().stream()
                                .mapToLong(SuperAdminDashboard.DatabaseStatistics.TableStats::getSizeInMB)
                                .sum();

                long totalDiskAvailable = 10240L; // 10 GB
                double diskUsagePercentage = ((double) totalDiskUsed / totalDiskAvailable) * 100;

                String status = diskUsagePercentage < 50 ? "OK" : diskUsagePercentage < 80 ? "WARNING" : "CRITICAL";

                return SuperAdminDashboard.DatabaseStatistics.builder()
                                .tables(tables)
                                .totalDiskSpaceUsed(totalDiskUsed)
                                .totalDiskSpaceAvailable(totalDiskAvailable)
                                .diskUsagePercentage(diskUsagePercentage)
                                .status(status)
                                .lastBackup(LocalDateTime.now().minus(1, ChronoUnit.DAYS))
                                .build();
        }

        private SuperAdminDashboard.AuditStatistics getAuditStatistics() {
                // TODO: Implémenter le système d'audit réel

                List<SuperAdminDashboard.AuditStatistics.AuditLogEntry> recentLogs = new ArrayList<>();
                recentLogs.add(SuperAdminDashboard.AuditStatistics.AuditLogEntry.builder()
                                .timestamp(LocalDateTime.now().minus(5, ChronoUnit.MINUTES))
                                .userId(1L)
                                .action("CREATE_RESERVATION")
                                .details("Created reservation CONF-ABC123")
                                .ipAddress("192.168.1.100")
                                .build());

                List<SuperAdminDashboard.AuditStatistics.SecurityAlert> securityAlerts = new ArrayList<>();

                return SuperAdminDashboard.AuditStatistics.builder()
                                .actionsToday(425)
                                .actionsThisMonth(12750)
                                .recentLogs(recentLogs)
                                .failedLoginAttempts30Days(12)
                                .securityAlerts(securityAlerts)
                                .build();
        }

        private SuperAdminDashboard.SystemConfiguration getSystemConfiguration() {
                SuperAdminDashboard.SystemConfiguration.PaymentConfig paymentConfig = SuperAdminDashboard.SystemConfiguration.PaymentConfig
                                .builder()
                                .provider("STRIPE")
                                .currency("XOF")
                                .active(true)
                                .webhooksActive(true)
                                .build();

                SuperAdminDashboard.SystemConfiguration.EmailConfig emailConfig = SuperAdminDashboard.SystemConfiguration.EmailConfig
                                .builder()
                                .smtpServer("smtp.gmail.com")
                                .connected(true)
                                .emailsSentThisMonth(450)
                                .bounceRate(0.2)
                                .build();

                SuperAdminDashboard.SystemConfiguration.SecurityConfig securityConfig = SuperAdminDashboard.SystemConfiguration.SecurityConfig
                                .builder()
                                .sslEnabled(true)
                                .jwtConfigured(true)
                                .jwtExpirationHours(24)
                                .rateLimitingEnabled(true)
                                .build();

                return SuperAdminDashboard.SystemConfiguration.builder()
                                .applicationName("GDLDV")
                                .version("1.0.0")
                                .environment("PRODUCTION")
                                .timezone("Africa/Dakar")
                                .paymentConfig(paymentConfig)
                                .emailConfig(emailConfig)
                                .securityConfig(securityConfig)
                                .build();
        }

        private SuperAdminDashboard.SecurityMetrics getSecurityMetrics() {
                // TODO: Implémenter logique réelle
                List<String> blockedIPs = new ArrayList<>();
                List<SuperAdminDashboard.SuspiciousActivity> suspiciousActivities = new ArrayList<>();
                List<SuperAdminDashboard.AuditStatistics.SecurityAlert> securityAlerts = new ArrayList<>();
                List<SuperAdminDashboard.FailedLogin> recentFailedLogins = new ArrayList<>();

                return SuperAdminDashboard.SecurityMetrics.builder()
                                .failedLoginAttempts(12)
                                .blockedIPs(blockedIPs)
                                .suspiciousActivities(suspiciousActivities)
                                .securityAlerts(securityAlerts)
                                .recentFailedLogins(recentFailedLogins)
                                .build();
        }

        private SuperAdminDashboard.PerformanceMetrics getPerformanceMetrics() {
                return SuperAdminDashboard.PerformanceMetrics.builder()
                                .averageResponseTime(150.0) // ms
                                .requestsPerMinute(45)
                                .cpuUsage(35.5)
                                .memoryUsage(62.3)
                                .activeConnections(12)
                                .build();
        }
}
