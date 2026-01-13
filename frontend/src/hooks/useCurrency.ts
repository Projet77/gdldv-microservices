import { useState, useEffect, useCallback } from 'react';
import { authService } from '../services/authService';
import { formatPrice as formatPriceUtil } from '../utils/currencyUtils';

export const useCurrency = () => {
    const [currency, setCurrency] = useState<string>('CFA');

    useEffect(() => {
        const loadCurrency = () => {
            const user = authService.getCurrentUser();
            if (user?.currency) {
                setCurrency(user.currency);
            } else {
                setCurrency('CFA');
            }
        };

        loadCurrency();

        // Listen for global user updates (emitted by SuperAdminConfig)
        window.addEventListener('user-updated', loadCurrency);
        window.addEventListener('storage', loadCurrency); // Also listen to storage changes if feasible

        return () => {
            window.removeEventListener('user-updated', loadCurrency);
            window.removeEventListener('storage', loadCurrency);
        };
    }, []);

    /**
     * Formats the given amount (assumed in CFA) to the current user's preferred currency.
     */
    const formatPrice = useCallback((amountInCfa: number) => {
        return formatPriceUtil(amountInCfa, currency);
    }, [currency]);

    return {
        currency,
        formatPrice
    };
};
