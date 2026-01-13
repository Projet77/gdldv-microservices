// ... imports
import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { Calendar, CreditCard, ArrowLeft, Check, AlertCircle, Loader, MapPin, Car } from 'lucide-react';
import api from '../../../services/api';
import { authService } from '../../../services/authService';
import { useCurrency } from '../../../hooks/useCurrency';

// Reusing the interface or defining a local one if import is tricky
interface Vehicle {
    id: string;
    brand: string;
    model: string;
    pricePerDay: number; // Note: Frontend uses number, Backend might expect Double
    image: string;
}

const ClientReservations: React.FC = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const { id } = useParams<{ id: string }>();
    const { formatPrice } = useCurrency();
    const [selectedVehicle, setSelectedVehicle] = useState<Vehicle | null>(null);

    // Form State
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');
    const [totalPrice, setTotalPrice] = useState(0);
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState<{ type: 'success' | 'error', text: string } | null>(null);

    // Load Reservations List (Future implementation)
    const [reservations, setReservations] = useState<any[]>([]);
    const [detailReservation, setDetailReservation] = useState<any | null>(null);

    // Extension State
    const [isExtending, setIsExtending] = useState(false);
    const [extensionDate, setExtensionDate] = useState('');
    const [extensionCost, setExtensionCost] = useState(0);

    useEffect(() => {
        // Check if we came from the Homepage with a vehicle
        if (location.state?.selectedVehicle) {
            setSelectedVehicle(location.state.selectedVehicle);
        } else if (id) {
            // Fetch specific reservation details
            fetchReservationDetails(id);

            // Check for extend action
            const searchParams = new URLSearchParams(location.search);
            if (searchParams.get('action') === 'extend') {
                setIsExtending(true);
            }
        } else {
            fetchReservations();
        }
    }, [location.state, id, location.search]);

    const fetchReservationDetails = async (resId: string) => {
        setLoading(true);
        try {
            // Mock logic for the demo IDs used in Overview
            if (resId === '1' || resId === '2') {
                // Simulate network delay
                setTimeout(() => {
                    setDetailReservation({
                        id: resId,
                        vehicleName: resId === '1' ? 'Lamborghini Urus' : 'Peugeot 208',
                        vehicleImage: resId === '1' ? '/images/suv_car.png' : '/images/city_car.png',
                        status: resId === '1' ? 'ACTIVE' : 'COMPLETED',
                        startDate: resId === '1' ? '2025-12-26' : '2025-12-10',
                        endDate: resId === '1' ? '2025-12-29' : '2025-12-12',
                        totalPrice: resId === '1' ? 750000 : 60000,
                        pricePerDay: resId === '1' ? 250000 : 20000, // Added pricePerDay
                        pickupLocation: resId === '1' ? 'Agence Ziguinchor Centre' : 'Aéroport Dakar'
                    });
                    setLoading(false);
                }, 500);
                return;
            }

            const response = await api.get(`/api/reservations/${resId}`);
            setDetailReservation(response.data);
        } catch (error) {
            console.warn("Could not fetch reservation details", error);
            // Fallback or error state
            setLoading(false);
        }
    };

    const handleExtensionDateChange = (date: string) => {
        setExtensionDate(date);
        if (!detailReservation) return;

        const currentEnd = new Date(detailReservation.endDate);
        const newEnd = new Date(date);

        const diffTime = newEnd.getTime() - currentEnd.getTime();
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

        if (diffDays > 0) {
            setExtensionCost(diffDays * detailReservation.pricePerDay);
        } else {
            setExtensionCost(0);
        }
    };

    const handleExtensionSubmit = async () => {
        if (extensionCost <= 0) return;

        // Simulate API call for extension
        if (window.confirm(`Confirmer la prolongation pour ${formatPrice(extensionCost)} ?`)) {
            alert('Prolongation effectuée avec succès !');
            setIsExtending(false);
            // Refresh details (mock update)
            setDetailReservation((prev: any) => ({
                ...prev,
                endDate: extensionDate,
                totalPrice: prev.totalPrice + extensionCost
            }));
            navigate(location.pathname, { replace: true });
        }
    };

    const handleDownloadInvoice = () => {
        import('jspdf').then(({ default: jsPDF }) => {
            const doc = new jsPDF();

            // --- HEADER WITH LOGO ---
            // Draw a black header background
            doc.setFillColor(0, 0, 0); // Black
            doc.rect(0, 0, 210, 40, 'F');

            // Add Logo Text (Simulating Logo Image for now if accurate image data not available)
            doc.setTextColor(255, 255, 255); // White
            doc.setFontSize(22);
            doc.setFont("helvetica", "bold");
            doc.text("ACA LOCATIONS", 20, 25);

            doc.setTextColor(250, 204, 21); // Yellow-400 equivalent for "MOTORS" or accent
            doc.setFontSize(10);
            doc.text("LOCATION DE VOITURES", 20, 32);

            // --- INVOICE DETAILS ---
            doc.setTextColor(0, 0, 0);
            doc.setFontSize(18);
            doc.text("FACTURE", 150, 60);

            doc.setFontSize(10);
            doc.setTextColor(100, 100, 100);
            doc.text(`Reference: #${detailReservation.id}`, 150, 66);
            doc.text(`Date: ${new Date().toLocaleDateString()}`, 150, 71);

            // Client Info (Mock)
            doc.setFontSize(12);
            doc.setTextColor(0, 0, 0);
            doc.text("CLIENT:", 20, 60);
            doc.setFontSize(10);
            doc.setTextColor(80, 80, 80);
            const user = authService.getCurrentUser();
            doc.text(user?.email || "Email Client", 20, 66);

            // --- VEHICLE INFO ---
            doc.setDrawColor(200, 200, 200);
            doc.line(20, 80, 190, 80);

            doc.setFontSize(14);
            doc.setTextColor(0, 0, 0);
            doc.text("DETAILS RESERVATION", 20, 90);

            doc.setFontSize(10);
            doc.text(`Vehicule: ${detailReservation.vehicleName}`, 20, 100);
            doc.text(`Lieu: ${detailReservation.pickupLocation}`, 20, 106);

            doc.text(`Du: ${new Date(detailReservation.startDate).toLocaleDateString()}`, 120, 100);
            doc.text(`Au: ${new Date(detailReservation.endDate).toLocaleDateString()}`, 120, 106);

            // --- PRICING ---
            doc.setDrawColor(0, 0, 0);
            doc.setLineWidth(0.5);
            doc.line(20, 120, 190, 120);

            doc.setFontSize(12);
            doc.text("Description", 20, 128);
            doc.text("Montant", 170, 128);

            doc.line(20, 132, 190, 132);

            doc.setFontSize(10);
            doc.text(`Location ${detailReservation.vehicleName}`, 20, 140);

            // Helper to sanitize text for PDF (removes non-breaking spaces causing issues)
            const safeFormat = (amount: number) => {
                return formatPrice(amount).replace(/\u00a0/g, ' ').replace(/\u202f/g, ' ');
            };

            doc.text(`${safeFormat(detailReservation.totalPrice)}`, 170, 140, { align: "left" }); // Unit/Total line item

            // Total
            doc.setFontSize(16);
            doc.setFont("helvetica", "bold");
            doc.text("TOTAL", 120, 160);
            doc.setTextColor(0, 0, 0); // Ensure black
            doc.text(`${safeFormat(detailReservation.totalPrice)}`, 170, 160, { align: "left" });

            // Footer
            doc.setFontSize(8);
            doc.setTextColor(150, 150, 150);
            doc.text("Merci pour votre confiance. Prestige Drive International.", 105, 280, { align: "center" });

            doc.save(`Facture_${detailReservation.id}.pdf`);
        });
    };

    const fetchReservations = async () => {
        try {
            const user = authService.getCurrentUser();
            if (!user) {
                console.warn('⚠️ No user found when fetching reservations');
                return;
            }
            console.log('📋 Fetching reservations for user:', user.id);
            // Endpoint needs to exist: GET /api/reservations/user/{id} or similar
            const response = await api.get(`/api/reservations/user/${user.id}`);
            setReservations(response.data);
            console.log('✅ Reservations loaded:', response.data.length);
        } catch (error: any) {
            console.warn("⚠️ Error fetching reservations:", error.response?.status, error.message);
            // Ne pas planter la page si l'endpoint n'existe pas encore
            // Afficher simplement "Aucune réservation"
            setReservations([]);
        }
    };

    const calculateTotal = (start: string, end: string) => {
        if (!start || !end || !selectedVehicle) return 0;
        const d1 = new Date(start);
        const d2 = new Date(end);
        const diffTime = Math.abs(d2.getTime() - d1.getTime());
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        // Ensure at least 1 day
        const days = diffDays > 0 ? diffDays : 1;
        return days * selectedVehicle.pricePerDay;
    };

    const handleDateChange = (type: 'start' | 'end', value: string) => {
        if (type === 'start') {
            setStartDate(value);
            if (endDate) setTotalPrice(calculateTotal(value, endDate));
        } else {
            setEndDate(value);
            if (startDate) setTotalPrice(calculateTotal(startDate, value));
        }
    };

    const handleBooking = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setMessage(null);

        const user = authService.getCurrentUser();
        if (!user || !user.id || !user.email) {
            setMessage({ type: 'error', text: 'Utilisateur non identifié ou email manquant.' });
            setLoading(false);
            return;
        }

        try {
            // 1. Create Reservation
            const userId = typeof user.id === 'string' ? parseInt(user.id) : user.id;
            const vehicleId = typeof selectedVehicle!.id === 'string' ? parseInt(selectedVehicle!.id) : selectedVehicle!.id;

            const payload = {
                userId: userId,
                vehicleId: vehicleId,
                // Send Local Date Time format (YYYY-MM-DDTHH:mm:ss) - append T00:00:00 to date strings
                startDate: `${startDate}T00:00:00`,
                endDate: `${endDate}T23:59:59`,
                options: [] // Empty options for now
            };
            console.log('📦 Sending Payload:', payload);
            console.log('📦 Payload Types:', {
                userId: typeof payload.userId,
                vehicleId: typeof payload.vehicleId,
                startDate: typeof payload.startDate,
                endDate: typeof payload.endDate
            });

            const response = await api.post('/api/reservations', payload);
            const reservationId = response.data.id;
            const confirmationNumber = response.data.confirmationNumber;

            // 2. Success - Reservation created (waiting for admin confirmation)
            setMessage({
                type: 'success',
                text: `Réservation créée avec succès! Numéro: ${confirmationNumber}`
            });

            // Show confirmation message
            setTimeout(() => {
                setMessage({
                    type: 'success',
                    text: '✅ Demande envoyée! En attente de validation par notre équipe.'
                });

                // Reset form and show reservations list after 2 seconds
                setTimeout(() => {
                    setSelectedVehicle(null);
                    fetchReservations();
                }, 2000);
            }, 1500);

        } catch (error: any) {
            console.error('❌ Full Error Object:', error);
            console.error('❌ Error Response:', error.response);
            console.error('❌ Error Response Data:', error.response?.data);

            let errMsg = 'Échec de la réservation.';
            if (error.response?.data?.error) {
                errMsg = error.response.data.error;
            } else if (error.response?.data?.message) {
                errMsg = error.response.data.message;
            } else if (typeof error.response?.data === 'string') {
                errMsg = error.response.data;
            }

            setMessage({ type: 'error', text: errMsg });
        } finally {
            setLoading(false);
        }
    };

    // VIEW: Details of existing reservation
    if (id && detailReservation) {
        return (
            <div className="py-8 bg-gray-50 min-h-screen relative">
                {/* Extend Modal Overlay */}
                {isExtending && (
                    <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
                        <div className="bg-white rounded-2xl w-full max-w-md p-6 shadow-2xl animate-fade-in text-left">
                            <h3 className="text-xl font-black text-gray-900 mb-4">Prolonger la location</h3>
                            <div className="space-y-4">
                                <div>
                                    <label className="block text-sm font-bold text-gray-700 mb-1">Date de fin actuelle</label>
                                    <p className="text-gray-500">{new Date(detailReservation.endDate).toLocaleDateString()}</p>
                                </div>
                                <div>
                                    <label className="block text-sm font-bold text-gray-700 mb-1">Nouvelle date de fin</label>
                                    <input
                                        type="date"
                                        min={detailReservation.endDate}
                                        className="w-full border border-gray-300 rounded-lg p-3 focus:ring-yellow-400 focus:border-yellow-400 outline-none"
                                        onChange={(e) => handleExtensionDateChange(e.target.value)}
                                    />
                                </div>
                                {extensionCost > 0 && (
                                    <div className="bg-green-50 p-4 rounded-lg flex justify-between items-center text-green-800 font-bold border border-green-100">
                                        <span>Coût supplémentaire</span>
                                        <span>+{formatPrice(extensionCost)}</span>
                                    </div>
                                )}
                                <div className="flex gap-3 pt-2">
                                    <button
                                        onClick={() => setIsExtending(false)}
                                        className="flex-1 px-4 py-3 border border-gray-300 rounded-lg font-bold text-gray-700 hover:bg-gray-50 transition-colors"
                                    >
                                        Annuler
                                    </button>
                                    <button
                                        onClick={handleExtensionSubmit}
                                        disabled={extensionCost <= 0}
                                        className="flex-1 px-4 py-3 bg-yellow-400 rounded-lg font-bold text-black hover:bg-yellow-300 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                                    >
                                        Confirmer
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                )}

                <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
                    <button
                        onClick={() => navigate('/dashboard/client/overview')}
                        className="flex items-center text-gray-500 hover:text-black mb-6 transition-colors"
                    >
                        <ArrowLeft className="w-5 h-5 mr-2" /> Retour au tableau de bord
                    </button>

                    <div className="bg-white rounded-2xl shadow-xl overflow-hidden border border-gray-100">
                        {/* Header Image */}
                        <div className="h-64 relative bg-gray-900">
                            <img src={detailReservation.vehicleImage} alt={detailReservation.vehicleName} className="w-full h-full object-cover opacity-80" />
                            <div className="absolute inset-0 bg-gradient-to-t from-black/80 to-transparent flex items-end p-8">
                                <div>
                                    <h1 className="text-3xl font-black text-white italic">{detailReservation.vehicleName}</h1>
                                    <div className="flex items-center gap-3 mt-2">
                                        <span className={`px-3 py-1 rounded-full text-xs font-bold ${detailReservation.status === 'ACTIVE' ? 'bg-green-500 text-white' : 'bg-gray-500 text-white'
                                            }`}>
                                            {detailReservation.status === 'ACTIVE' ? 'EN COURS' : detailReservation.status}
                                        </span>
                                        <span className="text-yellow-400 font-bold text-xl">{formatPrice(detailReservation.totalPrice)}</span>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div className="p-8 grid grid-cols-1 md:grid-cols-2 gap-8">
                            <div className="space-y-6">
                                <h3 className="text-xl font-bold text-gray-900 flex items-center gap-2">
                                    <Calendar className="w-5 h-5 text-yellow-500" /> Dates
                                </h3>
                                <div className="bg-gray-50 p-4 rounded-xl border border-gray-100 flex justify-between items-center">
                                    <div>
                                        <p className="text-sm text-gray-500">Du</p>
                                        <p className="font-bold">{new Date(detailReservation.startDate).toLocaleDateString()}</p>
                                    </div>
                                    <ArrowLeft className="w-4 h-4 text-gray-400 rotate-180" />
                                    <div className="text-right">
                                        <p className="text-sm text-gray-500">Au</p>
                                        <p className="font-bold">{new Date(detailReservation.endDate).toLocaleDateString()}</p>
                                    </div>
                                </div>
                            </div>

                            <div className="space-y-6">
                                <h3 className="text-xl font-bold text-gray-900 flex items-center gap-2">
                                    <MapPin className="w-5 h-5 text-yellow-500" /> Lieu
                                </h3>
                                <div className="bg-gray-50 p-4 rounded-xl border border-gray-100">
                                    <p className="font-bold text-gray-800">{detailReservation.pickupLocation}</p>
                                    <p className="text-sm text-gray-500">Agence principale</p>
                                </div>
                            </div>
                        </div>

                        {/* Actions */}
                        <div className="p-8 bg-gray-50 border-t border-gray-100 flex justify-end gap-3">
                            {detailReservation.status === 'ACTIVE' && (
                                <button onClick={() => setIsExtending(true)} className="bg-yellow-400 hover:bg-yellow-300 text-black px-6 py-3 rounded-xl font-bold shadow-lg transition-transform hover:-translate-y-1">
                                    Prolonger la location
                                </button>
                            )}
                            <button
                                onClick={handleDownloadInvoice}
                                className="bg-white border border-gray-300 text-gray-700 px-6 py-3 rounded-xl font-bold hover:bg-gray-50"
                            >
                                Télécharger la facture
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        );
    } else if (id && loading) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <Loader className="w-10 h-10 animate-spin text-yellow-500" />
            </div>
        );
    }


    // VIEW: New Reservation Form
    if (selectedVehicle) {
        return (
            <div className="py-8 bg-gray-50 min-h-screen">
                <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8">
                    <button
                        onClick={() => setSelectedVehicle(null)}
                        className="flex items-center text-gray-500 hover:text-black mb-6 transition-colors"
                    >
                        <ArrowLeft className="w-5 h-5 mr-2" /> Retour à mes réservations
                    </button>

                    <div className="bg-white rounded-2xl shadow-xl overflow-hidden border border-gray-100">
                        {/* Header */}
                        <div className="bg-black text-white p-6 flex items-center justify-between relative overflow-hidden">
                            <div className="relative z-10">
                                <h2 className="text-2xl font-black italic uppercase">Confirmer la Réservation</h2>
                                <p className="text-yellow-400 font-bold mt-1">{selectedVehicle.brand} {selectedVehicle.model}</p>
                            </div>
                            <img src={selectedVehicle.image} alt="Car" className="absolute right-0 top-0 h-full w-1/2 object-cover opacity-30 mask-image-gradient" />
                        </div>

                        <form onSubmit={handleBooking} className="p-8 space-y-6">
                            {message && (
                                <div className={`p-4 rounded-xl flex items-center gap-3 ${message.type === 'success' ? 'bg-green-50 text-green-700 border border-green-100' : 'bg-red-50 text-red-700 border border-red-100'}`}>
                                    {message.type === 'success' ? <Check className="w-5 h-5" /> : <AlertCircle className="w-5 h-5" />}
                                    <p className="font-bold">{message.text}</p>
                                </div>
                            )}

                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                <div>
                                    <label className="block text-sm font-bold text-gray-700 mb-2">Début Location</label>
                                    <div className="relative">
                                        <Calendar className="absolute left-3 top-3 h-5 w-5 text-gray-400" />
                                        <input
                                            type="date"
                                            required
                                            value={startDate}
                                            onChange={(e) => handleDateChange('start', e.target.value)}
                                            className="block w-full pl-10 pr-3 py-3 border border-gray-200 rounded-xl focus:ring-yellow-400 focus:border-yellow-400"
                                        />
                                    </div>
                                </div>
                                <div>
                                    <label className="block text-sm font-bold text-gray-700 mb-2">Fin Location</label>
                                    <div className="relative">
                                        <Calendar className="absolute left-3 top-3 h-5 w-5 text-gray-400" />
                                        <input
                                            type="date"
                                            required
                                            min={startDate}
                                            value={endDate}
                                            onChange={(e) => handleDateChange('end', e.target.value)}
                                            className="block w-full pl-10 pr-3 py-3 border border-gray-200 rounded-xl focus:ring-yellow-400 focus:border-yellow-400"
                                        />
                                    </div>
                                </div>
                            </div>

                            <div className="bg-gray-50 p-6 rounded-xl border border-gray-100">
                                <div className="flex justify-between items-center mb-2">
                                    <span className="text-gray-500">Prix par jour</span>
                                    <span className="font-bold">{formatPrice(selectedVehicle.pricePerDay)}/jour</span>
                                </div>
                                <div className="flex justify-between items-center text-xl font-black mt-4 pt-4 border-t border-gray-200">
                                    <span>Total Estimé</span>
                                    <span className="text-yellow-600">{formatPrice(totalPrice)}</span>
                                </div>
                            </div>

                            <button
                                type="submit"
                                disabled={loading || totalPrice === 0}
                                className="w-full flex items-center justify-center bg-yellow-400 hover:bg-yellow-300 text-black font-black py-4 rounded-xl text-lg shadow-lg hover:shadow-xl transition-all transform hover:-translate-y-1 disabled:opacity-50 disabled:cursor-not-allowed"
                            >
                                {loading ? <Loader className="animate-spin w-6 h-6" /> : 'CONFIRMER LA RÉSERVATION'}
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        );
    }

    // VIEW: Reservations List
    return (
        <div className="py-8 bg-gray-50 min-h-screen">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="flex justify-between items-center mb-8">
                    <h1 className="text-3xl font-black text-gray-900 tracking-tight">Mes Réservations</h1>
                    <button onClick={() => navigate('/')} className="text-sm font-bold text-yellow-600 hover:text-yellow-700 underline">
                        Réserver un autre véhicule
                    </button>
                </div>

                {reservations.length > 0 ? (
                    <div className="bg-white shadow overflow-hidden sm:rounded-md">
                        <ul className="divide-y divide-gray-200">
                            {/* Map reservations here */}
                            {reservations.map((res: any) => (
                                <li key={res.id} className="px-4 py-4 sm:px-6 hover:bg-gray-50">
                                    <div className="flex items-center justify-between">
                                        <p className="text-sm font-medium text-indigo-600 truncate">Réservation #{res.id}</p>
                                        <div className="ml-2 flex-shrink-0 flex">
                                            <span className="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">
                                                {res.status}
                                            </span>
                                        </div>
                                    </div>
                                    <div className="mt-2 sm:flex sm:justify-between">
                                        <div className="sm:flex">
                                            <p className="flex items-center text-sm text-gray-500">
                                                <Calendar className="flex-shrink-0 mr-1.5 h-5 w-5 text-gray-400" />
                                                {new Date(res.startDate).toLocaleDateString()} - {new Date(res.endDate).toLocaleDateString()}
                                            </p>
                                        </div>
                                    </div>
                                </li>
                            ))}
                        </ul>
                    </div>
                ) : (
                    <div className="text-center py-20 bg-white rounded-2xl shadow-sm border border-gray-100">
                        <Calendar className="mx-auto h-12 w-12 text-gray-300" />
                        <h3 className="mt-2 text-sm font-medium text-gray-900">Aucune réservation</h3>
                        <p className="mt-1 text-sm text-gray-500">Commencez par choisir un véhicule de prestige.</p>
                        <div className="mt-6">
                            <button
                                onClick={() => navigate('/')}
                                className="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-black hover:bg-zinc-800 focus:outline-none"
                            >
                                Voir la flotte
                            </button>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default ClientReservations;
