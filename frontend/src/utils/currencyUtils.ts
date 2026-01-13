/**
 * Utility for currency conversion and formatting.
 * Base currency is assumed to be XOF (CFA).
 */

const EXCHANGE_RATES: Record<string, number> = {
    'CFA': 1,
    'EUR': 0.001524, // 1 CFA = 0.001524 EUR (1 EUR = 655.957 CFA)
    'USD': 0.001666, // Approx 1 CFA = 0.001666 USD (1 USD = 600 CFA)
};

const LOCALE_MAP: Record<string, string> = {
    'CFA': 'fr-FR', // Use French formatting for CFA
    'EUR': 'fr-FR',
    'USD': 'en-US',
};

export const getExchangeRate = (targetCurrency: string): number => {
    return EXCHANGE_RATES[targetCurrency] || 1;
};

/**
 * Converts an amount from CFA to the target currency.
 * @param amountInCfa Amount in XOF/CFA
 * @param targetCurrency Currency code (CFA, EUR, USD)
 */
export const convertPrice = (amountInCfa: number, targetCurrency: string): number => {
    const rate = getExchangeRate(targetCurrency);
    return amountInCfa * rate;
};

/**
 * Formats a price into the target currency string.
 * @param amountInCfa Amount in XOF/CFA
 * @param targetCurrency Currency code (CFA, EUR, USD)
 */
export const formatPrice = (amountInCfa: number, targetCurrency: string = 'CFA'): string => {
    const convertedAmount = convertPrice(amountInCfa, targetCurrency);
    const locale = LOCALE_MAP[targetCurrency] || 'fr-FR';

    // XOF is the ISO code for CFA BCEAO
    const currencyCode = targetCurrency === 'CFA' ? 'XOF' : targetCurrency;

    return new Intl.NumberFormat(locale, {
        style: 'currency',
        currency: currencyCode,
        minimumFractionDigits: targetCurrency === 'CFA' ? 0 : 2,
        maximumFractionDigits: targetCurrency === 'CFA' ? 0 : 2,
    }).format(convertedAmount);
};
