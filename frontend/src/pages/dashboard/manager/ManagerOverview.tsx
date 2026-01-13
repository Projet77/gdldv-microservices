import React, { useState, useEffect } from 'react';
import {
    Users,
    Car,
    Calendar,
    DollarSign,
    TrendingUp,
    Clock,
    CheckCircle,
    AlertTriangle,
    Loader,
    BarChart3
} from 'lucide-react';
import StatCard from '../../../components/dashboard/StatCard';
import { authService } from '../../../services/authService';
import api from '../../../services/api';

// Types
interface ManagerStats {
    todayReservations: number;
    activeRentals: number;
    pendingApprovals: number;
    teamMembers: number;
    weeklyRevenue: number;
    completionRate: number;
}

interface RecentReservation {
    id: number;
    confirmationNumber: string;
    customerName: string;
    vehicleName: string;
    status: string;
    startDate: string;
    totalPrice: number;
}

const ManagerOverview: React.FC = () => {
    const [stats, setStats] = useState<ManagerStats>({
        todayReservations: 0,
        activeRentals: 0,
        pendingApprovals: 0,
        teamMembers: 0,
        weeklyRevenue: 0,
        completionRate: 0
    });
    const [recentReservations, setRecentReservations] = useState<RecentReservation[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const user = authService.getCurrentUser();

    useEffect(() => {
        fetchDashboardData();
    }, []);

    const fetchDashboardData = async () => {
        try {
            setLoading(true);
            setError(null);

            console.log('🔄 [ManagerOverview] Fetching dashboard data...');

            // Récupérer les réservations directement via les endpoints status
            const [pendingRes, confirmedRes, activeRes, completedRes, cancelledRes] = await Promise.all([
                api.get('/api/reservations/status/PENDING').catch(() => ({ data: [] })),
                api.get('/api/reservations/status/CONFIRMED').catch(() => ({ data: [] })),
                api.get('/api/reservations/status/ACTIVE').catch(() => ({ data: [] })),
                api.get('/api/reservations/status/COMPLETED').catch(() => ({ data: [] })),
                api.get('/api/reservations/status/CANCELLED').catch(() => ({ data: [] }))
            ]);

            const allReservations = [
                ...pendingRes.data,
                ...confirmedRes.data,
                ...activeRes.data,
                ...completedRes.data,
                ...cancelledRes.data
            ];

            // Filtrer les réservations par statut
            const activeRentals = activeRes.data;
            const pendingApprovals = pendingRes.data;
            const confirmedReservations = confirmedRes.data;
            const completedReservations = completedRes.data;
            const cancelledReservations = cancelledRes.data;

            // Calculer les réservations d'aujourd'hui
            const today = new Date();
            today.setHours(0, 0, 0, 0);
            const tomorrow = new Date(today);
            tomorrow.setDate(tomorrow.getDate() + 1);

            const todayReservations = allReservations.filter((r: any) => {
                if (!r.createdAt) return false;
                const createdAt = new Date(r.createdAt);
                return createdAt >= today && createdAt < tomorrow;
            });

            // Calculer le revenu hebdomadaire (réservations confirmées ou complétées de la semaine)
            const weekAgo = new Date();
            weekAgo.setDate(weekAgo.getDate() - 7);
            const weeklyRevenue = allReservations
                .filter((r: any) => {
                    if (!r.createdAt) return false;
                    const createdAt = new Date(r.createdAt);
                    return createdAt >= weekAgo && (r.status === 'CONFIRMED' || r.status === 'COMPLETED');
                })
                .reduce((sum: number, r: any) => sum + (r.totalPrice || 0), 0);

            // Calculer le taux de complétion (complétées / (complétées + annulées))
            const totalCompleted = completedReservations.length;
            const totalCancelled = cancelledReservations.length;
            const totalProcessed = totalCompleted + totalCancelled;
            const completionRate = totalProcessed > 0 ? Math.round((totalCompleted / totalProcessed) * 100) : 100;

            setStats({
                todayReservations: todayReservations.length,
                activeRentals: activeRentals.length,
                pendingApprovals: pendingApprovals.length,
                teamMembers: 5, // Placeholder - à connecter à l'API utilisateurs plus tard
                weeklyRevenue: weeklyRevenue,
                completionRate: completionRate
            });

            // Préparer les réservations récentes (les 5 dernières en attente ou confirmées)
            const recent = [...pendingApprovals, ...confirmedReservations]
                .sort((a: any, b: any) => {
                    const dateA = new Date(a.createdAt || a.startDate).getTime();
                    const dateB = new Date(b.createdAt || b.startDate).getTime();
                    return dateB - dateA;
                })
                .slice(0, 5)
                .map((r: any) => ({
                    id: r.id,
                    confirmationNumber: r.confirmationNumber,
                    customerName: `Client #${r.userId}`,
                    vehicleName: r.vehicle ? `${r.vehicle.brand} ${r.vehicle.model}` : 'Véhicule',
                    status: r.status,
                    startDate: r.startDate,
                    totalPrice: r.totalPrice
                }));

            setRecentReservations(recent);
            console.log('✅ [ManagerOverview] Dashboard data loaded successfully');
        } catch (error: any) {
            console.error('❌ [ManagerOverview] Error fetching dashboard:', error);
            setError('Erreur lors du chargement du dashboard');
        } finally {
            setLoading(false);
        }
    };

    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('fr-FR', { day: 'numeric', month: 'short', year: 'numeric' });
    };

    const getStatusBadge = (status: string) => {
        const statusConfig: Record<string, { label: string; classes: string }> = {
            PENDING: { label: 'En Attente', classes: 'bg-yellow-100 text-yellow-800' },
            CONFIRMED: { label: 'Confirmée', classes: 'bg-green-100 text-green-800' },
            ACTIVE: { label: 'Active', classes: 'bg-blue-100 text-blue-800' },
            COMPLETED: { label: 'Terminée', classes: 'bg-gray-100 text-gray-800' },
            CANCELLED: { label: 'Annulée', classes: 'bg-red-100 text-red-800' }
        };

        const config = statusConfig[status] || { label: status, classes: 'bg-gray-100 text-gray-800' };
        return (
            <span className={`px-2 py-1 text-xs font-bold rounded-full ${config.classes}`}>
                {config.label}
            </span>
        );
    };

    // État de chargement
    if (loading) {
        return (
            <div className="max-w-7xl mx-auto flex items-center justify-center h-64">
                <div className="text-center">
                    <Loader className="w-12 h-12 animate-spin text-yellow-500 mx-auto mb-4" />
                    <p className="text-gray-600 font-medium">Chargement du dashboard...</p>
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
                        onClick={fetchDashboardData}
                        className="mt-4 bg-red-600 text-white px-4 py-2 rounded-lg font-bold text-sm hover:bg-red-700"
                    >
                        Réessayer
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="max-w-7xl mx-auto">
            {/* Header */}
            <div className="flex flex-col md:flex-row md:items-center justify-between mb-8 gap-4">
                <div>
                    <h1 className="text-3xl font-black text-gray-900">Vue d'ensemble Manager</h1>
                    <p className="mt-1 text-gray-500">Vue d'ensemble de votre espace de gestion</p>
                </div>
                <div className="flex gap-3">
                    <button className="bg-white border border-gray-200 text-gray-700 px-4 py-2 rounded-lg font-bold text-sm hover:bg-gray-50">
                        <BarChart3 className="w-4 h-4 inline mr-2" />
                        Rapport
                    </button>
                    <button className="bg-black text-white px-4 py-2 rounded-lg font-bold text-sm hover:bg-zinc-800">
                        Nouvelle Tâche
                    </button>
                </div>
            </div>

            {/* KPI Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-10">
                <StatCard
                    title="Réservations Aujourd'hui"
                    value={stats.todayReservations.toString()}
                    icon={Calendar}
                    color="blue"
                    trend=""
                    trendUp={true}
                />
                <StatCard
                    title="Locations Actives"
                    value={stats.activeRentals.toString()}
                    icon={Car}
                    color="green"
                    trend=""
                    trendUp={true}
                />
                <StatCard
                    title="En Attente d'Approbation"
                    value={stats.pendingApprovals.toString()}
                    icon={Clock}
                    color="yellow"
                    trend=""
                    trendUp={false}
                />
                <StatCard
                    title="Revenus Hebdomadaires"
                    value={`${stats.weeklyRevenue.toLocaleString()} CFA`}
                    icon={DollarSign}
                    color="green"
                    trend=""
                    trendUp={true}
                />
                <StatCard
                    title="Taux de Complétion"
                    value={`${stats.completionRate}%`}
                    icon={CheckCircle}
                    color="indigo"
                    trend=""
                    trendUp={true}
                />
                <StatCard
                    title="Membres d'Équipe"
                    value={stats.teamMembers.toString()}
                    icon={Users}
                    color="purple"
                    trend=""
                    trendUp={true}
                />
            </div>

            {/* Réservations Récentes */}
            <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                <div className="px-6 py-4 border-b border-gray-100">
                    <h2 className="text-lg font-bold text-gray-900 flex items-center gap-2">
                        <Clock className="w-5 h-5 text-blue-500" />
                        Réservations Récentes
                    </h2>
                </div>
                <div className="overflow-x-auto">
                    {recentReservations.length > 0 ? (
                        <table className="min-w-full divide-y divide-gray-200">
                            <thead className="bg-gray-50">
                                <tr>
                                    <th className="px-6 py-3 text-left text-xs font-bold text-gray-700 uppercase">
                                        Référence
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-bold text-gray-700 uppercase">
                                        Client
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-bold text-gray-700 uppercase">
                                        Véhicule
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-bold text-gray-700 uppercase">
                                        Date Début
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-bold text-gray-700 uppercase">
                                        Montant
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-bold text-gray-700 uppercase">
                                        Statut
                                    </th>
                                    <th className="px-6 py-3 text-right text-xs font-bold text-gray-700 uppercase">
                                        Actions
                                    </th>
                                </tr>
                            </thead>
                            <tbody className="bg-white divide-y divide-gray-200">
                                {recentReservations.map((reservation) => (
                                    <tr key={reservation.id} className="hover:bg-gray-50">
                                        <td className="px-6 py-4 whitespace-nowrap">
                                            <span className="font-mono text-sm font-bold text-gray-900">
                                                {reservation.confirmationNumber}
                                            </span>
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                                            {reservation.customerName}
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">
                                            {reservation.vehicleName}
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                            {formatDate(reservation.startDate)}
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap text-sm font-bold text-gray-900">
                                            {reservation.totalPrice.toLocaleString()} CFA
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap">
                                            {getStatusBadge(reservation.status)}
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                            <button className="text-black hover:text-zinc-700 font-bold">
                                                Voir
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    ) : (
                        <div className="text-center py-12">
                            <Calendar className="mx-auto h-12 w-12 text-gray-300" />
                            <h3 className="mt-2 text-sm font-medium text-gray-900">Aucune réservation récente</h3>
                            <p className="mt-1 text-sm text-gray-500">Les réservations apparaîtront ici.</p>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default ManagerOverview;
