import React, { useEffect, useState } from 'react';
import { Calendar, Wallet, Clock, Car, Loader } from 'lucide-react';
import StatCard from '../../../components/dashboard/StatCard';
import RentalCard from '../../../components/dashboard/RentalCard';
import { useNavigate } from 'react-router-dom';
import { useCurrency } from '../../../hooks/useCurrency';
import { authService } from '../../../services/authService';
import api from '../../../services/api';

interface Reservation {
    id: number;
    confirmationNumber: string;
    vehicleId: number;
    userId: number;
    startDate: string;
    endDate: string;
    totalPrice: number;
    status: 'PENDING' | 'CONFIRMED' | 'ACTIVE' | 'COMPLETED' | 'CANCELLED';
    vehicle?: {
        id: number;
        brand: string;
        model: string;
        images: string[];
    };
}

const ClientOverview: React.FC = () => {
    const navigate = useNavigate();
    const { formatPrice } = useCurrency();
    const [reservations, setReservations] = useState<Reservation[]>([]);
    const [loading, setLoading] = useState(true);
    const user = authService.getCurrentUser();

    useEffect(() => {
        fetchReservations();
    }, []);

    const fetchReservations = async () => {
        if (!user?.id) return;

        try {
            setLoading(true);
            const response = await api.get(`/api/reservations/user/${user.id}`);
            setReservations(response.data);
            console.log('✅ Reservations loaded:', response.data);
        } catch (error: any) {
            console.error('⚠️ Error fetching reservations:', error);
            setReservations([]);
        } finally {
            setLoading(false);
        }
    };

    // Filter out PENDING reservations (only show CONFIRMED, ACTIVE, COMPLETED)
    const validReservations = reservations.filter(r => r.status !== 'PENDING' && r.status !== 'CANCELLED');

    // Calculate statistics
    const activeReservations = validReservations.filter(r => r.status === 'ACTIVE' || r.status === 'CONFIRMED');
    const completedReservations = validReservations.filter(r => r.status === 'COMPLETED');
    const totalSpent = completedReservations.reduce((sum, r) => sum + r.totalPrice, 0);

    // Calculate total days
    const totalDays = completedReservations.reduce((sum, r) => {
        const start = new Date(r.startDate);
        const end = new Date(r.endDate);
        const days = Math.ceil((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24));
        return sum + days;
    }, 0);

    const formatDate = (dateString: string) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('fr-FR', { day: 'numeric', month: 'short', year: 'numeric' });
    };

    // Mapping d'images par catégorie
    const categoryImageMap: { [key: string]: string } = {
        'SUV Sport': 'https://images.unsplash.com/photo-1519641471654-76ce0107ad1b?q=80&w=2671&auto=format&fit=crop',
        'Citadine': 'https://images.unsplash.com/photo-1541899481282-d53bffe3c35d?q=80&w=2670&auto=format&fit=crop',
        'Berline': 'https://images.unsplash.com/photo-1623869675785-00c88206972d?q=80&w=2670&auto=format&fit=crop',
        'SUV': 'https://images.unsplash.com/photo-1566008885218-90abf9200ddb?q=80&w=2670&auto=format&fit=crop',
        'SUV Compact': 'https://images.unsplash.com/photo-1632245889029-e412c6310525?q=80&w=2670&auto=format&fit=crop',
        'default': 'https://images.unsplash.com/photo-1494976388531-d1058494cdd8?q=80&w=2670&auto=format&fit=crop'
    };

    const getVehicleImage = (reservation: Reservation) => {
        if (reservation.vehicle?.images && reservation.vehicle.images.length > 0) {
            const imageUrl = reservation.vehicle.images[0];

            // Si l'image commence par http ou https, c'est une URL complète (Unsplash, etc.)
            if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
                return imageUrl;
            }

            // Si c'est un chemin local qui commence par /images/, utiliser l'image de la catégorie
            if (imageUrl.startsWith('/images/')) {
                const category = reservation.vehicle?.category || 'default';
                return categoryImageMap[category] || categoryImageMap['default'];
            }

            return imageUrl;
        }

        // Image par défaut basée sur la catégorie du véhicule
        const category = reservation.vehicle?.category || 'default';
        return categoryImageMap[category] || categoryImageMap['default'];
    };

    const getVehicleName = (reservation: Reservation) => {
        if (reservation.vehicle) {
            return `${reservation.vehicle.brand} ${reservation.vehicle.model}`;
        }
        return 'Véhicule';
    };

    const handleDetails = (id: string | number) => {
        navigate(`/dashboard/client/reservations/${id}`);
    };

    const handleExtend = (id: string | number) => {
        navigate(`/dashboard/client/reservations/${id}?action=extend`);
    };

    if (loading) {
        return (
            <div className="max-w-7xl mx-auto flex items-center justify-center min-h-screen">
                <Loader className="w-10 h-10 animate-spin text-yellow-500" />
            </div>
        );
    }

    return (
        <div className="max-w-7xl mx-auto">
            <div className="mb-8">
                <h1 className="text-3xl font-black text-gray-900">
                    Bonjour, <span className="text-transparent bg-clip-text bg-gradient-to-r from-yellow-400 to-yellow-600">{user?.firstName || 'Client'}</span> 👋
                </h1>
                <p className="mt-2 text-gray-500 text-lg">Prêt pour votre prochaine expérience de conduite ?</p>
            </div>

            {/* Stats Grid */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-10">
                <StatCard
                    title="Réservations Actives"
                    value={activeReservations.length.toString()}
                    icon={Car}
                    color="yellow"
                />
                <StatCard
                    title="Dépenses Totales"
                    value={formatPrice(totalSpent)}
                    icon={Wallet}
                    color="green"
                />
                <StatCard
                    title="Jours de Locations"
                    value={totalDays.toString()}
                    icon={Calendar}
                    color="blue"
                />
            </div>

            {/* Current Rentals */}
            {activeReservations.length > 0 && (
                <div className="mb-10">
                    <div className="flex items-center justify-between mb-6">
                        <h2 className="text-xl font-bold text-gray-900 flex items-center gap-2">
                            <Clock className="w-5 h-5 text-yellow-500" />
                            En Cours
                        </h2>
                        <button
                            onClick={() => navigate('/dashboard/client/reservations')}
                            className="text-sm font-bold text-yellow-600 hover:text-yellow-700"
                        >
                            Voir tout
                        </button>
                    </div>

                    <div className="space-y-4">
                        {activeReservations.map((reservation) => (
                            <RentalCard
                                key={reservation.id}
                                id={reservation.id.toString()}
                                vehicleName={getVehicleName(reservation)}
                                vehicleImage={getVehicleImage(reservation)}
                                status={reservation.status}
                                startDate={formatDate(reservation.startDate)}
                                endDate={formatDate(reservation.endDate)}
                                pickupLocation="Agence Principale"
                                totalPrice={reservation.totalPrice}
                                onDetailsClick={handleDetails}
                                onExtendClick={handleExtend}
                            />
                        ))}
                    </div>
                </div>
            )}

            {/* Recent Rentals */}
            {completedReservations.length > 0 && (
                <div>
                    <h2 className="text-xl font-bold text-gray-900 mb-6 flex items-center gap-2">
                        <HistoryIcon className="w-5 h-5 text-gray-400" />
                        Récents
                    </h2>
                    <div className="space-y-4 opacity-75 grayscale hover:grayscale-0 transition-all duration-500">
                        {completedReservations.slice(0, 3).map((reservation) => (
                            <RentalCard
                                key={reservation.id}
                                id={reservation.id.toString()}
                                vehicleName={getVehicleName(reservation)}
                                vehicleImage={getVehicleImage(reservation)}
                                status={reservation.status}
                                startDate={formatDate(reservation.startDate)}
                                endDate={formatDate(reservation.endDate)}
                                pickupLocation="Agence Principale"
                                totalPrice={reservation.totalPrice}
                                onDetailsClick={handleDetails}
                            />
                        ))}
                    </div>
                </div>
            )}

            {/* Empty State */}
            {validReservations.length === 0 && (
                <div className="text-center py-20 bg-white rounded-2xl shadow-sm border border-gray-100">
                    <Car className="mx-auto h-12 w-12 text-gray-300" />
                    <h3 className="mt-2 text-sm font-medium text-gray-900">Aucune réservation active</h3>
                    <p className="mt-1 text-sm text-gray-500">
                        {reservations.some(r => r.status === 'PENDING')
                            ? 'Vos demandes sont en attente de validation par notre équipe.'
                            : 'Commencez par choisir un véhicule de prestige.'}
                    </p>
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
    );
};

// Helper icon
const HistoryIcon = ({ className }: { className?: string }) => (
    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className={className}>
        <path strokeLinecap="round" strokeLinejoin="round" d="M12 6v6h4.5m4.5 0a9 9 0 11-18 0 9 9 0 0118 0z" />
    </svg>
);

export default ClientOverview;
