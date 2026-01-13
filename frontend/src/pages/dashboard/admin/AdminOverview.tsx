import React, { useState, useEffect } from 'react';
import { Users, Car, Calendar, DollarSign, TrendingUp, AlertTriangle, Loader } from 'lucide-react';
import StatCard from '../../../components/dashboard/StatCard';
import { adminService, AdminDashboard } from '../../../services/adminService';
import { vehicleService } from '../../../services/vehicleService';

const AdminOverview: React.FC = () => {
    const [dashboardData, setDashboardData] = useState<AdminDashboard | null>(null);
    const [topVehicles, setTopVehicles] = useState<any[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                setLoading(true);
                setError(null);

                console.log('🔄 [AdminOverview] Fetching dashboard data...');

                // Charger les données du dashboard et les véhicules tendance en parallèle
                const [dashboard, trending] = await Promise.all([
                    adminService.getAdminDashboard(),
                    vehicleService.getTrendingVehicles(3).catch(err => {
                        console.warn('⚠️ Could not load trending vehicles:', err);
                        return [];
                    })
                ]);

                setDashboardData(dashboard);
                setTopVehicles(trending);
                console.log('✅ [AdminOverview] Dashboard data loaded successfully');
            } catch (error: any) {
                console.error('❌ [AdminOverview] Error fetching dashboard:', error);
                setError(error.response?.data?.message || 'Erreur lors du chargement du dashboard');
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, []);

    // Fonction pour exporter les données en CSV
    const handleExport = () => {
        if (!dashboardData) return;

        const today = new Date().toLocaleDateString('fr-FR');
        const csvContent = [
            ['Métrique', 'Valeur'],
            ['Date', today],
            ['Revenus Aujourd\'hui', `${dashboardData.todayRevenue} CFA`],
            ['Réservations Aujourd\'hui', dashboardData.todayReservations],
            ['Locations Actives', dashboardData.activeRentals],
            ['Total Utilisateurs', dashboardData.totalUsers],
            ['Paiements en Attente', dashboardData.pendingPayments || 0],
            [''],
            ['Véhicules Tendance'],
            ['Marque/Modèle', 'Prix Journalier', 'Catégorie'],
            ...topVehicles.map(v => [
                `${v.brand} ${v.model}`,
                `${v.dailyPrice} CFA`,
                v.category || 'N/A'
            ])
        ].map(row => row.join(',')).join('\n');

        const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' });
        const link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.download = `dashboard-admin-${new Date().toISOString().split('T')[0]}.csv`;
        link.click();
    };

    // Fonction pour générer un rapport hebdomadaire
    const handleWeeklyReport = () => {
        if (!dashboardData) return;

        const today = new Date();
        const weekStart = new Date(today.setDate(today.getDate() - today.getDay()));
        const weekEnd = new Date(today.setDate(today.getDate() - today.getDay() + 6));

        const reportContent = `
==============================================
        RAPPORT HEBDOMADAIRE GDLDV
==============================================

Période: ${weekStart.toLocaleDateString('fr-FR')} - ${weekEnd.toLocaleDateString('fr-FR')}
Généré le: ${new Date().toLocaleString('fr-FR')}

----------------------------------------------
📊 MÉTRIQUES CLÉS
----------------------------------------------
Revenus Aujourd'hui:           ${dashboardData.todayRevenue.toLocaleString()} CFA
Réservations Aujourd'hui:      ${dashboardData.todayReservations}
Locations Actives:             ${dashboardData.activeRentals}
Total Utilisateurs:            ${dashboardData.totalUsers}
Paiements en Attente:          ${dashboardData.pendingPayments || 0}

----------------------------------------------
🚗 VÉHICULES TENDANCE
----------------------------------------------
${topVehicles.map((v, i) => `${i + 1}. ${v.brand} ${v.model} - ${v.dailyPrice.toLocaleString()} CFA/j (${v.category || 'N/A'})`).join('\n')}

----------------------------------------------
⚠️ TÂCHES URGENTES
----------------------------------------------
${dashboardData.urgentTasks && dashboardData.urgentTasks.length > 0
    ? dashboardData.urgentTasks.map(t => `[${t.priority}] ${t.type}: ${t.description}`).join('\n')
    : 'Aucune tâche urgente'}

==============================================
        © GDLDV - Gestion des Locations
==============================================
`;

        const blob = new Blob([reportContent], { type: 'text/plain;charset=utf-8;' });
        const link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.download = `rapport-hebdo-${new Date().toISOString().split('T')[0]}.txt`;
        link.click();
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
                        onClick={() => window.location.reload()}
                        className="mt-4 bg-red-600 text-white px-4 py-2 rounded-lg font-bold text-sm hover:bg-red-700"
                    >
                        Réessayer
                    </button>
                </div>
            </div>
        );
    }

    // Filtrer les tâches de maintenance pour la section "Alertes Maintenance"
    const maintenanceTasks = dashboardData?.urgentTasks?.filter(task =>
        task.type === 'MAINTENANCE' || task.priority === 'HIGH'
    ) || [];

    return (
        <div className="max-w-7xl mx-auto">
             <div className="flex flex-col md:flex-row md:items-center justify-between mb-8 gap-4">
                <div>
                    <h1 className="text-3xl font-black text-gray-900">Vue d'ensemble Admin</h1>
                    <p className="mt-1 text-gray-500">Performances et métriques clés de la plateforme.</p>
                </div>
                <div className="flex gap-3">
                    <button
                        onClick={handleExport}
                        disabled={!dashboardData}
                        className="bg-white border border-gray-200 text-gray-700 px-4 py-2 rounded-lg font-bold text-sm hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        Exporter
                    </button>
                    <button
                        onClick={handleWeeklyReport}
                        disabled={!dashboardData}
                        className="bg-black text-white px-4 py-2 rounded-lg font-bold text-sm hover:bg-zinc-800 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        Rapport Hebdo
                    </button>
                </div>
            </div>

            {/* KPI Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-10">
                <StatCard
                    title="Revenus Aujourd'hui"
                    value={`${(dashboardData?.todayRevenue || 0).toLocaleString()} CFA`}
                    icon={DollarSign}
                    color="yellow"
                    trend=""
                    trendUp={true}
                />
                <StatCard
                    title="Réservations Aujourd'hui"
                    value={`${dashboardData?.todayReservations || 0}`}
                    icon={Calendar}
                    color="blue"
                    trend=""
                    trendUp={true}
                />
                <StatCard
                    title="Locations Actives"
                    value={`${dashboardData?.activeRentals || 0}`}
                    icon={Car}
                    color="green"
                    trend=""
                    trendUp={true}
                />
                <StatCard
                    title="Total Utilisateurs"
                    value={`${dashboardData?.totalUsers || 0}`}
                    icon={Users}
                    color="indigo"
                    trend=""
                    trendUp={true}
                />
            </div>

            {/* Quick Actions & Recent Activity (Placeholder for detailed components) */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                <div className="bg-white rounded-2xl p-6 border border-gray-100 shadow-sm">
                    <h3 className="text-lg font-bold text-gray-900 mb-6 flex items-center gap-2">
                        <TrendingUp className="w-5 h-5 text-yellow-500" />
                        Véhicules Tendance
                    </h3>
                    {/* List of top trending vehicles */}
                    <div className="space-y-4">
                        {topVehicles && topVehicles.length > 0 ? (
                            topVehicles.map((vehicle, i) => (
                                <div key={vehicle.id || i} className="flex items-center justify-between p-3 hover:bg-gray-50 rounded-xl transition-colors cursor-pointer group">
                                    <div className="flex items-center gap-4">
                                        <div className="h-10 w-10 bg-gray-200 rounded-lg overflow-hidden">
                                            <img
                                                src={vehicle.images?.[0] || vehicle.image || "/images/suv_car.png"}
                                                className="w-full h-full object-cover"
                                                alt={vehicle.brand}
                                                onError={(e) => {
                                                    (e.target as HTMLImageElement).src = "/images/suv_car.png";
                                                }}
                                            />
                                        </div>
                                        <div>
                                            <p className="font-bold text-gray-900 group-hover:text-yellow-500 transition-colors">
                                                {vehicle.brand} {vehicle.model}
                                            </p>
                                            <p className="text-xs text-gray-400">{vehicle.category || 'Véhicule'}</p>
                                        </div>
                                    </div>
                                    <span className="font-mono font-bold text-gray-900">
                                        {vehicle.dailyPrice ? `${vehicle.dailyPrice.toLocaleString()} CFA/j` : 'N/A'}
                                    </span>
                                </div>
                            ))
                        ) : (
                            <p className="text-center text-gray-400 py-8">Aucun véhicule disponible</p>
                        )}
                    </div>
                </div>

                <div className="bg-white rounded-2xl p-6 border border-gray-100 shadow-sm">
                    <h3 className="text-lg font-bold text-gray-900 mb-6 flex items-center gap-2">
                        <AlertTriangle className="w-5 h-5 text-red-500" />
                        Tâches Urgentes & Alertes
                    </h3>
                    <div className="space-y-3">
                        {maintenanceTasks && maintenanceTasks.length > 0 ? (
                            maintenanceTasks.map((task, index) => {
                                const isHigh = task.priority === 'HIGH';
                                const bgColor = isHigh ? 'bg-red-50 border-red-100' : 'bg-yellow-50 border-yellow-100';
                                const textColor = isHigh ? 'text-red-900' : 'text-yellow-900';
                                const iconColor = isHigh ? 'text-red-500' : 'text-yellow-600';
                                const btnColor = isHigh ? 'text-red-600 border-red-200 hover:bg-red-50' : 'text-yellow-600 border-yellow-200 hover:bg-yellow-50';

                                return (
                                    <div key={index} className={`flex items-center p-3 ${bgColor} border rounded-xl`}>
                                        <AlertTriangle className={`w-5 h-5 ${iconColor} mr-3 flex-shrink-0`} />
                                        <div className="flex-1 min-w-0">
                                            <p className={`text-sm font-bold ${textColor} truncate`}>
                                                {task.type || 'Alerte'}
                                            </p>
                                            <p className={`text-xs ${textColor.replace('900', '700')} mt-0.5`}>
                                                {task.description}
                                            </p>
                                        </div>
                                        <button className={`px-3 py-1 bg-white ${btnColor} text-xs font-bold rounded-lg border flex-shrink-0 ml-2`}>
                                            Voir
                                        </button>
                                    </div>
                                );
                            })
                        ) : (
                            <div className="flex items-center p-3 bg-green-50 border border-green-100 rounded-xl">
                                <div className="flex-1 text-center py-4">
                                    <p className="text-sm font-bold text-green-900">✓ Aucune alerte urgente</p>
                                    <p className="text-xs text-green-700 mt-1">Tout fonctionne normalement</p>
                                </div>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default AdminOverview;
