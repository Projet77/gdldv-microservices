import React from 'react';
import { Calendar, Wallet, Clock, Car } from 'lucide-react';
import StatCard from '../../../components/dashboard/StatCard';
import RentalCard from '../../../components/dashboard/RentalCard';

const ClientOverview: React.FC = () => {
    // Mock user data
    const user = { firstName: 'Abdou' };

    return (
        <div className="max-w-7xl mx-auto">
            <div className="mb-8">
                <h1 className="text-3xl font-black text-gray-900">
                    Bonjour, <span className="text-transparent bg-clip-text bg-gradient-to-r from-yellow-400 to-yellow-600">{user.firstName}</span> 👋
                </h1>
                <p className="mt-2 text-gray-500 text-lg">Prêt pour votre prochaine expérience de conduite ?</p>
            </div>

            {/* Stats Grid */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-10">
                <StatCard
                    title="Réservations Actives"
                    value="1"
                    icon={Car}
                    color="yellow"
                />
                <StatCard
                    title="Dépenses Totales"
                    value="1 250 000 FCFA"
                    icon={Wallet}
                    color="green"
                    trend="+12%"
                    trendUp={true}
                />
                <StatCard
                    title="Jours de Locations"
                    value="14"
                    icon={Calendar}
                    color="blue"
                />
            </div>

            {/* Current Rentals */}
            <div className="mb-10">
                <div className="flex items-center justify-between mb-6">
                    <h2 className="text-xl font-bold text-gray-900 flex items-center gap-2">
                        <Clock className="w-5 h-5 text-yellow-500" />
                        En Cours
                    </h2>
                    <button className="text-sm font-bold text-yellow-600 hover:text-yellow-700">
                        Voir tout
                    </button>
                </div>

                <div className="space-y-4">
                    <RentalCard
                        vehicleName="Lamborghini Urus"
                        vehicleImage="/images/suv_car.png"
                        status="ACTIVE"
                        startDate="26 Déc 2025"
                        endDate="29 Déc 2025"
                        pickupLocation="Agence Ziguinchor Centre"
                        totalPrice={750000}
                    />
                </div>
            </div>

            {/* Recent Rentals */}
            <div>
                <h2 className="text-xl font-bold text-gray-900 mb-6 flex items-center gap-2">
                    <HistoryIcon className="w-5 h-5 text-gray-400" />
                    Récents
                </h2>
                <div className="space-y-4 opacity-75 grayscale hover:grayscale-0 transition-all duration-500">
                    <RentalCard
                        vehicleName="Peugeot 208"
                        vehicleImage="/images/city_car.png"
                        status="COMPLETED"
                        startDate="10 Déc 2025"
                        endDate="12 Déc 2025"
                        pickupLocation="Aéroport Dakar"
                        totalPrice={60000}
                    />
                </div>
            </div>
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
