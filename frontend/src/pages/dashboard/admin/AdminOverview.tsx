import React from 'react';
import { Users, Car, Calendar, DollarSign, TrendingUp, AlertTriangle } from 'lucide-react';
import StatCard from '../../../components/dashboard/StatCard';

const AdminOverview: React.FC = () => {
    return (
        <div className="max-w-7xl mx-auto">
             <div className="flex flex-col md:flex-row md:items-center justify-between mb-8 gap-4">
                <div>
                    <h1 className="text-3xl font-black text-gray-900">Vue d'ensemble Admin</h1>
                    <p className="mt-1 text-gray-500">Performances et métriques clés de la plateforme.</p>
                </div>
                <div className="flex gap-3">
                    <button className="bg-white border border-gray-200 text-gray-700 px-4 py-2 rounded-lg font-bold text-sm hover:bg-gray-50">
                        Exporter
                    </button>
                    <button className="bg-black text-white px-4 py-2 rounded-lg font-bold text-sm hover:bg-zinc-800">
                        Rapport Hebdo
                    </button>
                </div>
            </div>

            {/* KPI Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-10">
                <StatCard 
                    title="Revenus Mensuels" 
                    value="15.2M CFA" 
                    icon={DollarSign} 
                    color="yellow" 
                    trend="+18%" 
                    trendUp={true} 
                />
                <StatCard 
                    title="Réservations" 
                    value="142" 
                    icon={Calendar} 
                    color="blue" 
                    trend="+5%" 
                    trendUp={true} 
                />
                <StatCard 
                    title="Flotte Active" 
                    value="85%" 
                    icon={Car} 
                    color="green" 
                    trend="-2%" 
                    trendUp={false} 
                />
                <StatCard 
                    title="Nouveaux Clients" 
                    value="24" 
                    icon={Users} 
                    color="indigo" 
                    trend="+12%" 
                    trendUp={true} 
                />
            </div>

            {/* Quick Actions & Recent Activity (Placeholder for detailed components) */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                <div className="bg-white rounded-2xl p-6 border border-gray-100 shadow-sm">
                    <h3 className="text-lg font-bold text-gray-900 mb-6 flex items-center gap-2">
                        <TrendingUp className="w-5 h-5 text-yellow-500" />
                        Véhicules les plus loués
                    </h3>
                    {/* List of top cars would go here */}
                    <div className="space-y-4">
                        {[1, 2, 3].map((_, i) => (
                            <div key={i} className="flex items-center justify-between p-3 hover:bg-gray-50 rounded-xl transition-colors cursor-pointer group">
                                <div className="flex items-center gap-4">
                                    <div className="h-10 w-10 bg-gray-200 rounded-lg overflow-hidden">
                                        <img src="/images/suv_car.png" className="w-full h-full object-cover" alt="Car" />
                                    </div>
                                    <div>
                                        <p className="font-bold text-gray-900 group-hover:text-yellow-500 transition-colors">Lamborghini Urus</p>
                                        <p className="text-xs text-gray-400">SUV Luxe</p>
                                    </div>
                                </div>
                                <span className="font-mono font-bold text-gray-900">12 Loc.</span>
                            </div>
                        ))}
                    </div>
                </div>

                <div className="bg-white rounded-2xl p-6 border border-gray-100 shadow-sm">
                    <h3 className="text-lg font-bold text-gray-900 mb-6 flex items-center gap-2">
                        <AlertTriangle className="w-5 h-5 text-red-500" />
                        Alertes Maintenance
                    </h3>
                    <div className="space-y-3">
                         <div className="flex items-center p-3 bg-red-50 border border-red-100 rounded-xl">
                            <AlertTriangle className="w-5 h-5 text-red-500 mr-3" />
                            <div className="flex-1">
                                <p className="text-sm font-bold text-red-900">Ferrari F8 Tributo</p>
                                <p className="text-xs text-red-700">Contrôle technique requis (-2 jours)</p>
                            </div>
                            <button className="px-3 py-1 bg-white text-red-600 text-xs font-bold rounded-lg border border-red-200 hover:bg-red-50">
                                Voir
                            </button>
                        </div>
                        <div className="flex items-center p-3 bg-yellow-50 border border-yellow-100 rounded-xl">
                            <Clock className="w-5 h-5 text-yellow-600 mr-3" />
                            <div className="flex-1">
                                <p className="text-sm font-bold text-yellow-900">BMW M4</p>
                                <p className="text-xs text-yellow-700">Vidange dans 500km</p>
                            </div>
                            <button className="px-3 py-1 bg-white text-yellow-600 text-xs font-bold rounded-lg border border-yellow-200 hover:bg-yellow-50">
                                Planifier
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

// Helper
const Clock = ({ className }: {className?: string}) => (
    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className={className}>
        <path strokeLinecap="round" strokeLinejoin="round" d="M12 6v6h4.5m4.5 0a9 9 0 11-18 0 9 9 0 0118 0z" />
    </svg>
);


export default AdminOverview;
