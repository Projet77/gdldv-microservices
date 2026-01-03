import React, { useState } from 'react';
import { Plus, Search, Edit2, Trash2, Archive, Filter, MoreVertical, CheckCircle, XCircle } from 'lucide-react';

interface Vehicle {
    id: string;
    brand: string;
    model: string;
    category: string;
    price: number;
    status: 'AVAILABLE' | 'RENTED' | 'MAINTENANCE' | 'ARCHIVED';
    image: string;
    plate: string;
}

// MOCK DATA for Admin View
const INITIAL_FLEET: Vehicle[] = [
    { id: '1', brand: 'Lamborghini', model: 'Urus', category: 'SUV Sport', price: 250000, status: 'RENTED', image: '/images/suv_car.png', plate: 'DK-2024-LX' },
    { id: '2', brand: 'Peugeot', model: '208', category: 'Citadine', price: 30000, status: 'AVAILABLE', image: '/images/city_car.png', plate: 'DK-8899-BB' },
    { id: '3', brand: 'Toyota', model: 'Corolla', category: 'Berline', price: 40000, status: 'MAINTENANCE', image: '/images/sedan_car.png', plate: 'DK-1234-AA' },
    { id: '4', brand: 'Dacia', model: 'Duster', category: 'SUV Éco', price: 35000, status: 'AVAILABLE', image: '/images/compact_suv.png', plate: 'DK-5678-CC' },
    { id: '5', brand: 'Ferrari', model: 'F8', category: 'Supercar', price: 350000, status: 'ARCHIVED', image: '/images/sport_car.png', plate: 'DK-0001-FF' },
];

const AdminVehicles: React.FC = () => {
    const [vehicles, setVehicles] = useState<Vehicle[]>(INITIAL_FLEET);
    const [searchQuery, setSearchQuery] = useState('');
    const [filterStatus, setFilterStatus] = useState<string>('ALL');
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingVehicle, setEditingVehicle] = useState<Vehicle | null>(null);

    // Filter Logic
    const filteredVehicles = vehicles.filter(v => {
        const matchesSearch = v.model.toLowerCase().includes(searchQuery.toLowerCase()) ||
            v.brand.toLowerCase().includes(searchQuery.toLowerCase()) ||
            v.plate.toLowerCase().includes(searchQuery.toLowerCase());
        const matchesStatus = filterStatus === 'ALL' || v.status === filterStatus;
        return matchesSearch && matchesStatus;
    });

    // CRUD Handlers
    const handleDelete = (id: string) => {
        if (window.confirm('Êtes-vous sûr de vouloir supprimer ce véhicule ?')) {
            setVehicles(vehicles.filter(v => v.id !== id));
        }
    };

    const handleArchive = (id: string) => {
        setVehicles(vehicles.map(v => v.id === id ? { ...v, status: v.status === 'ARCHIVED' ? 'AVAILABLE' : 'ARCHIVED' } : v));
    };

    const handleSave = (e: React.FormEvent) => {
        e.preventDefault();
        // In a real app, gather form data here
        // This is just a simulation of the "Add/Edit" action closing
        console.log("Saving vehicle...");
        setIsModalOpen(false);
        setEditingVehicle(null);
    };

    const openEditModal = (vehicle: Vehicle) => {
        setEditingVehicle(vehicle);
        setIsModalOpen(true);
    };

    const openAddModal = () => {
        setEditingVehicle(null);
        setIsModalOpen(true);
    };

    return (
        <div className="max-w-7xl mx-auto">
            {/* Header Actions */}
            <div className="flex flex-col md:flex-row md:items-center justify-between mb-8 gap-4">
                <div>
                    <h1 className="text-3xl font-black text-gray-900">Gestion Flotte</h1>
                    <p className="text-gray-500">Gérez l'inventaire, les statuts et la maintenance.</p>
                </div>
                <button
                    onClick={openAddModal}
                    className="bg-yellow-400 hover:bg-yellow-300 text-black px-4 py-3 rounded-xl font-bold flex items-center gap-2 shadow-lg hover:shadow-xl transition-all"
                >
                    <Plus className="w-5 h-5" /> Ajouter un véhicule
                </button>
            </div>

            {/* Filters & Search */}
            <div className="bg-white rounded-2xl p-4 shadow-sm border border-gray-100 mb-6 flex flex-col md:flex-row gap-4 items-center justify-between">
                <div className="relative w-full md:w-96">
                    <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 w-5 h-5" />
                    <input
                        type="text"
                        placeholder="Rechercher (Modèle, Marque, Plaque)..."
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        className="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-yellow-400"
                    />
                </div>
                <div className="flex gap-2 w-full md:w-auto overflow-x-auto pb-2 md:pb-0">
                    {['ALL', 'AVAILABLE', 'RENTED', 'MAINTENANCE', 'ARCHIVED'].map(status => (
                        <button
                            key={status}
                            onClick={() => setFilterStatus(status)}
                            className={`px-4 py-2 rounded-lg text-sm font-bold whitespace-nowrap transition-colors ${filterStatus === status
                                    ? 'bg-black text-white'
                                    : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                                }`}
                        >
                            {status === 'ALL' ? 'Tous' : status}
                        </button>
                    ))}
                </div>
            </div>

            {/* Vehicle List */}
            <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                <div className="overflow-x-auto">
                    <table className="w-full">
                        <thead className="bg-gray-50 border-b border-gray-100">
                            <tr>
                                <th className="px-6 py-4 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">Véhicule</th>
                                <th className="px-6 py-4 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">Catégorie</th>
                                <th className="px-6 py-4 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">Plaque</th>
                                <th className="px-6 py-4 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">Prix / Jour</th>
                                <th className="px-6 py-4 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">Statut</th>
                                <th className="px-6 py-4 text-right text-xs font-bold text-gray-500 uppercase tracking-wider">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-100">
                            {filteredVehicles.map((vehicle) => (
                                <tr key={vehicle.id} className="hover:bg-gray-50 transition-colors group">
                                    <td className="px-6 py-4 whitespace-nowrap">
                                        <div className="flex items-center">
                                            <div className="h-10 w-10 flex-shrink-0">
                                                <img className="h-10 w-10 rounded-lg object-cover bg-gray-100" src={vehicle.image} alt="" />
                                            </div>
                                            <div className="ml-4">
                                                <div className="text-sm font-bold text-gray-900">{vehicle.brand} {vehicle.model}</div>
                                            </div>
                                        </div>
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap">
                                        <span className="px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full bg-gray-100 text-gray-800">
                                            {vehicle.category}
                                        </span>
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 font-mono">
                                        {vehicle.plate}
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm font-bold text-gray-900">
                                        {vehicle.price.toLocaleString('fr-FR')} CFA
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap">
                                        <StatusBadge status={vehicle.status} />
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                        <div className="flex justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                                            <button onClick={() => openEditModal(vehicle)} className="p-2 text-blue-600 hover:bg-blue-50 rounded-lg" title="Modifier">
                                                <Edit2 className="w-4 h-4" />
                                            </button>
                                            <button onClick={() => handleArchive(vehicle.id)} className="p-2 text-orange-600 hover:bg-orange-50 rounded-lg" title={vehicle.status === 'ARCHIVED' ? 'Désarchiver' : 'Archiver'}>
                                                <Archive className="w-4 h-4" />
                                            </button>
                                            <button onClick={() => handleDelete(vehicle.id)} className="p-2 text-red-600 hover:bg-red-50 rounded-lg" title="Supprimer">
                                                <Trash2 className="w-4 h-4" />
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>

            {/* Mock Modal for Add/Edit */}
            {isModalOpen && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
                    <div className="bg-white rounded-2xl w-full max-w-lg p-6 shadow-2xl">
                        <div className="flex justify-between items-center mb-6">
                            <h2 className="text-xl font-black text-gray-900">
                                {editingVehicle ? 'Modifier Véhicule' : 'Ajouter Véhicule'}
                            </h2>
                            <button onClick={() => setIsModalOpen(false)} className="p-2 hover:bg-gray-100 rounded-full">
                                <XCircle className="w-6 h-6 text-gray-400" />
                            </button>
                        </div>
                        <form onSubmit={handleSave} className="space-y-4">
                            <div>
                                <label className="block text-sm font-bold text-gray-700 mb-1">Marque & Modèle</label>
                                <input type="text" className="w-full p-2 border border-gray-200 rounded-lg" defaultValue={editingVehicle ? `${editingVehicle.brand} ${editingVehicle.model}` : ''} placeholder="Ex: Peugeot 208" />
                            </div>
                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-sm font-bold text-gray-700 mb-1">Catégorie</label>
                                    <select className="w-full p-2 border border-gray-200 rounded-lg">
                                        <option>Citadine</option>
                                        <option>Berline</option>
                                        <option>SUV</option>
                                        <option>Luxe</option>
                                    </select>
                                </div>
                                <div>
                                    <label className="block text-sm font-bold text-gray-700 mb-1">Prix Jour (CFA)</label>
                                    <input type="number" className="w-full p-2 border border-gray-200 rounded-lg" defaultValue={editingVehicle?.price} placeholder="30000" />
                                </div>
                            </div>
                            <div>
                                <label className="block text-sm font-bold text-gray-700 mb-1">Immatriculation</label>
                                <input type="text" className="w-full p-2 border border-gray-200 rounded-lg" defaultValue={editingVehicle?.plate} placeholder="AA-123-BB" />
                            </div>

                            <div className="pt-4 flex gap-3">
                                <button type="button" onClick={() => setIsModalOpen(false)} className="flex-1 py-3 font-bold text-gray-600 hover:bg-gray-100 rounded-xl transition-colors">Annuler</button>
                                <button type="submit" className="flex-1 py-3 font-bold text-black bg-yellow-400 hover:bg-yellow-300 rounded-xl transition-colors shadow-lg">Enregistrer</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

const StatusBadge = ({ status }: { status: string }) => {
    const styles = {
        AVAILABLE: 'bg-green-100 text-green-800',
        RENTED: 'bg-blue-100 text-blue-800',
        MAINTENANCE: 'bg-yellow-100 text-yellow-800',
        ARCHIVED: 'bg-gray-100 text-gray-800',
    };

    // safe access using keyof
    const style = styles[status as keyof typeof styles] || styles.ARCHIVED;

    return (
        <span className={`px-3 py-1 rounded-full text-xs font-bold border border-transparent ${style}`}>
            {status}
        </span>
    );
}

export default AdminVehicles;
