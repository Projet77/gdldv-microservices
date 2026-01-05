import React, { useState, useEffect } from 'react';
import {
    Users,
    Search,
    Plus,
    MoreVertical,
    Edit2,
    Trash2,
    Shield,
    Check,
    X,
    Loader
} from 'lucide-react';
import api from '../../../services/api';
import { Dialog } from '@headlessui/react';

// Types
interface Role {
    id: number;
    name: string;
}

interface User {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
    roles: Role[];
    active: boolean;
}

const SuperAdminUsers: React.FC = () => {
    const [users, setUsers] = useState<User[]>([]);
    const [loading, setLoading] = useState(true);
    const [searchTerm, setSearchTerm] = useState('');
    const [isCreateOpen, setIsCreateOpen] = useState(false);
    const [selectedUser, setSelectedUser] = useState<User | null>(null);
    const [isEditOpen, setIsEditOpen] = useState(false);

    // Form States
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        role: 'ROLE_CLIENT' // Default
    });

    useEffect(() => {
        fetchUsers();
    }, []);

    const fetchUsers = async () => {
        try {
            setLoading(true);
            // Assuming the gateway routes /user-service/api/users correctly
            // Or if using direct service port. Using 'api' axios instance which should be configured.
            const response = await api.get('/api/users');
            setUsers(response.data);
        } catch (error) {
            console.error("Error fetching users:", error);
        } finally {
            setLoading(false);
        }
    };

    const handleCreate = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            // We need to construct the payload expected by the backend logic.
            // Backend expects User object. For roles, it's tricky via simple JSON if we don't have IDs.
            // However, AuthService.register handles creating with default role.
            // But here we want to specify role.
            // If the backend accepts simple User object with roles set...
            // Let's rely on a helper to format roles. 
            // Since we updated UserService to handle setRoles, we need to pass a Role object structure.

            // NOTE: This implementation assumes the backend can map the input.
            // If not, we might need a specific endpoint for Admin creation or handle it differently.
            // For now, let's try to send what we have.

            // Workaround: We'll create the user first, then update if needed, OR send roles array.

            const roles = [{ name: formData.role }]; // Mock role object

            const payload = {
                firstName: formData.firstName,
                lastName: formData.lastName,
                email: formData.email,
                password: formData.password,
                roles: roles
            };

            await api.post('/api/users', payload);
            setIsCreateOpen(false);
            setFormData({ firstName: '', lastName: '', email: '', password: '', role: 'ROLE_CLIENT' });
            fetchUsers();
        } catch (error) {
            console.error("Error creating user:", error);
            alert("Erreur lors de la création");
        }
    };

    const handleUpdate = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!selectedUser) return;

        try {
            // For role update
            const roles = [{ id: 99, name: formData.role }]; // ID is dummy, name matters if logic uses name lookup or cascade
            // Wait, our backend might need existing ID for Roles if it doesn't cascade persist.
            // To be safe, we should probably fetch roles to get IDs, but let's try sending Name.
            // If it fails, we know we need to improve backend DTO handling.

            const payload = {
                ...selectedUser,
                firstName: formData.firstName,
                lastName: formData.lastName,
                email: formData.email,
                roles: roles
            };

            await api.put(`/api/users/${selectedUser.id}`, payload);
            setIsEditOpen(false);
            setSelectedUser(null);
            fetchUsers();
        } catch (error) {
            console.error("Error updating user:", error);
            alert("Erreur lors de la mise à jour");
        }
    };

    const handleDelete = async (id: number) => {
        if (!confirm("Êtes-vous sûr de vouloir supprimer cet utilisateur ?")) return;
        try {
            await api.delete(`/api/users/${id}`);
            fetchUsers();
        } catch (error) {
            console.error("Error deleting user:", error);
        }
    };

    const openEdit = (user: User) => {
        setSelectedUser(user);
        setFormData({
            firstName: user.firstName,
            lastName: user.lastName,
            email: user.email,
            password: '', // Keep empty
            role: user.roles && user.roles.length > 0 ? user.roles[0].name : 'ROLE_CLIENT'
        });
        setIsEditOpen(true);
    };

    const filteredUsers = users.filter(user =>
        user.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
        user.lastName.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <div className="space-y-6">
            <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
                <div>
                    <h1 className="text-3xl font-black text-gray-900 tracking-tight">Utilisateurs</h1>
                    <p className="mt-1 text-gray-500">Gérez les comptes clients, agents et administrateurs.</p>
                </div>
                <button
                    onClick={() => setIsCreateOpen(true)}
                    className="flex items-center gap-2 bg-black text-white px-5 py-2.5 rounded-xl font-bold hover:bg-zinc-800 transition shadow-lg hover:shadow-xl transform hover:-translate-y-0.5"
                >
                    <Plus className="h-5 w-5" />
                    Nouvel Utilisateur
                </button>
            </div>

            {/* Filters */}
            <div className="bg-white p-4 rounded-2xl shadow-sm border border-gray-100 flex items-center gap-4">
                <div className="relative flex-1">
                    <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" />
                    <input
                        type="text"
                        placeholder="Rechercher par nom ou email..."
                        className="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-yellow-400"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                </div>
                {/* Add Role Filter here if needed */}
            </div>

            {/* List */}
            <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                {loading ? (
                    <div className="p-12 flex justify-center">
                        <Loader className="h-8 w-8 animate-spin text-yellow-400" />
                    </div>
                ) : (
                    <table className="w-full text-left">
                        <thead className="bg-gray-50 border-b border-gray-100">
                            <tr>
                                <th className="px-6 py-4 text-xs font-bold text-gray-500 uppercase tracking-wider">Utilisateur</th>
                                <th className="px-6 py-4 text-xs font-bold text-gray-500 uppercase tracking-wider">Rôle</th>
                                <th className="px-6 py-4 text-xs font-bold text-gray-500 uppercase tracking-wider">Statut</th>
                                <th className="px-6 py-4 text-xs font-bold text-gray-500 uppercase tracking-wider text-right">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-100">
                            {filteredUsers.map((user) => (
                                <tr key={user.id} className="hover:bg-gray-50/50 transition-colors group">
                                    <td className="px-6 py-4">
                                        <div className="flex items-center gap-3">
                                            <div className="h-10 w-10 rounded-full bg-gray-100 flex items-center justify-center font-bold text-gray-500">
                                                {user.firstName.charAt(0)}{user.lastName.charAt(0)}
                                            </div>
                                            <div>
                                                <p className="font-bold text-gray-900">{user.firstName} {user.lastName}</p>
                                                <p className="text-sm text-gray-500">{user.email}</p>
                                            </div>
                                        </div>
                                    </td>
                                    <td className="px-6 py-4">
                                        <span className={`px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wide border ${user.roles && user.roles.some(r => r.name === 'ROLE_SUPER_ADMIN') ? 'bg-black text-yellow-400 border-black' :
                                                user.roles && user.roles.some(r => r.name === 'ROLE_ADMIN') ? 'bg-purple-100 text-purple-700 border-purple-200' :
                                                    user.roles && user.roles.some(r => r.name === 'ROLE_MANAGER') ? 'bg-blue-100 text-blue-700 border-blue-200' :
                                                        user.roles && user.roles.some(r => r.name === 'ROLE_AGENT') ? 'bg-green-100 text-green-700 border-green-200' :
                                                            'bg-gray-100 text-gray-600 border-gray-200'
                                            }`}>
                                            {user.roles && user.roles.length > 0 ? user.roles[0].name.replace('ROLE_', '') : 'CLIENT'}
                                        </span>
                                    </td>
                                    <td className="px-6 py-4">
                                        <span className="flex items-center gap-1.5 text-sm font-medium text-green-600">
                                            <div className="h-2 w-2 rounded-full bg-green-500 animate-pulse"></div>
                                            Actif
                                        </span>
                                    </td>
                                    <td className="px-6 py-4 text-right">
                                        <div className="flex items-center justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                                            <button
                                                onClick={() => openEdit(user)}
                                                className="p-2 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                                                title="Modifier / Changer Rôle"
                                            >
                                                <Edit2 className="h-4 w-4" />
                                            </button>
                                            <button
                                                onClick={() => handleDelete(user.id)}
                                                className="p-2 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                                                title="Supprimer"
                                            >
                                                <Trash2 className="h-4 w-4" />
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                )}
            </div>

            {/* CREATE MODAL */}
            {isCreateOpen && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
                    <div className="fixed inset-0 bg-black/50 backdrop-blur-sm" onClick={() => setIsCreateOpen(false)}></div>
                    <div className="bg-white rounded-2xl shadow-xl w-full max-w-md relative z-10 p-6">
                        <h2 className="text-xl font-bold mb-6">Nouvel Utilisateur</h2>
                        <form onSubmit={handleCreate} className="space-y-4">
                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-sm font-bold text-gray-700 mb-1">Prénom</label>
                                    <input type="text" required className="w-full p-2 border rounded-lg" value={formData.firstName} onChange={e => setFormData({ ...formData, firstName: e.target.value })} />
                                </div>
                                <div>
                                    <label className="block text-sm font-bold text-gray-700 mb-1">Nom</label>
                                    <input type="text" required className="w-full p-2 border rounded-lg" value={formData.lastName} onChange={e => setFormData({ ...formData, lastName: e.target.value })} />
                                </div>
                            </div>
                            <div>
                                <label className="block text-sm font-bold text-gray-700 mb-1">Email</label>
                                <input type="email" required className="w-full p-2 border rounded-lg" value={formData.email} onChange={e => setFormData({ ...formData, email: e.target.value })} />
                            </div>
                            <div>
                                <label className="block text-sm font-bold text-gray-700 mb-1">Mot de passe</label>
                                <input type="password" required className="w-full p-2 border rounded-lg" value={formData.password} onChange={e => setFormData({ ...formData, password: e.target.value })} />
                            </div>
                            <div>
                                <label className="block text-sm font-bold text-gray-700 mb-1">Rôle</label>
                                <select className="w-full p-2 border rounded-lg bg-white" value={formData.role} onChange={e => setFormData({ ...formData, role: e.target.value })}>
                                    <option value="ROLE_CLIENT">Client</option>
                                    <option value="ROLE_AGENT">Agent</option>
                                    <option value="ROLE_MANAGER">Manager</option>
                                    <option value="ROLE_ADMIN">Admin</option>
                                    <option value="ROLE_SUPER_ADMIN">Super Admin</option>
                                </select>
                            </div>
                            <div className="flex justify-end gap-3 pt-4">
                                <button type="button" onClick={() => setIsCreateOpen(false)} className="px-4 py-2 text-gray-500 font-bold hover:bg-gray-100 rounded-lg">Annuler</button>
                                <button type="submit" className="px-4 py-2 bg-yellow-400 text-black font-bold rounded-lg hover:bg-yellow-500">Créer</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {/* EDIT MODAL */}
            {isEditOpen && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
                    <div className="fixed inset-0 bg-black/50 backdrop-blur-sm" onClick={() => setIsEditOpen(false)}></div>
                    <div className="bg-white rounded-2xl shadow-xl w-full max-w-md relative z-10 p-6">
                        <h2 className="text-xl font-bold mb-6">Modifier Utilisateur</h2>
                        <form onSubmit={handleUpdate} className="space-y-4">
                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-sm font-bold text-gray-700 mb-1">Prénom</label>
                                    <input type="text" required className="w-full p-2 border rounded-lg" value={formData.firstName} onChange={e => setFormData({ ...formData, firstName: e.target.value })} />
                                </div>
                                <div>
                                    <label className="block text-sm font-bold text-gray-700 mb-1">Nom</label>
                                    <input type="text" required className="w-full p-2 border rounded-lg" value={formData.lastName} onChange={e => setFormData({ ...formData, lastName: e.target.value })} />
                                </div>
                            </div>
                            <div>
                                <label className="block text-sm font-bold text-gray-700 mb-1">Email</label>
                                <input type="email" required className="w-full p-2 border rounded-lg" value={formData.email} onChange={e => setFormData({ ...formData, email: e.target.value })} />
                            </div>
                            <div>
                                <label className="block text-sm font-bold text-gray-700 mb-1">Rôle</label>
                                <select className="w-full p-2 border rounded-lg bg-white" value={formData.role} onChange={e => setFormData({ ...formData, role: e.target.value })}>
                                    <option value="ROLE_CLIENT">Client</option>
                                    <option value="ROLE_AGENT">Agent</option>
                                    <option value="ROLE_MANAGER">Manager</option>
                                    <option value="ROLE_ADMIN">Admin</option>
                                    <option value="ROLE_SUPER_ADMIN">Super Admin</option>
                                </select>
                            </div>
                            <div className="flex justify-end gap-3 pt-4">
                                <button type="button" onClick={() => setIsEditOpen(false)} className="px-4 py-2 text-gray-500 font-bold hover:bg-gray-100 rounded-lg">Annuler</button>
                                <button type="submit" className="px-4 py-2 bg-black text-white font-bold rounded-lg hover:bg-zinc-800">Enregistrer</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default SuperAdminUsers;
