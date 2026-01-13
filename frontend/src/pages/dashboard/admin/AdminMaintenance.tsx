import React, { useState, useEffect } from 'react';
import {
    Wrench,
    AlertTriangle,
    CheckCircle,
    Clock,
    Car,
    Loader,
    Calendar
} from 'lucide-react';
import { vehicleService } from '../../../services/vehicleService';

interface VehicleWithMaintenance {
    id: number;
    brand: string;
    model: string;
    licensePlate: string;
    mileage: number;
    updatedAt: string;
    status: string;
    images?: string[];
    maintenanceStatus: 'OK' | 'ATTENTION' | 'URGENT';
    maintenanceReason: string;
    daysSinceUpdate: number;
    nextMaintenanceDue: string;
}

const AdminMaintenance: React.FC = () => {
    const [vehicles, setVehicles] = useState<VehicleWithMaintenance[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [filter, setFilter] = useState<'ALL' | 'OK' | 'ATTENTION' | 'URGENT'>('ALL');

    useEffect(() => {
        fetchVehiclesWithMaintenance();
    }, []);

    const calculateMaintenanceStatus = (vehicle: any): VehicleWithMaintenance => {
        const now = new Date();
        const lastUpdate = new Date(vehicle.updatedAt || vehicle.createdAt);
        const daysSinceUpdate = Math.floor((now.getTime() - lastUpdate.getTime()) / (1000 * 60 * 60 * 24));

        // Règles de maintenance:
        // - Urgent: Plus de 180 jours (6 mois) ou plus de 10,000 km
        // - Attention: Entre 150-180 jours ou 8,000-10,000 km
        // - OK: Moins de 150 jours et moins de 8,000 km

        let maintenanceStatus: 'OK' | 'ATTENTION' | 'URGENT' = 'OK';
        let maintenanceReason = 'Entretien à jour';
        let nextMaintenanceDue = '';

        // Vérification par date
        if (daysSinceUpdate > 180) {
            maintenanceStatus = 'URGENT';
            maintenanceReason = `Dernière maintenance il y a ${daysSinceUpdate} jours (>6 mois)`;
            nextMaintenanceDue = 'Maintenance requise immédiatement';
        } else if (daysSinceUpdate > 150) {
            maintenanceStatus = 'ATTENTION';
            maintenanceReason = `Dernière maintenance il y a ${daysSinceUpdate} jours`;
            const daysRemaining = 180 - daysSinceUpdate;
            nextMaintenanceDue = `Dans ${daysRemaining} jours`;
        } else {
            const daysRemaining = 180 - daysSinceUpdate;
            nextMaintenanceDue = `Dans ${daysRemaining} jours environ`;
        }

        // Vérification par kilométrage (si disponible)
        if (vehicle.mileage) {
            const mileageSinceLastMaintenance = vehicle.mileage % 10000;
            if (vehicle.mileage > 10000 && mileageSinceLastMaintenance < 1000) {
                if (maintenanceStatus !== 'URGENT') {
                    maintenanceStatus = 'URGENT';
                    maintenanceReason = `Kilométrage: ${vehicle.mileage.toLocaleString()} km (maintenance tous les 10,000 km)`;
                }
            } else if (vehicle.mileage > 8000 && maintenanceStatus === 'OK') {
                const kmRemaining = 10000 - mileageSinceLastMaintenance;
                maintenanceStatus = 'ATTENTION';
                maintenanceReason = `Prochain entretien dans ${kmRemaining.toLocaleString()} km`;
            }
        }

        return {
            ...vehicle,
            maintenanceStatus,
            maintenanceReason,
            daysSinceUpdate,
            nextMaintenanceDue
        };
    };

    const fetchVehiclesWithMaintenance = async () => {
        try {
            setLoading(true);
            setError(null);

            console.log('🔧 [AdminMaintenance] Fetching vehicles...');

            const response = await vehicleService.getAllVehicles();
            const allVehicles = response.content || response;

            // Calculer le statut de maintenance pour chaque véhicule
            const vehiclesWithMaintenance = allVehicles.map(calculateMaintenanceStatus);

            // Trier par priorité : URGENT > ATTENTION > OK
            vehiclesWithMaintenance.sort((a, b) => {
                const priority = { URGENT: 3, ATTENTION: 2, OK: 1 };
                return priority[b.maintenanceStatus] - priority[a.maintenanceStatus];
            });

            setVehicles(vehiclesWithMaintenance);
            console.log('✅ [AdminMaintenance] Loaded', vehiclesWithMaintenance.length, 'vehicles');
        } catch (error: any) {
            console.error('❌ [AdminMaintenance] Error:', error);
            setError('Erreur lors du chargement des données de maintenance');
        } finally {
            setLoading(false);
        }
    };

    const filteredVehicles = filter === 'ALL'
        ? vehicles
        : vehicles.filter(v => v.maintenanceStatus === filter);

    const urgentCount = vehicles.filter(v => v.maintenanceStatus === 'URGENT').length;
    const attentionCount = vehicles.filter(v => v.maintenanceStatus === 'ATTENTION').length;
    const okCount = vehicles.filter(v => v.maintenanceStatus === 'OK').length;

    // Loading state
    if (loading) {
        return (
            <div className="max-w-7xl mx-auto px-4 py-8">
                <div className="flex items-center justify-center h-64">
                    <div className="text-center">
                        <Loader className="w-12 h-12 animate-spin text-yellow-500 mx-auto mb-4" />
                        <p className="text-gray-600 font-medium">Analyse de la flotte...</p>
                        <p className="text-sm text-gray-400 mt-2">Calcul des statuts de maintenance...</p>
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
                        onClick={fetchVehiclesWithMaintenance}
                        className="mt-4 bg-red-600 text-white px-4 py-2 rounded-lg font-bold text-sm hover:bg-red-700"
                    >
                        Réessayer
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
            {/* Header */}
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                <div>
                    <h1 className="text-3xl font-black text-gray-900 flex items-center gap-3">
                        <Wrench className="w-8 h-8" />
                        Suivi Maintenance
                    </h1>
                    <p className="mt-1 text-gray-500">Planifiez et suivez l'entretien de votre flotte</p>
                </div>
            </div>

            {/* Status Cards */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div className="bg-red-50 border-2 border-red-200 rounded-2xl p-6">
                    <div className="flex items-center justify-between mb-2">
                        <AlertTriangle className="w-8 h-8 text-red-600" />
                        <span className="text-3xl font-black text-red-600">{urgentCount}</span>
                    </div>
                    <p className="text-sm font-bold text-red-900">Maintenance Urgente</p>
                    <p className="text-xs text-red-700 mt-1">Action immédiate requise</p>
                </div>

                <div className="bg-yellow-50 border-2 border-yellow-200 rounded-2xl p-6">
                    <div className="flex items-center justify-between mb-2">
                        <Clock className="w-8 h-8 text-yellow-600" />
                        <span className="text-3xl font-black text-yellow-600">{attentionCount}</span>
                    </div>
                    <p className="text-sm font-bold text-yellow-900">À Surveiller</p>
                    <p className="text-xs text-yellow-700 mt-1">Maintenance à planifier</p>
                </div>

                <div className="bg-green-50 border-2 border-green-200 rounded-2xl p-6">
                    <div className="flex items-center justify-between mb-2">
                        <CheckCircle className="w-8 h-8 text-green-600" />
                        <span className="text-3xl font-black text-green-600">{okCount}</span>
                    </div>
                    <p className="text-sm font-bold text-green-900">Entretien OK</p>
                    <p className="text-xs text-green-700 mt-1">Véhicules à jour</p>
                </div>
            </div>

            {/* Filters */}
            <div className="flex gap-2 overflow-x-auto pb-2">
                <FilterButton active={filter === 'ALL'} onClick={() => setFilter('ALL')} label={`Tous (${vehicles.length})`} />
                <FilterButton active={filter === 'URGENT'} onClick={() => setFilter('URGENT')} label={`Urgent (${urgentCount})`} color="red" />
                <FilterButton active={filter === 'ATTENTION'} onClick={() => setFilter('ATTENTION')} label={`Attention (${attentionCount})`} color="yellow" />
                <FilterButton active={filter === 'OK'} onClick={() => setFilter('OK')} label={`OK (${okCount})`} color="green" />
            </div>

            {/* Vehicles Table */}
            <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                <table className="w-full">
                    <thead className="bg-gray-50">
                        <tr>
                            <th className="px-6 py-4 text-left text-xs font-bold text-gray-500 uppercase">Véhicule</th>
                            <th className="px-6 py-4 text-left text-xs font-bold text-gray-500 uppercase">Kilométrage</th>
                            <th className="px-6 py-4 text-left text-xs font-bold text-gray-500 uppercase">Dernière MAJ</th>
                            <th className="px-6 py-4 text-left text-xs font-bold text-gray-500 uppercase">Statut</th>
                            <th className="px-6 py-4 text-left text-xs font-bold text-gray-500 uppercase">Détails</th>
                            <th className="px-6 py-4 text-left text-xs font-bold text-gray-500 uppercase">Prochaine Maintenance</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-100">
                        {filteredVehicles.length > 0 ? (
                            filteredVehicles.map((vehicle) => (
                                <tr key={vehicle.id} className="hover:bg-gray-50">
                                    <td className="px-6 py-4">
                                        <div className="flex items-center gap-3">
                                            {vehicle.images && vehicle.images[0] && (
                                                <div className="h-12 w-12 bg-gray-200 rounded-lg overflow-hidden flex-shrink-0">
                                                    <img
                                                        src={vehicle.images[0]}
                                                        alt={vehicle.brand}
                                                        className="w-full h-full object-cover"
                                                        onError={(e) => {
                                                            (e.target as HTMLImageElement).style.display = 'none';
                                                        }}
                                                    />
                                                </div>
                                            )}
                                            <div>
                                                <p className="font-bold text-gray-900">{vehicle.brand} {vehicle.model}</p>
                                                <p className="text-xs text-gray-500">{vehicle.licensePlate}</p>
                                            </div>
                                        </div>
                                    </td>
                                    <td className="px-6 py-4">
                                        <p className="text-sm font-medium text-gray-900">
                                            {vehicle.mileage?.toLocaleString() || 'N/A'} km
                                        </p>
                                    </td>
                                    <td className="px-6 py-4">
                                        <p className="text-sm text-gray-600">{vehicle.daysSinceUpdate} jours</p>
                                        <p className="text-xs text-gray-400">
                                            {new Date(vehicle.updatedAt).toLocaleDateString()}
                                        </p>
                                    </td>
                                    <td className="px-6 py-4">
                                        <MaintenanceStatusBadge status={vehicle.maintenanceStatus} />
                                    </td>
                                    <td className="px-6 py-4">
                                        <p className="text-sm text-gray-700">{vehicle.maintenanceReason}</p>
                                    </td>
                                    <td className="px-6 py-4">
                                        <p className="text-sm font-medium text-gray-900">{vehicle.nextMaintenanceDue}</p>
                                    </td>
                                </tr>
                            ))
                        ) : (
                            <tr>
                                <td colSpan={6} className="px-6 py-12 text-center text-gray-500">
                                    <Car className="w-12 h-12 mx-auto mb-3 text-gray-300" />
                                    <p className="font-medium">Aucun véhicule dans cette catégorie</p>
                                </td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

const FilterButton = ({
    active,
    onClick,
    label,
    color = 'default'
}: {
    active: boolean;
    onClick: () => void;
    label: string;
    color?: 'default' | 'red' | 'yellow' | 'green';
}) => {
    const colorClasses = {
        default: active ? 'bg-black text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200',
        red: active ? 'bg-red-600 text-white' : 'bg-red-100 text-red-600 hover:bg-red-200',
        yellow: active ? 'bg-yellow-600 text-white' : 'bg-yellow-100 text-yellow-600 hover:bg-yellow-200',
        green: active ? 'bg-green-600 text-white' : 'bg-green-100 text-green-600 hover:bg-green-200'
    };

    return (
        <button
            onClick={onClick}
            className={`px-4 py-2 rounded-lg font-bold transition-colors whitespace-nowrap ${colorClasses[color]}`}
        >
            {label}
        </button>
    );
};

const MaintenanceStatusBadge = ({ status }: { status: string }) => {
    const config: Record<string, { color: string; icon: React.ElementType; label: string }> = {
        URGENT: { color: 'bg-red-100 text-red-800 border-red-200', icon: AlertTriangle, label: 'Urgent' },
        ATTENTION: { color: 'bg-yellow-100 text-yellow-800 border-yellow-200', icon: Clock, label: 'Attention' },
        OK: { color: 'bg-green-100 text-green-800 border-green-200', icon: CheckCircle, label: 'OK' }
    };

    const { color, icon: Icon, label } = config[status] || config.OK;

    return (
        <span className={`px-3 py-1 rounded-full text-xs font-bold inline-flex items-center gap-1 border ${color}`}>
            <Icon className="w-3 h-3" /> {label}
        </span>
    );
};

export default AdminMaintenance;
