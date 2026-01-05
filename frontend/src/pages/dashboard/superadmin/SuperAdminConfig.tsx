import React, { useState, useEffect } from 'react';
import { Settings, Save, Globe, Coins, Moon, Sun, Award, Percent } from 'lucide-react';
import { authService } from '../../../services/authService';
import api from '../../../services/api';

const SuperAdminConfig: React.FC = () => {
    const [loading, setLoading] = useState(false);
    const [preferences, setPreferences] = useState({
        language: 'FR',
        currency: 'CFA',
        theme: 'light'
    });
    const [loyaltyDiscount, setLoyaltyDiscount] = useState<string>('10');
    const [loyaltyLoading, setLoyaltyLoading] = useState(false);

    useEffect(() => {
        // Load current user prefs
        const user = authService.getCurrentUser();
        if (user) {
            setPreferences(prev => ({
                ...prev,
                language: user.language || 'FR',
                currency: user.currency || 'CFA'
            }));
        }

        // Load Loyalty Config
        fetchLoyaltyConfig();
    }, []);

    const fetchLoyaltyConfig = async () => {
        try {
            // Note: Ensure /api/v1/loyalty is routed to reservation-service via Gateway
            // If gateway not ready, this might fail or need direct port
            const res = await api.get('/api/v1/loyalty/config');
            if (res.data && res.data.discountPercentage) {
                setLoyaltyDiscount(res.data.discountPercentage.toString());
            }
        } catch (e) {
            console.error("Failed to fetch loyalty config", e);
        }
    }

    const handleSavePreferences = async () => {
        setLoading(true);
        try {
            const user = authService.getCurrentUser();
            if (user) {
                await api.put(`/api/users/${user.id}`, {
                    ...user,
                    language: preferences.language,
                    currency: preferences.currency
                });
                const updatedUser = { ...user, language: preferences.language, currency: preferences.currency };
                localStorage.setItem('user', JSON.stringify(updatedUser));
                alert("Préférences sauvegardées !");
            }
        } catch (error) {
            console.error("Failed to save preferences", error);
            alert("Erreur lors de la sauvegarde.");
        } finally {
            setLoading(false);
        }
    };

    const handleSaveLoyalty = async () => {
        setLoyaltyLoading(true);
        try {
            await api.put('/api/v1/loyalty/config', {
                discountPercentage: parseFloat(loyaltyDiscount)
            });
            alert("Configuration fidélité mise à jour !");
        } catch (error) {
            console.error(error);
            alert("Erreur lors de la mise à jour");
        } finally {
            setLoyaltyLoading(false);
        }
    };

    return (
        <div className="space-y-6">
            <div>
                <h1 className="text-3xl font-black text-gray-900 tracking-tight">Configuration</h1>
                <p className="mt-1 text-gray-500">Personnalisez votre expérience et les paramètres système.</p>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                {/* User Preferences */}
                <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                    <div className="p-6 border-b border-gray-100">
                        <h2 className="text-lg font-bold text-gray-900 flex items-center gap-2">
                            <Settings className="h-5 w-5 text-gray-400" />
                            Préférences Personnelles
                        </h2>
                    </div>
                    <div className="p-6 space-y-6">
                        <div>
                            <label className="block text-sm font-bold text-gray-700 mb-2 flex items-center gap-2">
                                <Globe className="h-4 w-4 text-gray-400" /> Langue
                            </label>
                            <select
                                value={preferences.language}
                                onChange={(e) => setPreferences({ ...preferences, language: e.target.value })}
                                className="w-full p-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-yellow-400"
                            >
                                <option value="FR">Français (France)</option>
                                <option value="EN">English (US)</option>
                                <option value="ES">Español</option>
                            </select>
                        </div>

                        <div>
                            <label className="block text-sm font-bold text-gray-700 mb-2 flex items-center gap-2">
                                <Coins className="h-4 w-4 text-gray-400" /> Devise
                            </label>
                            <select
                                value={preferences.currency}
                                onChange={(e) => setPreferences({ ...preferences, currency: e.target.value })}
                                className="w-full p-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-yellow-400"
                            >
                                <option value="CFA">CFA (Franc CFA)</option>
                                <option value="EUR">EUR (Euro)</option>
                                <option value="USD">USD (Dollar)</option>
                            </select>
                        </div>

                        <button
                            onClick={handleSavePreferences}
                            disabled={loading}
                            className="w-full bg-black text-white font-bold py-3 rounded-xl hover:bg-zinc-800 transition flex items-center justify-center gap-2"
                        >
                            <Save className="h-5 w-5" />
                            {loading ? 'Sauvegarde...' : 'Enregistrer les préférences'}
                        </button>
                    </div>
                </div>

                {/* Loyalty Config */}
                <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                    <div className="p-6 border-b border-gray-100">
                        <h2 className="text-lg font-bold text-gray-900 flex items-center gap-2">
                            <Award className="h-5 w-5 text-yellow-500" />
                            Programme de Fidélité
                        </h2>
                    </div>
                    <div className="p-6 space-y-6">
                        <div className="bg-yellow-50 p-4 rounded-xl border border-yellow-100">
                            <p className="text-sm text-yellow-800 font-medium">
                                Les clients ayant effectué plus de <span className="font-bold">5 réservations</span> sont considérés comme fidèles.
                            </p>
                        </div>

                        <div>
                            <label className="block text-sm font-bold text-gray-700 mb-2 flex items-center gap-2">
                                <Percent className="h-4 w-4 text-gray-400" /> Réduction (%)
                            </label>
                            <div className="relative">
                                <input
                                    type="number"
                                    min="0" max="100"
                                    className="w-full p-3 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-yellow-400 font-bold"
                                    value={loyaltyDiscount}
                                    onChange={(e) => setLoyaltyDiscount(e.target.value)}
                                />
                                <span className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-500 font-bold">%</span>
                            </div>
                            <p className="text-xs text-gray-500 mt-2">Ce pourcentage sera automatiquement déduit du total pour les clients fidèles.</p>
                        </div>

                        <button
                            onClick={handleSaveLoyalty}
                            disabled={loyaltyLoading}
                            className="w-full bg-yellow-400 text-black font-bold py-3 rounded-xl hover:bg-yellow-300 transition flex items-center justify-center gap-2"
                        >
                            <Save className="h-5 w-5" />
                            {loyaltyLoading ? 'Mise à jour...' : 'Mettre à jour la réduction'}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default SuperAdminConfig;
