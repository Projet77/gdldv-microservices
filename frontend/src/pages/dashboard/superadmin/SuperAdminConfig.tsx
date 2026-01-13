import React, { useState, useEffect } from 'react';
import { Settings, Save, Globe, Coins, Moon, Sun, Award, Percent } from 'lucide-react';
import { authService } from '../../../services/authService';
import api from '../../../services/api';
import { useTranslation, Trans } from 'react-i18next';

const SuperAdminConfig: React.FC = () => {
    const { t, i18n } = useTranslation();
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
            // Ensure i18n is synced if page reload happened
            if (user.language && i18n.language !== user.language) {
                i18n.changeLanguage(user.language);
            }
        }

        // Load Loyalty Config
        fetchLoyaltyConfig();
    }, [i18n]);

    const fetchLoyaltyConfig = async () => {
        try {
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

                // Instant update
                await i18n.changeLanguage(preferences.language);
                // Dispatch event so DashboardLayout also updates if it listens to something (though it should use i18n too)
                window.dispatchEvent(new Event('user-updated'));

                alert(t('success_prefs'));
            }
        } catch (error) {
            console.error("Failed to save preferences", error);
            alert(t('error_save'));
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
            alert(t('success_loyalty'));
        } catch (error) {
            console.error(error);
            alert(t('error_save'));
        } finally {
            setLoyaltyLoading(false);
        }
    };

    return (
        <div className="space-y-6">
            <div>
                <h1 className="text-3xl font-black text-gray-900 tracking-tight">{t('config_title')}</h1>
                <p className="mt-1 text-gray-500">{t('config_subtitle')}</p>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                {/* User Preferences */}
                <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                    <div className="p-6 border-b border-gray-100">
                        <h2 className="text-lg font-bold text-gray-900 flex items-center gap-2">
                            <Settings className="h-5 w-5 text-gray-400" />
                            {t('personal_prefs')}
                        </h2>
                    </div>
                    <div className="p-6 space-y-6">
                        <div>
                            <label className="block text-sm font-bold text-gray-700 mb-2 flex items-center gap-2">
                                <Globe className="h-4 w-4 text-gray-400" /> {t('language')}
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
                                <Coins className="h-4 w-4 text-gray-400" /> {t('currency')}
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
                            {loading ? t('saving') : t('save_prefs')}
                        </button>
                    </div>
                </div>

                {/* Loyalty Config */}
                <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                    <div className="p-6 border-b border-gray-100">
                        <h2 className="text-lg font-bold text-gray-900 flex items-center gap-2">
                            <Award className="h-5 w-5 text-yellow-500" />
                            {t('loyalty_program')}
                        </h2>
                    </div>
                    <div className="p-6 space-y-6">
                        <div className="bg-yellow-50 p-4 rounded-xl border border-yellow-100">
                            <p className="text-sm text-yellow-800 font-medium">
                                <Trans i18nKey="loyalty_desc">
                                    Les clients ayant effectué plus de <span className="font-bold">5 réservations</span> sont considérés comme fidèles.
                                </Trans>
                            </p>
                        </div>

                        <div>
                            <label className="block text-sm font-bold text-gray-700 mb-2 flex items-center gap-2">
                                <Percent className="h-4 w-4 text-gray-400" /> {t('discount_percent')}
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
                            <p className="text-xs text-gray-500 mt-2">{t('discount_hint')}</p>
                        </div>

                        <button
                            onClick={handleSaveLoyalty}
                            disabled={loyaltyLoading}
                            className="w-full bg-yellow-400 text-black font-bold py-3 rounded-xl hover:bg-yellow-300 transition flex items-center justify-center gap-2"
                        >
                            <Save className="h-5 w-5" />
                            {loyaltyLoading ? t('updating') : t('update_loyalty')}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default SuperAdminConfig;
