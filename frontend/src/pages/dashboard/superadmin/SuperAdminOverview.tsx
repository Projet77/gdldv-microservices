import React, { useState, useEffect } from 'react';
import {
    Users,
    Server,
    Activity,
    DollarSign,
    ShieldCheck,
    AlertTriangle,
    CheckCircle,
    Database,
    Zap,
    Loader
} from 'lucide-react';
import { superAdminService } from '../../../services/superAdminService';
import type { SuperAdminDashboard, SystemHealth } from '../../../services/superAdminService';

const SuperAdminOverview: React.FC = () => {
    const [dashboard, setDashboard] = useState<SuperAdminDashboard | null>(null);
    const [systemHealth, setSystemHealth] = useState<SystemHealth | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                setLoading(true);
                setError(null);

                console.log('🔒 [SuperAdminOverview] Fetching dashboard data...');

                // Charger les données du dashboard et la santé système en parallèle
                const [dashboardData, health] = await Promise.all([
                    superAdminService.getSuperAdminDashboard(),
                    superAdminService.getSystemHealth()
                ]);

                setDashboard(dashboardData);
                setSystemHealth(health);
                console.log('✅ [SuperAdminOverview] Dashboard data loaded successfully');
            } catch (error: any) {
                console.error('❌ [SuperAdminOverview] Error fetching dashboard:', error);
                setError(error.response?.data?.message || 'Erreur lors du chargement du dashboard');
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, []);

    // Sécuriser l'accès aux services (au cas où ce n'est pas un tableau)
    const servicesList = Array.isArray(systemHealth?.services) ? systemHealth.services : [];

    // Calculer les statistiques dynamiques
    const stats = dashboard ? [
        {
            name: 'Utilisateurs Totaux',
            value: dashboard.userStatistics.totalUsers.toLocaleString(),
            change: `+${dashboard.userStatistics.newUsersThisMonth}`,
            icon: Users,
            color: 'text-blue-500',
            bg: 'bg-blue-100'
        },
        {
            name: 'Charge Système',
            value: `${Math.round(dashboard.performanceMetrics.cpuUsage)}%`,
            change: dashboard.performanceMetrics.cpuUsage > 50 ? 'Élevée' : 'Normale',
            icon: Activity,
            color: 'text-yellow-500',
            bg: 'bg-yellow-100'
        },
        {
            name: 'Services Actifs',
            value: `${servicesList.filter(s => s.status === 'UP').length}/${servicesList.length}`,
            change: systemHealth?.overallStatus === 'HEALTHY' ? '100%' : 'Dégradé',
            icon: Server,
            color: 'text-purple-500',
            bg: 'bg-purple-100'
        },
        {
            name: 'Mémoire Utilisée',
            value: `${Math.round(dashboard.performanceMetrics.memoryUsage)}%`,
            change: dashboard.performanceMetrics.memoryUsage > 70 ? 'Critique' : 'OK',
            icon: Database,
            color: 'text-green-500',
            bg: 'bg-green-100'
        },
    ] : [];

    // Mapper les services avec leurs statuts
    const services = servicesList.map(service => ({
        name: service.name,
        status: service.status === 'UP' ? 'Online' : 'Offline',
        uptime: service.status === 'UP' ? '99.9%' : '0%',
        latency: service.status === 'UP' ? `${service.responseTime}ms` : '-'
    }));

    // Mapper les logs d'audit récents en activités
    const activities = dashboard?.auditStatistics.recentLogs.slice(0, 4).map(log => {
        let icon = ShieldCheck;
        let color = 'text-blue-500';

        if (log.action.includes('DELETE') || log.action.includes('BLOCK')) {
            icon = AlertTriangle;
            color = 'text-red-500';
        } else if (log.action.includes('UPDATE')) {
            icon = DollarSign;
            color = 'text-yellow-500';
        } else if (log.action.includes('CREATE')) {
            icon = CheckCircle;
            color = 'text-green-500';
        }

        const timeDiff = Math.floor((Date.now() - new Date(log.timestamp).getTime()) / 60000);
        const timeStr = timeDiff < 60 ? `il y a ${timeDiff} min` : `il y a ${Math.floor(timeDiff / 60)}h`;

        return {
            user: log.userName || `Utilisateur #${log.userId}`,
            action: log.action,
            time: timeStr,
            icon,
            color
        };
    }) || [];

    // État de chargement
    if (loading) {
        return (
            <div className="max-w-7xl mx-auto flex items-center justify-center h-64">
                <div className="text-center">
                    <Loader className="w-12 h-12 animate-spin text-yellow-500 mx-auto mb-4" />
                    <p className="text-gray-600 font-medium">Chargement des données système...</p>
                    <p className="text-sm text-gray-400 mt-2">Analyse de l'infrastructure en cours...</p>
                </div>
            </div>
        );
    }

    // État d'erreur
    if (error) {
        return (
            <div className="max-w-7xl mx-auto">
                <div className="bg-red-50 border border-red-200 rounded-xl p-6">
                    <div className="flex items-center gap-3">
                        <AlertTriangle className="w-6 h-6 text-red-500" />
                        <div>
                            <h3 className="font-bold text-red-900">Erreur de chargement</h3>
                            <p className="text-sm text-red-700 mt-1">{error}</p>
                        </div>
                    </div>
                    <button
                        onClick={() => window.location.reload()}
                        className="mt-4 bg-red-600 text-white px-4 py-2 rounded-lg font-bold text-sm hover:bg-red-700"
                    >
                        Réessayer
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="space-y-8">
            {/* Header Content */}
            <div>
                <h1 className="text-3xl font-black text-gray-900 tracking-tight">Vue Système</h1>
                <p className="mt-2 text-gray-500">
                    Aperçu global de l'infrastructure et des performances de <span className="font-bold text-black">ACA LOCATIONS</span>.
                </p>
            </div>

            {/* KPI Grid */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
                {stats.map((stat) => (
                    <div key={stat.name} className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 hover:shadow-md transition-shadow">
                        <div className="flex items-center justify-between">
                            <div>
                                <p className="text-sm font-medium text-gray-500 uppercase tracking-wider">{stat.name}</p>
                                <p className="mt-2 text-3xl font-black text-gray-900">{stat.value}</p>
                            </div>
                            <div className={`h-12 w-12 rounded-xl flex items-center justify-center ${stat.bg}`}>
                                <stat.icon className={`h-6 w-6 ${stat.color}`} />
                            </div>
                        </div>
                        <div className="mt-4 flex items-center text-sm">
                            <span className={`font-bold ${stat.change.startsWith('+') ? 'text-green-500' : 'text-red-500'}`}>
                                {stat.change}
                            </span>
                            <span className="text-gray-400 ml-2">vs le mois dernier</span>
                        </div>
                    </div>
                ))}
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                {/* Services Status */}
                <div className="lg:col-span-2 bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                    <div className="p-6 border-b border-gray-100 flex items-center justify-between">
                        <h2 className="text-lg font-bold text-gray-900">État des Microservices</h2>
                        <span className="px-3 py-1 bg-green-100 text-green-700 text-xs font-bold rounded-full uppercase tracking-wide">
                            Système Stable
                        </span>
                    </div>
                    <div className="p-6">
                        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                            {services.map((service) => (
                                <div key={service.name} className="flex items-center justify-between p-4 bg-gray-50 rounded-xl border border-gray-100">
                                    <div className="flex items-center gap-3">
                                        <div className={`h-3 w-3 rounded-full ${service.status === 'Online' ? 'bg-green-500 shadow-[0_0_8px_rgba(34,197,94,0.6)]' :
                                            service.status === 'Warning' ? 'bg-yellow-500 shadow-[0_0_8px_rgba(234,179,8,0.6)]' : 'bg-red-500 shadow-[0_0_8px_rgba(239,68,68,0.6)]'
                                            }`} />
                                        <div>
                                            <p className="font-bold text-gray-900 text-sm">{service.name}</p>
                                            <p className="text-xs text-gray-500">{service.uptime} uptime</p>
                                        </div>
                                    </div>
                                    <div className="text-right">
                                        <p className={`text-sm font-bold ${service.status === 'Online' ? 'text-green-600' :
                                            service.status === 'Warning' ? 'text-yellow-600' : 'text-red-600'
                                            }`}>
                                            {service.status === 'Online' ? 'En Ligne' : service.status === 'Offline' ? 'Hors Ligne' : 'Attention'}
                                        </p>
                                        <p className="text-xs text-gray-400">{service.latency}</p>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>

                {/* Recent Activity */}
                <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                    <div className="p-6 border-b border-gray-100">
                        <h2 className="text-lg font-bold text-gray-900">Activité Récente</h2>
                    </div>
                    <div className="p-6">
                        <div className="flow-root">
                            <ul className="-mb-8">
                                {activities.map((activity, activityIdx) => (
                                    <li key={activityIdx}>
                                        <div className="relative pb-8">
                                            {activityIdx !== activities.length - 1 ? (
                                                <span className="absolute top-4 left-4 -ml-px h-full w-0.5 bg-gray-100" aria-hidden="true" />
                                            ) : null}
                                            <div className="relative flex space-x-3">
                                                <div className="h-8 w-8 rounded-full bg-gray-50 flex items-center justify-center ring-8 ring-white">
                                                    <activity.icon className={`h-4 w-4 ${activity.color}`} />
                                                </div>
                                                <div className="min-w-0 flex-1 pt-1.5 flex justify-between space-x-4">
                                                    <div>
                                                        <p className="text-sm text-gray-500">
                                                            <span className="font-medium text-gray-900">{activity.user}</span>{' '}
                                                            {activity.action}
                                                        </p>
                                                    </div>
                                                    <div className="text-right text-sm whitespace-nowrap text-gray-400">
                                                        <time>{activity.time}</time>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </li>
                                ))}
                            </ul>
                        </div>
                    </div>
                </div>
            </div>

            {/* Quick Actions Row */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div className="bg-gradient-to-br from-gray-900 to-black rounded-2xl p-6 text-white relative overflow-hidden group cursor-pointer shadow-lg">
                    <div className="absolute top-0 right-0 -mt-4 -mr-4 h-24 w-24 bg-yellow-500 rounded-full opacity-20 group-hover:scale-150 transition-transform duration-500"></div>
                    <Zap className="h-8 w-8 text-yellow-400 mb-4" />
                    <h3 className="text-lg font-bold mb-1">Performance Tuning</h3>
                    <p className="text-gray-400 text-sm">Optimiser les caches et les connexions DB.</p>
                </div>

                <div className="bg-yellow-400 rounded-2xl p-6 text-black relative overflow-hidden group cursor-pointer shadow-lg">
                    <div className="absolute top-0 right-0 -mt-4 -mr-4 h-24 w-24 bg-white rounded-full opacity-20 group-hover:scale-150 transition-transform duration-500"></div>
                    <ShieldCheck className="h-8 w-8 text-black mb-4" />
                    <h3 className="text-lg font-black mb-1">Audit de Sécurité</h3>
                    <p className="text-black/70 text-sm font-medium">Lancer un scan complet des services.</p>
                </div>

                <div className="bg-white rounded-2xl p-6 border border-gray-100 text-gray-900 relative overflow-hidden group cursor-pointer hover:border-yellow-400 transition-colors shadow-sm">
                    <Database className="h-8 w-8 text-gray-400 group-hover:text-yellow-500 transition-colors mb-4" />
                    <h3 className="text-lg font-bold mb-1">Gestion Database</h3>
                    <p className="text-gray-500 text-sm">Backups et maintenance des schémas.</p>
                </div>
            </div>
        </div>
    );
};

export default SuperAdminOverview;
