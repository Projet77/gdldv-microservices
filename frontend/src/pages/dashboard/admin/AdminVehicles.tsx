import React, { useState, useEffect } from 'react';
import { Plus, Search, Edit2, Trash2, Eye, Armchair, Baby, LayoutGrid, LayoutList } from 'lucide-react';
import api from '../../../services/api';

// Updated Interface matching Backend
interface Vehicle {
    id: number;
    brand: string;
    model: string;
    category: string;
    dailyPrice: number;
    status: 'AVAILABLE' | 'RENTED' | 'MAINTENANCE' | 'ARCHIVED';
    images: string[];
    licensePlate: string;
    seats: number;
    babySeat: boolean;
    mileage: number;
    year: number;
    fuelType: string;
    transmission: string;
}

const AdminVehicles: React.FC = () => {
    const [vehicles, setVehicles] = useState<Vehicle[]>([]);
    const [loading, setLoading] = useState(true);
    const [searchQuery, setSearchQuery] = useState('');
    const [filterStatus, setFilterStatus] = useState<string>('ALL');
    const [viewMode, setViewMode] = useState<'list' | 'grid'>('grid');

    // Modals
    const [isEditModalOpen, setIsEditModalOpen] = useState(false);
    const [isDetailsModalOpen, setIsDetailsModalOpen] = useState(false);
    const [selectedVehicle, setSelectedVehicle] = useState<Vehicle | null>(null);

    // Form Data
    const [formData, setFormData] = useState<Partial<Vehicle>>({
        brand: '', model: '', category: 'Berline', dailyPrice: 0,
        licensePlate: '', seats: 5, babySeat: false, images: [],
        year: 2024, mileage: 0, fuelType: 'ESSENCE', transmission: 'AUTOMATIQUE'
    });

    useEffect(() => {
        fetchVehicles();
    }, []);

    const fetchVehicles = async () => {
        try {
            setLoading(true);
            const response = await api.get('/api/v1/vehicles');
            const data = response.data.content || response.data;
            setVehicles(data);
        } catch (error) {
            console.error("Failed to fetch vehicles", error);
        } finally {
            setLoading(false);
        }
    };

    const handleSave = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const payload = {
                ...formData,
                images: Array.isArray(formData.images) ? formData.images : (formData.images as string).split(',').map((s: string) => s.trim())
            };

            if (selectedVehicle?.id) {
                await api.put(`/api/v1/vehicles/${selectedVehicle.id}`, payload);
            } else {
                await api.post('/api/v1/vehicles', payload);
            }
            setIsEditModalOpen(false);
            fetchVehicles();
        } catch (error) {
            console.error("Error saving vehicle", error);
            alert("Erreur lors de l'enregistrement");
        }
    };

    const handleDelete = async (id: number) => {
        if (confirm('Supprimer ce véhicule ?')) {
            try {
                await api.delete(`/api/v1/vehicles/${id}`);
                fetchVehicles();
            } catch (e) { console.error(e); }
        }
    };

    const openAdd = () => {
        setSelectedVehicle(null);
        setFormData({
            brand: '', model: '', category: 'Berline', dailyPrice: 0,
            licensePlate: '', seats: 5, babySeat: false, images: [], year: 2024
        });
        setIsEditModalOpen(true);
    };

    const openEdit = (v: Vehicle) => {
        setSelectedVehicle(v);
        setFormData(v);
        setIsEditModalOpen(true);
    };

    const openDetails = (v: Vehicle) => {
        setSelectedVehicle(v);
        setIsDetailsModalOpen(true);
    };

    const filteredVehicles = vehicles.filter(v => {
        const matchesSearch = v.model.toLowerCase().includes(searchQuery.toLowerCase()) ||
            v.brand.toLowerCase().includes(searchQuery.toLowerCase()) ||
            v.licensePlate.toLowerCase().includes(searchQuery.toLowerCase());
        const matchesStatus = filterStatus === 'ALL' || v.status === filterStatus;
        return matchesSearch && matchesStatus;
    });

    return (
        <div className="max-w-7xl mx-auto space-y-6">
            {/* Header */}
            <div className="flex flex-col md:flex-row justify-between items-center gap-4">
                <div>
                    <h1 className="text-3xl font-black text-gray-900">Gestion Flotte</h1>
                    <p className="text-gray-500">Parc automobile et état des lieux.</p>
                </div>
                <button onClick={openAdd} className="bg-yellow-400 hover:bg-yellow-300 text-black px-5 py-3 rounded-xl font-bold flex items-center gap-2 shadow-lg hover:shadow-xl transition-all">
                    <Plus className="w-5 h-5" /> Ajouter un véhicule
                </button>
            </div>

            {/* Filters & View Toggle */}
            <div className="bg-white rounded-2xl p-4 shadow-sm border border-gray-100 flex flex-col md:flex-row gap-4 items-center justify-between">
                <div className="relative w-full md:w-96">
                    <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 w-5 h-5" />
                    <input
                        type="text"
                        placeholder="Rechercher..."
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        className="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-yellow-400"
                    />
                </div>

                <div className="flex items-center gap-4">
                    <div className="flex bg-gray-100 rounded-lg p-1">
                        <button
                            onClick={() => setViewMode('list')}
                            className={`p-2 rounded-md transition-all ${viewMode === 'list' ? 'bg-white shadow-sm text-black' : 'text-gray-400 hover:text-gray-600'}`}
                            title="Vue Liste"
                        >
                            <LayoutList className="w-5 h-5" />
                        </button>
                        <button
                            onClick={() => setViewMode('grid')}
                            className={`p-2 rounded-md transition-all ${viewMode === 'grid' ? 'bg-white shadow-sm text-black' : 'text-gray-400 hover:text-gray-600'}`}
                            title="Vue Grille"
                        >
                            <LayoutGrid className="w-5 h-5" />
                        </button>
                    </div>

                    <div className="h-8 w-px bg-gray-200 mx-2 hidden md:block"></div>

                    <div className="flex gap-2 overflow-x-auto pb-2 md:pb-0">
                        {[
                            { key: 'ALL', label: 'Tous' },
                            { key: 'AVAILABLE', label: 'Dispo' },
                            { key: 'RENTED', label: 'Loués' },
                            { key: 'MAINTENANCE', label: 'Maint.' }
                        ].map(option => (
                            <button
                                key={option.key}
                                onClick={() => setFilterStatus(option.key)}
                                className={`px-3 py-2 rounded-lg text-sm font-bold transition-colors whitespace-nowrap ${filterStatus === option.key ? 'bg-black text-white' : 'bg-gray-100 text-gray-600'}`}
                            >
                                {option.label}
                            </button>
                        ))}
                    </div>
                </div>
            </div>

            {/* Content */}
            {viewMode === 'list' ? (
                <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                    <table className="w-full text-left">
                        <thead className="bg-gray-50 border-b border-gray-100">
                            <tr>
                                <th className="px-6 py-4 font-bold text-gray-500 text-xs uppercase">Véhicule</th>
                                <th className="px-6 py-4 font-bold text-gray-500 text-xs uppercase">Info</th>
                                <th className="px-6 py-4 font-bold text-gray-500 text-xs uppercase">Prix / Jour</th>
                                <th className="px-6 py-4 font-bold text-gray-500 text-xs uppercase">Statut</th>
                                <th className="px-6 py-4 font-bold text-gray-500 text-xs uppercase text-right">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-100">
                            {filteredVehicles.map((vehicle) => (
                                <tr key={vehicle.id} className="hover:bg-gray-50 transition-colors group">
                                    <td className="px-6 py-4">
                                        <div className="flex items-center gap-4">
                                            <div className="h-12 w-16 bg-gray-100 rounded-lg overflow-hidden flex-shrink-0">
                                                {vehicle.images && vehicle.images.length > 0 ? (
                                                    <img src={vehicle.images[0]} alt="" className="w-full h-full object-cover" />
                                                ) : (
                                                    <div className="w-full h-full flex items-center justify-center text-gray-400 text-xs text-center p-1">No IMG</div>
                                                )}
                                            </div>
                                            <div>
                                                <p className="font-bold text-gray-900">{vehicle.brand} {vehicle.model}</p>
                                                <p className="text-xs text-gray-500 font-mono">{vehicle.licensePlate}</p>
                                            </div>
                                        </div>
                                    </td>
                                    <td className="px-6 py-4">
                                        <div className="flex flex-col gap-1">
                                            <span className="text-sm font-medium">{vehicle.category}</span>
                                            <div className="flex items-center gap-2 text-xs text-gray-500">
                                                <span className="flex items-center gap-1"><Armchair className="w-3 h-3" /> {vehicle.seats}</span>
                                                {vehicle.babySeat && <Baby className="w-3 h-3 text-green-500" />}
                                            </div>
                                        </div>
                                    </td>
                                    <td className="px-6 py-4 font-bold">
                                        {vehicle.dailyPrice?.toLocaleString()} CFA
                                    </td>
                                    <td className="px-6 py-4">
                                        <StatusBadge status={vehicle.status} />
                                    </td>
                                    <td className="px-6 py-4 text-right">
                                        <div className="flex justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                                            <button onClick={() => openDetails(vehicle)} className="p-2 text-gray-400 hover:text-black hover:bg-gray-100 rounded-lg" title="Détails">
                                                <Eye className="w-4 h-4" />
                                            </button>
                                            <button onClick={() => openEdit(vehicle)} className="p-2 text-blue-500 hover:bg-blue-50 rounded-lg" title="Modifier">
                                                <Edit2 className="w-4 h-4" />
                                            </button>
                                            <button onClick={() => handleDelete(vehicle.id)} className="p-2 text-red-500 hover:bg-red-50 rounded-lg" title="Supprimer">
                                                <Trash2 className="w-4 h-4" />
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                            {filteredVehicles.length === 0 && !loading && (
                                <tr><td colSpan={5} className="p-8 text-center text-gray-500">Aucun véhicule trouvé.</td></tr>
                            )}
                        </tbody>
                    </table>
                </div>
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                    {filteredVehicles.map((vehicle) => (
                        <div key={vehicle.id} className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden group hover:shadow-md transition-all flex flex-col">
                            <div className="relative h-48 bg-gray-100">
                                {vehicle.images && vehicle.images.length > 0 ? (
                                    <img src={vehicle.images[0]} alt="" className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500" />
                                ) : (
                                    <div className="w-full h-full flex items-center justify-center text-gray-400">Aucune image</div>
                                )}
                                <div className="absolute top-3 right-3">
                                    <StatusBadge status={vehicle.status} />
                                </div>
                                <div className="absolute inset-0 bg-black/0 group-hover:bg-black/20 transition-colors flex items-center justify-center opacity-0 group-hover:opacity-100">
                                    <button onClick={() => openDetails(vehicle)} className="bg-white text-black px-4 py-2 rounded-lg font-bold shadow-lg transform translate-y-4 group-hover:translate-y-0 transition-all flex items-center gap-2">
                                        <Eye className="w-4 h-4" /> Voir Détails
                                    </button>
                                </div>
                            </div>
                            <div className="p-5 flex flex-col flex-1">
                                <div className="flex justify-between items-start mb-2">
                                    <div>
                                        <h3 className="font-black text-lg text-gray-900 leading-tight">{vehicle.brand} {vehicle.model}</h3>
                                        <p className="text-xs text-gray-500 font-mono mt-1">{vehicle.licensePlate}</p>
                                    </div>
                                    <span className="bg-gray-100 text-gray-600 text-xs font-bold px-2 py-1 rounded-md">{vehicle.year}</span>
                                </div>

                                <div className="flex items-center gap-4 text-sm text-gray-500 mb-4">
                                    <span className="flex items-center gap-1"><Armchair className="w-4 h-4" /> {vehicle.seats}</span>
                                    <span className="flex items-center gap-1">{vehicle.transmission === 'AUTOMATIC' ? 'Auto' : 'Manuelle'}</span>
                                    <span className="flex items-center gap-1">{vehicle.fuelType}</span>
                                </div>

                                <div className="mt-auto flex items-center justify-between pt-4 border-t border-gray-100">
                                    <div>
                                        <p className="text-xs text-gray-400 font-bold uppercase">Prix / Jour</p>
                                        <p className="text-xl font-black text-gray-900">{vehicle.dailyPrice?.toLocaleString()} <span className="text-xs text-gray-500">CFA</span></p>
                                    </div>
                                    <div className="flex gap-1">
                                        <button onClick={() => openEdit(vehicle)} className="p-2 text-gray-400 hover:text-blue-500 hover:bg-blue-50 rounded-lg transition-colors">
                                            <Edit2 className="w-4 h-4" />
                                        </button>
                                        <button onClick={() => handleDelete(vehicle.id)} className="p-2 text-gray-400 hover:text-red-500 hover:bg-red-50 rounded-lg transition-colors">
                                            <Trash2 className="w-4 h-4" />
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    ))}
                    {filteredVehicles.length === 0 && !loading && (
                        <div className="col-span-full p-12 text-center text-gray-500">Aucun véhicule trouvé.</div>
                    )}
                </div>
            )}

            {/* EDIT/ADD MODAL */}
            {isEditModalOpen && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
                    <div className="fixed inset-0 bg-black/50 backdrop-blur-sm" onClick={() => setIsEditModalOpen(false)}></div>
                    <div className="bg-white rounded-2xl shadow-2xl w-full max-w-2xl relative z-10 p-8 max-h-[90vh] overflow-y-auto">
                        <h2 className="text-2xl font-black mb-6">{selectedVehicle ? 'Modifier Véhicule' : 'Nouveau Véhicule'}</h2>
                        <form onSubmit={handleSave} className="space-y-6">
                            <div className="grid grid-cols-2 gap-6">
                                <div>
                                    <label className="block text-sm font-bold mb-2">Marque</label>
                                    <input className="w-full p-3 border rounded-xl" value={formData.brand} onChange={e => setFormData({ ...formData, brand: e.target.value })} required />
                                </div>
                                <div>
                                    <label className="block text-sm font-bold mb-2">Modèle</label>
                                    <input className="w-full p-3 border rounded-xl" value={formData.model} onChange={e => setFormData({ ...formData, model: e.target.value })} required />
                                </div>
                                <div>
                                    <label className="block text-sm font-bold mb-2">Immatriculation</label>
                                    <input className="w-full p-3 border rounded-xl" value={formData.licensePlate} onChange={e => setFormData({ ...formData, licensePlate: e.target.value })} required />
                                </div>
                                <div>
                                    <label className="block text-sm font-bold mb-2">Prix / Jour (CFA)</label>
                                    <input type="number" className="w-full p-3 border rounded-xl" value={formData.dailyPrice} onChange={e => setFormData({ ...formData, dailyPrice: Number(e.target.value) })} required />
                                </div>
                                <div>
                                    <label className="block text-sm font-bold mb-2">Année Modèle</label>
                                    <input type="number" className="w-full p-3 border rounded-xl" value={formData.year} onChange={e => setFormData({ ...formData, year: Number(e.target.value) })} />
                                </div>
                                <div>
                                    <label className="block text-sm font-bold mb-2">Kilométrage</label>
                                    <input type="number" className="w-full p-3 border rounded-xl" value={formData.mileage} onChange={e => setFormData({ ...formData, mileage: Number(e.target.value) })} />
                                </div>
                                <div>
                                    <label className="block text-sm font-bold mb-2">Nombre de places</label>
                                    <input type="number" className="w-full p-3 border rounded-xl" value={formData.seats} onChange={e => setFormData({ ...formData, seats: Number(e.target.value) })} />
                                </div>
                                <div className="flex items-center gap-3 pt-8">
                                    <input type="checkbox" id="babySeat" className="w-5 h-5 accent-yellow-400" checked={formData.babySeat} onChange={e => setFormData({ ...formData, babySeat: e.target.checked })} />
                                    <label htmlFor="babySeat" className="font-bold text-gray-700">Siège Bébé Disponible</label>
                                </div>
                            </div>

                            <div>
                                <label className="block text-sm font-bold mb-2">Images (URLs séparées par des virgules)</label>
                                <textarea
                                    className="w-full p-3 border rounded-xl"
                                    rows={3}
                                    placeholder="https://example.com/car1.jpg, https://example.com/car2.jpg"
                                    value={Array.isArray(formData.images) ? formData.images.join(', ') : formData.images}
                                    onChange={e => setFormData({ ...formData, images: e.target.value.split(',').map(s => s.trim()) })}
                                ></textarea>
                            </div>

                            <div className="flex justify-end gap-3 pt-4">
                                <button type="button" onClick={() => setIsEditModalOpen(false)} className="px-6 py-3 font-bold text-gray-500 hover:bg-gray-100 rounded-xl">Annuler</button>
                                <button type="submit" className="px-6 py-3 bg-yellow-400 text-black font-bold rounded-xl hover:bg-yellow-500 shadow-lg">Enregistrer</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {/* DETAILS MODAL */}
            {isDetailsModalOpen && selectedVehicle && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
                    <div className="fixed inset-0 bg-black/80 backdrop-blur-md" onClick={() => setIsDetailsModalOpen(false)}></div>
                    <div className="bg-white rounded-2xl shadow-2xl w-full max-w-4xl relative z-10 overflow-hidden flex flex-col md:flex-row h-[80vh]">
                        {/* Left: Gallery */}
                        <div className="w-full md:w-1/2 bg-gray-100 p-4 overflow-y-auto custom-scrollbar">
                            <div className="grid gap-4">
                                {selectedVehicle.images && selectedVehicle.images.length > 0 ? (
                                    selectedVehicle.images.map((img, idx) => (
                                        <img key={idx} src={img} alt="" className="w-full rounded-xl shadow-md" />
                                    ))
                                ) : (
                                    <div className="h-64 flex items-center justify-center text-gray-400">Aucune image</div>
                                )}
                            </div>
                        </div>

                        {/* Right: Info */}
                        <div className="w-full md:w-1/2 p-8 flex flex-col overflow-y-auto">
                            <button onClick={() => setIsDetailsModalOpen(false)} className="self-end p-2 hover:bg-gray-100 rounded-full mb-4">
                                <XCircle className="w-6 h-6 text-gray-400" />
                            </button>

                            <h2 className="text-3xl font-black mb-2">{selectedVehicle.brand} {selectedVehicle.model}</h2>
                            <StatusBadge status={selectedVehicle.status} />

                            <div className="mt-8 space-y-6 flex-1">
                                <div className="grid grid-cols-2 gap-4">
                                    <InfoItem label="Immatriculation" value={selectedVehicle.licensePlate} />
                                    <InfoItem label="Année" value={selectedVehicle.year?.toString()} />
                                    <InfoItem label="Kilométrage" value={`${selectedVehicle.mileage} km`} />
                                    <InfoItem label="Carburant" value={selectedVehicle.fuelType} />
                                    <InfoItem label="Transmission" value={selectedVehicle.transmission} />
                                    <InfoItem label="Places" value={selectedVehicle.seats?.toString()} />
                                </div>

                                <div className="p-4 bg-yellow-50 rounded-xl border border-yellow-100">
                                    <p className="text-xs font-bold text-yellow-600 uppercase tracking-widest mb-1">Prix par jour</p>
                                    <p className="text-3xl font-black text-gray-900">{selectedVehicle.dailyPrice?.toLocaleString()} <span className="text-lg text-gray-500 font-medium">CFA</span></p>
                                </div>

                                <div className="border-t pt-4">
                                    <h3 className="font-bold mb-2">Équipements</h3>
                                    <div className="flex flex-wrap gap-2">
                                        {selectedVehicle.babySeat && (
                                            <span className="px-3 py-1 bg-green-100 text-green-700 rounded-lg text-sm font-bold flex items-center gap-2">
                                                <Baby className="w-4 h-4" /> Siège Bébé
                                            </span>
                                        )}
                                        {/* Add more mocked features if needed */}
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

const InfoItem = ({ label, value }: { label: string, value?: string }) => (
    <div>
        <p className="text-xs text-gray-500 uppercase font-bold">{label}</p>
        <p className="font-bold text-gray-900">{value || '-'}</p>
    </div>
);

const StatusBadge = ({ status }: { status: string }) => {
    const config: Record<string, { style: string, label: string }> = {
        AVAILABLE: { style: 'bg-green-100 text-green-800', label: 'Disponible' },
        RENTED: { style: 'bg-blue-100 text-blue-800', label: 'Loué' },
        MAINTENANCE: { style: 'bg-yellow-100 text-yellow-800', label: 'Maintenance' },
        ARCHIVED: { style: 'bg-gray-100 text-gray-800', label: 'Archivé' },
    };

    // Default to displaying the status code if no translation (or generic style)
    const { style, label } = config[status] || { style: 'bg-gray-100 text-gray-800', label: status };

    return <span className={`px-3 py-1 rounded-full text-xs font-bold ${style}`}>{label}</span>;
}

export default AdminVehicles;
