import React, { useState, useEffect } from 'react';
import {
    FileText,
    TrendingUp,
    DollarSign,
    Calendar,
    Car,
    Users,
    Download,
    Loader,
    AlertTriangle
} from 'lucide-react';
import { reservationService } from '../../../services/reservationService';
import { vehicleService } from '../../../services/vehicleService';
import { userService } from '../../../services/userService';

interface ReportData {
    totalRevenue: number;
    totalReservations: number;
    averageRevenuePerReservation: number;
    completedReservations: number;
    cancelledReservations: number;
    topVehicles: Array<{ vehicleId: number; vehicleName: string; count: number; revenue: number }>;
    topClients: Array<{ userId: number; userName: string; totalSpent: number; reservationCount: number }>;
    monthlyRevenue: { [month: string]: number };
    statusBreakdown: { [status: string]: number };
}

const AdminReports: React.FC = () => {
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [reportData, setReportData] = useState<ReportData | null>(null);

    useEffect(() => {
        fetchReportData();
    }, []);

    const fetchReportData = async () => {
        try {
            setLoading(true);
            setError(null);

            console.log('📊 [AdminReports] Fetching report data...');

            // Récupérer toutes les données nécessaires en parallèle
            // Récupérer toutes les données nécessaires en parallèle
            const [reservationsData, vehiclesData, usersData] = await Promise.all([
                reservationService.getAllReservations(),
                vehicleService.getAllVehicles().catch(err => {
                    console.error('Failed to load vehicles:', err);
                    return [];
                }),
                userService.getAllUsers().catch(err => {
                    console.error('Failed to load users:', err);
                    return [];
                })
            ]);

            // Handle potential pagination structures (Spring Data REST / Page<T>)
            const vehicles = Array.isArray(vehiclesData) ? vehiclesData : (vehiclesData.content || vehiclesData.data || []);
            const users = Array.isArray(usersData) ? usersData : (usersData.content || usersData.data || []);
            const reservations = Array.isArray(reservationsData) ? reservationsData : (reservationsData.content || reservationsData.data || []);

            console.log('📊 [AdminReports] Processing:', {
                reservations: reservations.length,
                vehicles: vehicles.length,
                users: users.length
            });

            // Calculer les statistiques
            const data = calculateReportData(reservations, vehicles, users);
            setReportData(data);

            console.log('✅ [AdminReports] Report data calculated');
        } catch (error: any) {
            console.error('❌ [AdminReports] Error:', error);
            setError('Erreur lors du chargement des rapports');
        } finally {
            setLoading(false);
        }
    };

    const calculateReportData = (reservations: any[], vehicles: any[], users: any[]): ReportData => {
        // Revenus totaux
        const totalRevenue = reservations.reduce((sum, res) => sum + (res.totalPrice || 0), 0);

        // Réservations complétées et annulées
        const completedReservations = reservations.filter(r => r.status === 'COMPLETED').length;
        const cancelledReservations = reservations.filter(r => r.status === 'CANCELLED').length;

        // Moyenne par réservation
        const averageRevenuePerReservation = reservations.length > 0 ? totalRevenue / reservations.length : 0;

        // Top véhicules
        const vehicleStats = new Map<number, { count: number; revenue: number }>();
        reservations.forEach(res => {
            const existing = vehicleStats.get(res.vehicleId) || { count: 0, revenue: 0 };
            vehicleStats.set(res.vehicleId, {
                count: existing.count + 1,
                revenue: existing.revenue + (res.totalPrice || 0)
            });
        });

        const topVehicles = Array.from(vehicleStats.entries())
            .map(([vehicleId, stats]) => {
                const vehicle = vehicles.find(v => v.id === vehicleId);
                return {
                    vehicleId,
                    vehicleName: vehicle ? `${vehicle.brand} ${vehicle.model}` : `Véhicule #${vehicleId}`,
                    count: stats.count,
                    revenue: stats.revenue
                };
            })
            .sort((a, b) => b.revenue - a.revenue)
            .slice(0, 10);

        // Top clients
        const clientStats = new Map<number, { totalSpent: number; count: number }>();
        reservations.forEach(res => {
            const existing = clientStats.get(res.userId) || { totalSpent: 0, count: 0 };
            clientStats.set(res.userId, {
                totalSpent: existing.totalSpent + (res.totalPrice || 0),
                count: existing.count + 1
            });
        });

        const topClients = Array.from(clientStats.entries())
            .map(([userId, stats]) => {
                const user = users.find(u => u.id === userId);
                return {
                    userId,
                    userName: user ? `${user.firstName} ${user.lastName}` : `Client #${userId}`,
                    totalSpent: stats.totalSpent,
                    reservationCount: stats.count
                };
            })
            .sort((a, b) => b.totalSpent - a.totalSpent)
            .slice(0, 10);

        // Revenus mensuels
        const monthlyRevenue: { [month: string]: number } = {};
        reservations.forEach(res => {
            const date = new Date(res.startDate);
            const monthKey = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
            monthlyRevenue[monthKey] = (monthlyRevenue[monthKey] || 0) + (res.totalPrice || 0);
        });

        // Répartition par statut
        const statusBreakdown: { [status: string]: number } = {};
        reservations.forEach(res => {
            statusBreakdown[res.status] = (statusBreakdown[res.status] || 0) + 1;
        });

        return {
            totalRevenue,
            totalReservations: reservations.length,
            averageRevenuePerReservation,
            completedReservations,
            cancelledReservations,
            topVehicles,
            topClients,
            monthlyRevenue,
            statusBreakdown
        };
    };

    const handleExport = () => {
        if (!reportData) return;

        // Créer un CSV simple pour l'export
        const csvContent = [
            'Rapport Financier - ACA Locations',
            '',
            'KPIs Principaux',
            `Revenus Totaux,${reportData.totalRevenue} CFA`,
            `Total Réservations,${reportData.totalReservations}`,
            `Moyenne par Réservation,${Math.round(reportData.averageRevenuePerReservation)} CFA`,
            `Réservations Complétées,${reportData.completedReservations}`,
            `Réservations Annulées,${reportData.cancelledReservations}`,
            '',
            'Top 10 Véhicules',
            'Véhicule,Nombre de Locations,Revenus (CFA)',
            ...reportData.topVehicles.map(v => `${v.vehicleName},${v.count},${v.revenue}`),
            '',
            'Top 10 Clients',
            'Client,Nombre de Réservations,Total Dépensé (CFA)',
            ...reportData.topClients.map(c => `${c.userName},${c.reservationCount},${c.totalSpent}`)
        ].join('\n');

        const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
        const link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.download = `rapport-${new Date().toISOString().split('T')[0]}.csv`;
        link.click();

        console.log('✅ [AdminReports] Report exported');
    };

    // Loading state
    if (loading) {
        return (
            <div className="max-w-7xl mx-auto px-4 py-8">
                <div className="flex items-center justify-center h-64">
                    <div className="text-center">
                        <Loader className="w-12 h-12 animate-spin text-yellow-500 mx-auto mb-4" />
                        <p className="text-gray-600 font-medium">Génération des rapports...</p>
                        <p className="text-sm text-gray-400 mt-2">Analyse des données en cours...</p>
                    </div>
                </div>
            </div>
        );
    }

    // Error state
    if (error) {
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
                        onClick={fetchReportData}
                        className="mt-4 bg-red-600 text-white px-4 py-2 rounded-lg font-bold text-sm hover:bg-red-700"
                    >
                        Réessayer
                    </button>
                </div>
            </div>
        );
    }

    if (!reportData) return null;

    return (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
            {/* Header */}
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                <div>
                    <h1 className="text-3xl font-black text-gray-900 flex items-center gap-3">
                        <FileText className="w-8 h-8" />
                        Rapports & Analyses
                    </h1>
                    <p className="mt-1 text-gray-500">Statistiques et performances de la plateforme</p>
                </div>
                <button
                    onClick={handleExport}
                    className="flex items-center gap-2 bg-yellow-400 hover:bg-yellow-300 text-black px-6 py-3 rounded-xl font-bold shadow-lg hover:shadow-xl transition-all transform hover:-translate-y-1"
                >
                    <Download className="w-5 h-5" />
                    Exporter CSV
                </button>
            </div>

            {/* KPIs Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                <div className="bg-gradient-to-br from-yellow-400 to-yellow-500 rounded-2xl p-6 text-black shadow-lg">
                    <div className="flex items-center justify-between mb-2">
                        <DollarSign className="w-8 h-8" />
                        <TrendingUp className="w-5 h-5" />
                    </div>
                    <p className="text-sm font-bold opacity-90">Revenus Totaux</p>
                    <p className="text-3xl font-black mt-2">{reportData.totalRevenue.toLocaleString()} CFA</p>
                </div>

                <div className="bg-white rounded-2xl p-6 border border-gray-200 shadow-sm">
                    <div className="flex items-center justify-between mb-2">
                        <Calendar className="w-8 h-8 text-blue-500" />
                    </div>
                    <p className="text-sm font-bold text-gray-500">Total Réservations</p>
                    <p className="text-3xl font-black mt-2 text-gray-900">{reportData.totalReservations}</p>
                </div>

                <div className="bg-white rounded-2xl p-6 border border-gray-200 shadow-sm">
                    <div className="flex items-center justify-between mb-2">
                        <DollarSign className="w-8 h-8 text-green-500" />
                    </div>
                    <p className="text-sm font-bold text-gray-500">Moyenne / Résa</p>
                    <p className="text-3xl font-black mt-2 text-gray-900">
                        {Math.round(reportData.averageRevenuePerReservation).toLocaleString()} CFA
                    </p>
                </div>

                <div className="bg-white rounded-2xl p-6 border border-gray-200 shadow-sm">
                    <div className="flex items-center justify-between mb-2">
                        <Car className="w-8 h-8 text-purple-500" />
                    </div>
                    <p className="text-sm font-bold text-gray-500">Taux Complétion</p>
                    <p className="text-3xl font-black mt-2 text-gray-900">
                        {reportData.totalReservations > 0
                            ? Math.round((reportData.completedReservations / reportData.totalReservations) * 100)
                            : 0}%
                    </p>
                </div>
            </div>

            {/* Statistiques par statut */}
            <div className="bg-white rounded-2xl p-6 border border-gray-200 shadow-sm">
                <h3 className="text-lg font-bold text-gray-900 mb-4">Répartition par Statut</h3>
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                    {Object.entries(reportData.statusBreakdown).map(([status, count]) => (
                        <div key={status} className="text-center p-4 bg-gray-50 rounded-xl">
                            <p className="text-2xl font-black text-gray-900">{count}</p>
                            <p className="text-sm text-gray-500 mt-1">{status}</p>
                        </div>
                    ))}
                </div>
            </div>

            {/* Top Véhicules */}
            <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                <div className="px-6 py-4 border-b border-gray-100 flex items-center justify-between">
                    <h3 className="text-lg font-bold text-gray-900 flex items-center gap-2">
                        <Car className="w-5 h-5 text-yellow-500" />
                        Top 10 Véhicules par Revenus
                    </h3>
                </div>
                <div className="overflow-x-auto">
                    <table className="w-full">
                        <thead className="bg-gray-50">
                            <tr>
                                <th className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase">#</th>
                                <th className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase">Véhicule</th>
                                <th className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase">Locations</th>
                                <th className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase">Revenus</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-100">
                            {reportData.topVehicles.map((vehicle, index) => (
                                <tr key={vehicle.vehicleId} className="hover:bg-gray-50">
                                    <td className="px-6 py-4 text-sm font-bold text-gray-900">#{index + 1}</td>
                                    <td className="px-6 py-4 text-sm font-medium text-gray-900">{vehicle.vehicleName}</td>
                                    <td className="px-6 py-4 text-sm text-gray-600">{vehicle.count} fois</td>
                                    <td className="px-6 py-4 text-sm font-bold text-gray-900">
                                        {vehicle.revenue.toLocaleString()} CFA
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>

            {/* Top Clients */}
            <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                <div className="px-6 py-4 border-b border-gray-100 flex items-center justify-between">
                    <h3 className="text-lg font-bold text-gray-900 flex items-center gap-2">
                        <Users className="w-5 h-5 text-blue-500" />
                        Top 10 Clients par Dépenses
                    </h3>
                </div>
                <div className="overflow-x-auto">
                    <table className="w-full">
                        <thead className="bg-gray-50">
                            <tr>
                                <th className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase">#</th>
                                <th className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase">Client</th>
                                <th className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase">Réservations</th>
                                <th className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase">Total Dépensé</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-100">
                            {reportData.topClients.map((client, index) => (
                                <tr key={client.userId} className="hover:bg-gray-50">
                                    <td className="px-6 py-4 text-sm font-bold text-gray-900">#{index + 1}</td>
                                    <td className="px-6 py-4 text-sm font-medium text-gray-900">{client.userName}</td>
                                    <td className="px-6 py-4 text-sm text-gray-600">{client.reservationCount} fois</td>
                                    <td className="px-6 py-4 text-sm font-bold text-gray-900">
                                        {client.totalSpent.toLocaleString()} CFA
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
};

export default AdminReports;
