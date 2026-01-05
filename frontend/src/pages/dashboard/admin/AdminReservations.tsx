import React, { useState, useEffect } from 'react';
import { CalendarDays, CheckCircle, XCircle, Clock, Search, Eye } from 'lucide-react';
import api from '../../../services/api';

interface Reservation {
    id: number;
    vehicleId: number;
    userId: number;
    userName?: string;
    vehicleName?: string;
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

    useEffect(() => {
        fetchReservations();
    }, []);

    const fetchReservations = async () => {
        try {
            setLoading(true);
            const res = await api.get('/api/reservations');
            // Assuming the API returns a list. If page, handle content.
            const data = Array.isArray(res.data) ? res.data : (res.data.content || []);
            setReservations(data);
        } catch (e) {
            console.error("Erreur lors du chargement des réservations", e);
        } finally {
            setLoading(false);
        }
    }

    const filteredReservations = reservations.filter(r => {
        // Safe check for undefined names if backend doesn't populate them yet
        const uName = r.userName || `Client #${r.userId}`;
        const vName = r.vehicleName || `Véhicule #${r.vehicleId}`;

        const matchesSearch = (uName.toLowerCase().includes(searchQuery.toLowerCase())) ||
            (vName.toLowerCase().includes(searchQuery.toLowerCase()));
        const matchesStatus = statusFilter === 'ALL' || r.status === statusFilter;
        return matchesSearch && matchesStatus;
    });

    return (
        <div className="max-w-7xl mx-auto space-y-6">
            <div className="flex flex-col md:flex-row justify-between items-center gap-4">
                <div>
                    <h1 className="text-3xl font-black text-gray-900">Réservations</h1>
                    <p className="text-gray-500">Suivi des réservations et disponibilités.</p>
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
                    <FilterButton active={statusFilter === 'CANCELLED'} onClick={() => setStatusFilter('CANCELLED')} label="Annulées" />
                    <FilterButton active={statusFilter === 'COMPLETED'} onClick={() => setStatusFilter('COMPLETED')} label="Terminées" />
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
                                    <div className="font-bold text-gray-900">{res.userName || `ID: ${res.userId}`}</div>
                                </td>
                                <td className="px-6 py-4">
                                    <div className="font-bold text-gray-900">{res.vehicleName || `ID: ${res.vehicleId}`}</div>
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
                                    <button className="p-2 text-gray-400 hover:text-black hover:bg-gray-100 rounded-lg" title="Voir détails">
                                        <Eye className="w-4 h-4" />
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>

                {filteredReservations.length === 0 && !loading && (
                    <div className="p-12 text-center text-gray-500">
                        Aucune réservation trouvée.
                    </div>
                )}
                {loading && (
                    <div className="p-12 text-center text-gray-500">
                        Chargement...
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
        CANCELLED: { color: 'bg-red-100 text-red-800', icon: XCircle, label: 'Annulée' },
        COMPLETED: { color: 'bg-gray-100 text-gray-800', icon: CheckCircle, label: 'Terminée' },
    };
    const { color, icon: Icon, label } = config[status] || { color: 'bg-gray-100 text-gray-800', icon: Clock, label: status };

    return (
        <span className={`px-3 py-1 rounded-full text-xs font-bold inline-flex items-center gap-1 ${color}`}>
            <Icon className="w-3 h-3" /> {label}
        </span>
    );
};

export default AdminReservations;
