import React, { useState, useEffect } from 'react';
import {
    Shield,
    AlertTriangle,
    CheckCircle,
    Lock,
    Key,
    Loader,
    Download,
    Ban,
    Unlock,
    Eye,
    XCircle,
    Activity,
    UserX
} from 'lucide-react';
import { superAdminService } from '../../../services/superAdminService';
import type { SecurityMetrics, AuditLog } from '../../../services/superAdminService';

const SuperAdminSecurity: React.FC = () => {
    const [securityMetrics, setSecurityMetrics] = useState<SecurityMetrics | null>(null);
    const [auditLogs, setAuditLogs] = useState<AuditLog[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [selectedAlert, setSelectedAlert] = useState<number | null>(null);

    useEffect(() => {
        fetchSecurityData();
    }, [page]);

    const fetchSecurityData = async () => {
        try {
            setLoading(true);
            setError(null);

            console.log('🔐 [SuperAdminSecurity] Fetching security data...');

            // Charger les métriques de sécurité et les logs d'audit en parallèle
            const [metrics, logs] = await Promise.all([
                superAdminService.getSecurityMetrics(),
                superAdminService.getAuditLogs(page, 20, 'timestamp,desc')
            ]);

            setSecurityMetrics(metrics);
            setAuditLogs(logs.content);
            setTotalPages(logs.totalPages);

            console.log('✅ [SuperAdminSecurity] Security data loaded');
        } catch (error: any) {
            console.error('❌ [SuperAdminSecurity] Error:', error);
            setError('Erreur lors du chargement des données de sécurité');
        } finally {
            setLoading(false);
        }
    };

    const handleBlockIP = async (ipAddress: string) => {
        try {
            await superAdminService.blockIP(ipAddress, 'Blocked by Super Admin');
            alert(`IP ${ipAddress} bloquée avec succès`);
            fetchSecurityData();
        } catch (error) {
            console.error('Error blocking IP:', error);
            alert('Erreur lors du blocage de l\'IP');
        }
    };

    const handleUnblockIP = async (ipAddress: string) => {
        try {
            await superAdminService.unblockIP(ipAddress);
            alert(`IP ${ipAddress} débloquée avec succès`);
            fetchSecurityData();
        } catch (error) {
            console.error('Error unblocking IP:', error);
            alert('Erreur lors du déblocage de l\'IP');
        }
    };

    const handleResolveAlert = async (alertId: number) => {
        try {
            await superAdminService.resolveSecurityAlert(alertId);
            alert('Alerte résolue avec succès');
            fetchSecurityData();
        } catch (error) {
            console.error('Error resolving alert:', error);
            alert('Erreur lors de la résolution de l\'alerte');
        }
    };

    const handleExportLogs = async () => {
        try {
            const blob = await superAdminService.exportAuditLogs();
            const url = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = url;
            link.download = `audit-logs-${new Date().toISOString().split('T')[0]}.csv`;
            link.click();
            console.log('✅ [SuperAdminSecurity] Logs exported');
        } catch (error) {
            console.error('Error exporting logs:', error);
            alert('Erreur lors de l\'export des logs');
        }
    };

    // Loading state
    if (loading && !securityMetrics) {
        return (
            <div className="max-w-7xl mx-auto px-4 py-8">
                <div className="flex items-center justify-center h-64">
                    <div className="text-center">
                        <Loader className="w-12 h-12 animate-spin text-yellow-500 mx-auto mb-4" />
                        <p className="text-gray-600 font-medium">Chargement des données de sécurité...</p>
                        <p className="text-sm text-gray-400 mt-2">Analyse des métriques en cours...</p>
                    </div>
                </div>
            </div>
        );
    }

    // Error state
    if (error && !securityMetrics) {
        return (
            <div className="max-w-7xl mx-auto px-4 py-8">
                <div className="bg-red-50 border border-red-200 rounded-xl p-6">
                    <div className="flex items-center gap-3">
                        <AlertTriangle className="w-6 h-6 text-red-500" />
                        <div>
                            <h3 className="font-bold text-red-900">Erreur</h3>
                            <p className="text-sm text-red-700 mt-1">{error}</p>
                        </div>
                    </div>
                    <button
                        onClick={fetchSecurityData}
                        className="mt-4 bg-red-600 text-white px-4 py-2 rounded-lg font-bold text-sm hover:bg-red-700"
                    >
                        Réessayer
                    </button>
                </div>
            </div>
        );
    }

    const criticalAlerts = securityMetrics?.securityAlerts.filter(a => a.severity === 'CRITICAL' && !a.resolved).length || 0;
    const highAlerts = securityMetrics?.securityAlerts.filter(a => a.severity === 'HIGH' && !a.resolved).length || 0;

    return (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
            {/* Header */}
            <div>
                <h1 className="text-3xl font-black text-gray-900 tracking-tight">Sécurité</h1>
                <p className="mt-1 text-gray-500">Surveillance et audit de la sécurité du système.</p>
            </div>

            {/* Security Status Cards */}
            <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
                <div className={`${criticalAlerts + highAlerts === 0 ? 'bg-green-50 border-green-200' : 'bg-red-50 border-red-200'} rounded-2xl p-6 border-2`}>
                    <div className="flex items-center justify-between mb-2">
                        {criticalAlerts + highAlerts === 0 ? (
                            <CheckCircle className="h-8 w-8 text-green-600" />
                        ) : (
                            <AlertTriangle className="h-8 w-8 text-red-600" />
                        )}
                        <span className="text-3xl font-black text-gray-900">{criticalAlerts + highAlerts}</span>
                    </div>
                    <p className="text-sm font-bold text-gray-900">Alertes Actives</p>
                    <p className="text-xs text-gray-600 mt-1">
                        {criticalAlerts + highAlerts === 0 ? 'Aucune menace détectée' : `${criticalAlerts} critiques, ${highAlerts} élevées`}
                    </p>
                </div>

                <div className="bg-white rounded-2xl p-6 border border-gray-200 shadow-sm">
                    <div className="flex items-center justify-between mb-2">
                        <UserX className="h-8 w-8 text-orange-500" />
                        <span className="text-3xl font-black text-gray-900">{securityMetrics?.failedLoginAttempts || 0}</span>
                    </div>
                    <p className="text-sm font-bold text-gray-900">Échecs Connexion</p>
                    <p className="text-xs text-gray-500 mt-1">Dernières 24h</p>
                </div>

                <div className="bg-white rounded-2xl p-6 border border-gray-200 shadow-sm">
                    <div className="flex items-center justify-between mb-2">
                        <Ban className="h-8 w-8 text-red-500" />
                        <span className="text-3xl font-black text-gray-900">{securityMetrics?.blockedIPs.length || 0}</span>
                    </div>
                    <p className="text-sm font-bold text-gray-900">IPs Bloquées</p>
                    <p className="text-xs text-gray-500 mt-1">Actuellement</p>
                </div>

                <div className="bg-white rounded-2xl p-6 border border-gray-200 shadow-sm">
                    <div className="flex items-center justify-between mb-2">
                        <Activity className="h-8 w-8 text-blue-500" />
                        <span className="text-3xl font-black text-gray-900">{securityMetrics?.suspiciousActivities.length || 0}</span>
                    </div>
                    <p className="text-sm font-bold text-gray-900">Activités Suspectes</p>
                    <p className="text-xs text-gray-500 mt-1">À surveiller</p>
                </div>
            </div>

            {/* Security Alerts */}
            {securityMetrics && securityMetrics.securityAlerts.length > 0 && (
                <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                    <div className="px-6 py-4 border-b border-gray-100 flex items-center justify-between">
                        <h3 className="text-lg font-bold text-gray-900 flex items-center gap-2">
                            <AlertTriangle className="w-5 h-5 text-red-500" />
                            Alertes de Sécurité
                        </h3>
                    </div>
                    <div className="p-6 space-y-3">
                        {securityMetrics.securityAlerts.filter(a => !a.resolved).slice(0, 5).map((alert) => {
                            const severityConfig = {
                                CRITICAL: { bg: 'bg-red-50', border: 'border-red-200', text: 'text-red-900', badge: 'bg-red-600 text-white' },
                                HIGH: { bg: 'bg-orange-50', border: 'border-orange-200', text: 'text-orange-900', badge: 'bg-orange-600 text-white' },
                                MEDIUM: { bg: 'bg-yellow-50', border: 'border-yellow-200', text: 'text-yellow-900', badge: 'bg-yellow-600 text-white' },
                                LOW: { bg: 'bg-blue-50', border: 'border-blue-200', text: 'text-blue-900', badge: 'bg-blue-600 text-white' }
                            };
                            const config = severityConfig[alert.severity];

                            return (
                                <div key={alert.id} className={`flex items-center justify-between p-4 ${config.bg} border ${config.border} rounded-xl`}>
                                    <div className="flex-1">
                                        <div className="flex items-center gap-2 mb-1">
                                            <span className={`px-2 py-1 ${config.badge} text-xs font-bold rounded uppercase`}>
                                                {alert.severity}
                                            </span>
                                            <span className="text-xs text-gray-500">{alert.type}</span>
                                        </div>
                                        <p className={`text-sm font-bold ${config.text}`}>{alert.message}</p>
                                        <p className="text-xs text-gray-500 mt-1">
                                            {new Date(alert.timestamp).toLocaleString()}
                                        </p>
                                    </div>
                                    <button
                                        onClick={() => handleResolveAlert(alert.id)}
                                        className="ml-4 px-3 py-1 bg-white border border-gray-300 text-gray-700 text-xs font-bold rounded-lg hover:bg-gray-50"
                                    >
                                        Résoudre
                                    </button>
                                </div>
                            );
                        })}
                    </div>
                </div>
            )}

            {/* Blocked IPs */}
            {securityMetrics && securityMetrics.blockedIPs.length > 0 && (
                <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                    <div className="px-6 py-4 border-b border-gray-100 flex items-center justify-between">
                        <h3 className="text-lg font-bold text-gray-900 flex items-center gap-2">
                            <Ban className="w-5 h-5 text-red-500" />
                            Adresses IP Bloquées
                        </h3>
                    </div>
                    <div className="p-6">
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3">
                            {securityMetrics.blockedIPs.map((ip, index) => (
                                <div key={index} className="flex items-center justify-between p-3 bg-red-50 border border-red-200 rounded-lg">
                                    <span className="font-mono text-sm font-bold text-red-900">{ip}</span>
                                    <button
                                        onClick={() => handleUnblockIP(ip)}
                                        className="p-1 text-red-600 hover:text-red-800"
                                        title="Débloquer"
                                    >
                                        <Unlock className="w-4 h-4" />
                                    </button>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            )}

            {/* Failed Login Attempts */}
            {securityMetrics && securityMetrics.recentFailedLogins.length > 0 && (
                <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                    <div className="px-6 py-4 border-b border-gray-100">
                        <h3 className="text-lg font-bold text-gray-900 flex items-center gap-2">
                            <UserX className="w-5 h-5 text-orange-500" />
                            Tentatives de Connexion Échouées
                        </h3>
                    </div>
                    <div className="overflow-x-auto">
                        <table className="w-full">
                            <thead className="bg-gray-50">
                                <tr>
                                    <th className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase">Utilisateur</th>
                                    <th className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase">IP</th>
                                    <th className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase">Raison</th>
                                    <th className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase">Date</th>
                                    <th className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase">Action</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-gray-100">
                                {securityMetrics.recentFailedLogins.slice(0, 10).map((login) => (
                                    <tr key={login.id} className="hover:bg-gray-50">
                                        <td className="px-6 py-4 text-sm font-medium text-gray-900">{login.username}</td>
                                        <td className="px-6 py-4 text-sm font-mono text-gray-600">{login.ipAddress}</td>
                                        <td className="px-6 py-4 text-sm text-gray-500">{login.reason}</td>
                                        <td className="px-6 py-4 text-sm text-gray-500">
                                            {new Date(login.timestamp).toLocaleString()}
                                        </td>
                                        <td className="px-6 py-4">
                                            <button
                                                onClick={() => handleBlockIP(login.ipAddress)}
                                                className="text-xs font-bold text-red-600 hover:text-red-800"
                                            >
                                                Bloquer IP
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}

            {/* Audit Logs */}
            <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                <div className="px-6 py-4 border-b border-gray-100 flex items-center justify-between">
                    <h3 className="text-lg font-bold text-gray-900">Journal d'Audit</h3>
                    <button
                        onClick={handleExportLogs}
                        className="flex items-center gap-2 text-sm font-bold text-yellow-500 hover:text-yellow-600"
                    >
                        <Download className="w-4 h-4" />
                        Exporter CSV
                    </button>
                </div>
                <div className="overflow-x-auto">
                    <table className="w-full">
                        <thead className="bg-gray-50">
                            <tr>
                                <th className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase">Utilisateur</th>
                                <th className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase">Action</th>
                                <th className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase">Type</th>
                                <th className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase">IP</th>
                                <th className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase">Date</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-100">
                            {auditLogs.map((log) => (
                                <tr key={log.id} className="hover:bg-gray-50">
                                    <td className="px-6 py-4 text-sm font-medium text-gray-900">
                                        {log.userName || `User #${log.userId}`}
                                    </td>
                                    <td className="px-6 py-4 text-sm text-gray-900">{log.action}</td>
                                    <td className="px-6 py-4 text-sm text-gray-500">{log.entityType}</td>
                                    <td className="px-6 py-4 text-sm font-mono text-gray-600">{log.ipAddress}</td>
                                    <td className="px-6 py-4 text-sm text-gray-500">
                                        {new Date(log.timestamp).toLocaleString()}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>

                {/* Pagination */}
                {totalPages > 1 && (
                    <div className="px-6 py-4 border-t border-gray-100 flex items-center justify-between">
                        <p className="text-sm text-gray-500">
                            Page {page + 1} sur {totalPages}
                        </p>
                        <div className="flex gap-2">
                            <button
                                onClick={() => setPage(Math.max(0, page - 1))}
                                disabled={page === 0}
                                className="px-3 py-1 bg-gray-100 text-gray-700 text-sm font-bold rounded-lg hover:bg-gray-200 disabled:opacity-50 disabled:cursor-not-allowed"
                            >
                                Précédent
                            </button>
                            <button
                                onClick={() => setPage(Math.min(totalPages - 1, page + 1))}
                                disabled={page === totalPages - 1}
                                className="px-3 py-1 bg-gray-100 text-gray-700 text-sm font-bold rounded-lg hover:bg-gray-200 disabled:opacity-50 disabled:cursor-not-allowed"
                            >
                                Suivant
                            </button>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default SuperAdminSecurity;
