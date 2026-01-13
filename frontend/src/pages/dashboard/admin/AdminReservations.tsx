import React, { useState, useEffect } from 'react';
import { CalendarDays, CheckCircle, XCircle, Clock, Search, Eye, Loader, AlertTriangle, Play, Flag } from 'lucide-react';
import { reservationService } from '../../../services/reservationService';
import { userService } from '../../../services/userService';
import { vehicleService } from '../../../services/vehicleService';

interface Reservation {
    id: number;
    vehicleId: number;
    userId: number;
    userName?: string;
    userEmail?: string;
    vehicleName?: string;
    vehicleImage?: string;
    startDate: string;
    endDate: string;
    totalPrice: number;
    status: 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED';
}

const AdminReservations: React.FC = () => {
    const [reservations, setReservations] = useState<Reservation[]>([]);
    const [loading, setLoading] = useState(true);
    const [searchQuery, setSearchQuery] = useState('');
    const [statusFilter, setStatusFilter] = useState('ALL');
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        fetchReservations();
    }, []);

    /**
     * Enrichit les réservations avec les noms des utilisateurs et véhicules
     */
    const enrichReservations = async (reservations: any[]): Promise<Reservation[]> => {
        console.log('🔄 [AdminReservations] Enriching', reservations.length, 'reservations...');

        const enriched = await Promise.all(
            reservations.map(async (res) => {
                try {
                    // Récupérer les données de l'utilisateur et du véhicule en parallèle
                    const [user, vehicle] = await Promise.all([
                        userService.getUserById(res.userId).catch(err => {
                            console.warn(`⚠️ Could not load user ${res.userId}:`, err.message);
                            return null;
                        }),
                        vehicleService.getVehicleById(res.vehicleId).catch(err => {
                            console.warn(`⚠️ Could not load vehicle ${res.vehicleId}:`, err.message);
                            return null;
                        })
                    ]);

                    return {
                        ...res,
                        userName: user ? `${user.firstName} ${user.lastName}` : `Client #${res.userId}`,
                        userEmail: user?.email || '',
                        vehicleName: vehicle ? `${vehicle.brand} ${vehicle.model}` : `Véhicule #${res.vehicleId}`,
                        vehicleImage: vehicle?.images?.[0] || vehicle?.image || ''
                    };
                } catch (error) {
                    console.error('❌ [AdminReservations] Error enriching reservation:', res.id, error);
                    return {
                        ...res,
                        userName: `Client #${res.userId}`,
                        vehicleName: `Véhicule #${res.vehicleId}`
                    };
                }
            })
        );

        console.log('✅ [AdminReservations] Enrichment complete');
        return enriched;
    };

    const fetchReservations = async () => {
        try {
            setLoading(true);
            setError(null);

            console.log('📋 [AdminReservations] Fetching reservations...');

            // Récupérer toutes les réservations
            const data = await reservationService.getAllReservations();

            console.log('📋 [AdminReservations] Loaded', data.length, 'reservations');

            // Enrichir avec les noms des clients et véhicules
            const enrichedData = await enrichReservations(data);

            setReservations(enrichedData);
            console.log('✅ [AdminReservations] All data loaded and enriched');
        } catch (e: any) {
            console.error('❌ [AdminReservations] Error loading reservations:', e);
            setError('Erreur lors du chargement des réservations');
        } finally {
            setLoading(false);
        }
    }

    const handleAction = async (id: number, action: 'confirm' | 'cancel' | 'start' | 'complete') => {
        let message = '';
        switch (action) {
            case 'confirm': message = 'Confirmer cette réservation ?'; break;
            case 'cancel': message = 'Annuler cette réservation ?'; break;
            case 'start': message = 'Démarrer la location (Véhicule remis) ?'; break;
            case 'complete': message = 'Terminer la location (Véhicule restitué) ?'; break;
        }

        if (!window.confirm(message)) return;

        try {
            if (action === 'confirm') await reservationService.confirmReservation(id);
            else if (action === 'cancel') await reservationService.cancelReservation(id);
            else if (action === 'start') await reservationService.startReservation(id);
            else if (action === 'complete') await reservationService.completeReservation(id);

            // Refresh list
            fetchReservations();
        } catch (e) {
            console.error(`Error during ${action}:`, e);
            alert(`Une erreur est survenue lors de l'action : ${action}`);
        }
    };

    const filteredReservations = reservations.filter(r => {
        // Safe check for undefined names if backend doesn't populate them yet
        const uName = r.userName || `Client #${r.userId}`;
        const vName = r.vehicleName || `Véhicule #${r.vehicleId}`;

        const matchesSearch = (uName.toLowerCase().includes(searchQuery.toLowerCase())) ||
            (vName.toLowerCase().includes(searchQuery.toLowerCase()));
        const matchesStatus = statusFilter === 'ALL' || r.status === statusFilter;
        return matchesSearch && matchesStatus;
    });

    // Loading state
    if (loading) {
        return (
            <div className="max-w-7xl mx-auto px-4 py-8">
                <div className="flex items-center justify-center h-64">
                    <div className="text-center">
                        <Loader className="w-12 h-12 animate-spin text-yellow-500 mx-auto mb-4" />
                        <p className="text-gray-600 font-medium">Chargement des réservations...</p>
                        <p className="text-sm text-gray-400 mt-2">Enrichissement des données en cours...</p>
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
                        onClick={fetchReservations}
                        className="mt-4 bg-red-600 text-white px-4 py-2 rounded-lg font-bold text-sm hover:bg-red-700"
                    >
                        Réessayer
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="max-w-7xl mx-auto space-y-6">
            <div className="flex flex-col md:flex-row justify-between items-center gap-4">
                <div>
                    <h1 className="text-3xl font-black text-gray-900">Réservations</h1>
                    <p className="text-gray-500">Suivi des réservations et disponibilités - {reservations.length} réservation(s)</p>
                </div>
            </div>

            {/* Filters */}
            <div className="bg-white rounded-2xl p-4 shadow-sm border border-gray-100 flex flex-col md:flex-row gap-4 items-center justify-between">
                <div className="relative w-full md:w-96">
                    <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 w-5 h-5" />
                    <input
                        type="text"
                        placeholder="Rechercher (Client, Véhicule)..."
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        className="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-yellow-400"
                    />
                </div>
                <div className="flex gap-2 text-sm overflow-x-auto pb-2 md:pb-0">
                    <FilterButton active={statusFilter === 'ALL'} onClick={() => setStatusFilter('ALL')} label="Tous" />
                    <FilterButton active={statusFilter === 'PENDING'} onClick={() => setStatusFilter('PENDING')} label="En Attente" />
                    <FilterButton active={statusFilter === 'CONFIRMED'} onClick={() => setStatusFilter('CONFIRMED')} label="Confirmées" />
                    <FilterButton active={statusFilter === 'ACTIVE'} onClick={() => setStatusFilter('ACTIVE')} label="En Cours" />
                    <FilterButton active={statusFilter === 'COMPLETED'} onClick={() => setStatusFilter('COMPLETED')} label="Terminées" />
                    <FilterButton active={statusFilter === 'CANCELLED'} onClick={() => setStatusFilter('CANCELLED')} label="Annulées" />
                </div>
            </div>

            {/* Table */}
            <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                <table className="w-full text-left">
                    <thead className="bg-gray-50 border-b border-gray-100">
                        <tr>
                            <th className="px-6 py-4 font-bold text-gray-500 text-xs uppercase">Client</th>
                            <th className="px-6 py-4 font-bold text-gray-500 text-xs uppercase">Véhicule</th>
                            <th className="px-6 py-4 font-bold text-gray-500 text-xs uppercase">Dates</th>
                            <th className="px-6 py-4 font-bold text-gray-500 text-xs uppercase">Montant</th>
                            <th className="px-6 py-4 font-bold text-gray-500 text-xs uppercase">Statut</th>
                            <th className="px-6 py-4 font-bold text-gray-500 text-xs uppercase text-right">Actions</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-100">
                        {filteredReservations.map((res) => (
                            <tr key={res.id} className="hover:bg-gray-50 transition-colors">
                                <td className="px-6 py-4">
                                    <div className="flex flex-col">
                                        <div className="font-bold text-gray-900">{res.userName || `ID: ${res.userId}`}</div>
                                        {res.userEmail && <div className="text-xs text-gray-500">{res.userEmail}</div>}
                                    </div>
                                </td>
                                <td className="px-6 py-4">
                                    <div className="flex items-center gap-3">
                                        {res.vehicleImage && (
                                            <div className="h-10 w-10 bg-gray-200 rounded-lg overflow-hidden flex-shrink-0">
                                                <img
                                                    src={res.vehicleImage}
                                                    alt={res.vehicleName}
                                                    className="w-full h-full object-cover"
                                                    onError={(e) => {
                                                        (e.target as HTMLImageElement).style.display = 'none';
                                                    }}
                                                />
                                            </div>
                                        )}
                                        <div className="font-bold text-gray-900">{res.vehicleName || `ID: ${res.vehicleId}`}</div>
                                    </div>
                                </td>
                                <td className="px-6 py-4 text-sm">
                                    <div className="flex flex-col">
                                        <span className="text-gray-900 font-medium">{new Date(res.startDate).toLocaleDateString()}</span>
                                        <span className="text-gray-400 text-xs">au</span>
                                        <span className="text-gray-900 font-medium">{new Date(res.endDate).toLocaleDateString()}</span>
                                    </div>
                                </td>
                                <td className="px-6 py-4 font-bold text-gray-900">
                                    {res.totalPrice?.toLocaleString()} CFA
                                </td>
                                <td className="px-6 py-4">
                                    <StatusBadge status={res.status} />
                                </td>
                                <td className="px-6 py-4 text-right">
                                    <div className="flex items-center justify-end gap-2">
                                        {res.status === 'PENDING' && (
                                            <>
                                                <button
                                                    onClick={() => handleAction(res.id, 'confirm')}
                                                    className="p-2 text-green-600 hover:bg-green-50 rounded-lg transition-colors"
                                                    title="Confirmer"
                                                >
                                                    <CheckCircle className="w-5 h-5" />
                                                </button>
                                                <button
                                                    onClick={() => handleAction(res.id, 'cancel')}
                                                    className="p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                                                    title="Refuser"
                                                >
                                                    <XCircle className="w-5 h-5" />
                                                </button>
                                            </>
                                        )}
                                        {res.status === 'CONFIRMED' && (
                                            <button
                                                onClick={() => handleAction(res.id, 'start')}
                                                className="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                                                title="Démarrer la location (Remettre clés)"
                                            >
                                                <Play className="w-5 h-5" />
                                            </button>
                                        )}
                                        {res.status === 'ACTIVE' && (
                                            <button
                                                onClick={() => handleAction(res.id, 'complete')}
                                                className="p-2 text-gray-800 hover:bg-gray-100 rounded-lg transition-colors"
                                                title="Terminer la location (Retour véhicule)"
                                            >
                                                <Flag className="w-5 h-5" />
                                            </button>
                                        )}
                                        <button className="p-2 text-gray-400 hover:text-black hover:bg-gray-100 rounded-lg transition-colors" title="Voir détails">
                                            <Eye className="w-5 h-5" />
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>

                {filteredReservations.length === 0 && !loading && (
                    <div className="p-12 text-center text-gray-500">
                        <CalendarDays className="w-12 h-12 mx-auto mb-3 text-gray-300" />
                        <p className="font-medium">Aucune réservation trouvée</p>
                        <p className="text-sm mt-1">Essayez de modifier vos filtres de recherche</p>
                    </div>
                )}
            </div>
        </div>
    );
};

const FilterButton = ({ active, onClick, label }: { active: boolean, onClick: () => void, label: string }) => (
    <button
        onClick={onClick}
        className={`px-4 py-2 rounded-lg font-bold transition-colors whitespace-nowrap ${active ? 'bg-black text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'}`}
    >
        {label}
    </button>
);

const StatusBadge = ({ status }: { status: string }) => {
    const config: Record<string, { color: string, icon: React.ElementType, label: string }> = {
        PENDING: { color: 'bg-yellow-100 text-yellow-800', icon: Clock, label: 'En Attente' },
        CONFIRMED: { color: 'bg-green-100 text-green-800', icon: CheckCircle, label: 'Confirmée' },
        ACTIVE: { color: 'bg-blue-100 text-blue-800', icon: Play, label: 'En Cours' },
        CANCELLED: { color: 'bg-red-100 text-red-800', icon: XCircle, label: 'Annulée' },
        COMPLETED: { color: 'bg-gray-100 text-gray-800', icon: Flag, label: 'Terminée' },
    };
    const { color, icon: Icon, label } = config[status] || { color: 'bg-gray-100 text-gray-800', icon: Clock, label: status };

    return (
        <span className={`px-3 py-1 rounded-full text-xs font-bold inline-flex items-center gap-1 ${color}`}>
            <Icon className="w-3 h-3" /> {label}
        </span>
    );
};

export default AdminReservations;
