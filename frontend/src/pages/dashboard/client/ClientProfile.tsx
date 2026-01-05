import React, { useState, useEffect } from 'react';
import { Camera, Save, User as UserIcon, Mail, Loader } from 'lucide-react';
import { authService } from '../../../services/authService';
import api from '../../../services/api';

const ClientProfile: React.FC = () => {
    const [user, setUser] = useState<any>(null);
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState<{ type: 'success' | 'error', text: string } | null>(null);

    // Form State
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        profileImage: '',
    });

    useEffect(() => {
        loadUserProfile();
    }, []);

    const loadUserProfile = () => {
        const currentUser = authService.getCurrentUser();
        if (currentUser) {
            setUser(currentUser);
            setFormData({
                firstName: currentUser.firstName || '',
                lastName: currentUser.lastName || '',
                email: currentUser.email || '',
                profileImage: currentUser.profileImage || '',
            });
        }
    };

    const handleImageUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        if (file) {
            const reader = new FileReader();
            reader.onloadend = () => {
                setFormData(prev => ({ ...prev, profileImage: reader.result as string }));
            };
            reader.readAsDataURL(file);
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setMessage(null);

        try {
            if (!user?.id) return;

            const response = await api.put(`/api/users/${user.id}`, {
                ...user,
                firstName: formData.firstName,
                lastName: formData.lastName,
                email: formData.email,
                profileImage: formData.profileImage
            });

            // Update local storage
            const updatedUser = response.data;
            localStorage.setItem('user', JSON.stringify(updatedUser)); // Update stored user session
            setUser(updatedUser);

            setMessage({ type: 'success', text: 'Profil mis à jour avec succès !' });
        } catch (error) {
            console.error(error);
            setMessage({ type: 'error', text: 'Erreur lors de la mise à jour du profil.' });
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="py-8 bg-gray-50 min-h-screen">
            <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="mb-8">
                    <h1 className="text-3xl font-black text-gray-900 tracking-tight">Mon Profil</h1>
                    <p className="mt-2 text-gray-600">Gérez vos informations personnelles et votre apparence.</p>
                </div>

                <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                    <form onSubmit={handleSubmit} className="divide-y divide-gray-100">
                        {/* Header / Avatar Section */}
                        <div className="p-8 bg-gradient-to-r from-gray-900 to-black text-white">
                            <div className="flex flex-col md:flex-row items-center gap-8">
                                <div className="relative group">
                                    <div className="h-32 w-32 rounded-full border-4 border-white/20 overflow-hidden bg-gray-700 shadow-xl flex items-center justify-center">
                                        {formData.profileImage ? (
                                            <img src={formData.profileImage} alt="Profile" className="w-full h-full object-cover" />
                                        ) : (
                                            <span className="text-4xl font-bold text-gray-400">{formData.firstName?.charAt(0)}{formData.lastName?.charAt(0)}</span>
                                        )}
                                    </div>
                                    <label className="absolute bottom-0 right-0 p-2 bg-yellow-400 text-black rounded-full cursor-pointer hover:bg-yellow-300 transition-colors shadow-lg group-hover:scale-110">
                                        <Camera className="w-5 h-5" />
                                        <input type="file" className="hidden" accept="image/*" onChange={handleImageUpload} />
                                    </label>
                                </div>
                                <div className="text-center md:text-left">
                                    <h2 className="text-2xl font-bold">{formData.firstName} {formData.lastName}</h2>
                                    <p className="text-yellow-400 font-medium">{user?.email}</p>
                                    <p className="text-xs text-gray-400 mt-1 uppercase tracking-wider">{user?.role || 'CLIENT'}</p>
                                </div>
                            </div>
                        </div>

                        {/* Form Fields */}
                        <div className="p-8 space-y-6">
                            {message && (
                                <div className={`p-4 rounded-xl flex items-center gap-3 ${message.type === 'success' ? 'bg-green-50 text-green-700 border border-green-100' : 'bg-red-50 text-red-700 border border-red-100'}`}>
                                    {message.type === 'success' ? <div className="w-2 h-2 bg-green-500 rounded-full" /> : <div className="w-2 h-2 bg-red-500 rounded-full" />}
                                    <p className="font-medium text-sm">{message.text}</p>
                                </div>
                            )}

                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                <div>
                                    <label className="block text-sm font-bold text-gray-700 mb-2">Prénom</label>
                                    <div className="relative">
                                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                            <UserIcon className="h-5 w-5 text-gray-400" />
                                        </div>
                                        <input
                                            type="text"
                                            value={formData.firstName}
                                            onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
                                            className="block w-full pl-10 pr-3 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-yellow-400 focus:border-transparent transition-all bg-gray-50 focus:bg-white"
                                            placeholder="Votre prénom"
                                        />
                                    </div>
                                </div>

                                <div>
                                    <label className="block text-sm font-bold text-gray-700 mb-2">Nom</label>
                                    <div className="relative">
                                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                            <UserIcon className="h-5 w-5 text-gray-400" />
                                        </div>
                                        <input
                                            type="text"
                                            value={formData.lastName}
                                            onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
                                            className="block w-full pl-10 pr-3 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-yellow-400 focus:border-transparent transition-all bg-gray-50 focus:bg-white"
                                            placeholder="Votre nom"
                                        />
                                    </div>
                                </div>

                                <div className="md:col-span-2">
                                    <label className="block text-sm font-bold text-gray-700 mb-2">Adresse Email</label>
                                    <div className="relative">
                                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                            <Mail className="h-5 w-5 text-gray-400" />
                                        </div>
                                        <input
                                            type="email"
                                            value={formData.email}
                                            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                                            className="block w-full pl-10 pr-3 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-yellow-400 focus:border-transparent transition-all bg-gray-50 focus:bg-white"
                                            placeholder="exemple@email.com"
                                        />
                                    </div>
                                </div>
                            </div>
                        </div>

                        {/* Actions */}
                        <div className="p-6 bg-gray-50 flex justify-end">
                            <button
                                type="submit"
                                disabled={loading}
                                className="inline-flex items-center px-6 py-3 border border-transparent text-base font-bold rounded-xl shadow-sm text-black bg-yellow-400 hover:bg-yellow-300 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-yellow-500 disabled:opacity-50 disabled:cursor-not-allowed transition-all"
                            >
                                {loading ? (
                                    <>
                                        <Loader className="animate-spin -ml-1 mr-2 h-5 w-5" />
                                        Enregistrement...
                                    </>
                                ) : (
                                    <>
                                        <Save className="-ml-1 mr-2 h-5 w-5" />
                                        Enregistrer les modifications
                                    </>
                                )}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default ClientProfile;
