import React from 'react';
import { Calendar, MapPin, Clock } from 'lucide-react';
import { useCurrency } from '../../hooks/useCurrency';

interface RentalCardProps {
    id: string | number;
    vehicleName: string;
    vehicleImage: string;
    status: 'PENDING' | 'CONFIRMED' | 'ACTIVE' | 'UPCOMING' | 'COMPLETED' | 'CANCELLED';
    startDate: string;
    endDate: string;
    totalPrice: number;
    pickupLocation: string;
    onDetailsClick?: (id: string | number) => void;
    onExtendClick?: (id: string | number) => void;
}

const RentalCard: React.FC<RentalCardProps> = ({
    id,
    vehicleName,
    vehicleImage,
    status,
    startDate,
    endDate,
    totalPrice,
    pickupLocation,
    onDetailsClick,
    onExtendClick
}) => {
    const { formatPrice } = useCurrency();

    const statusConfig = {
        PENDING: { label: 'En Attente', classes: 'bg-yellow-100 text-yellow-800 border-yellow-200' },
        CONFIRMED: { label: 'Confirmée', classes: 'bg-green-100 text-green-800 border-green-200' },
        ACTIVE: { label: 'En Cours', classes: 'bg-green-100 text-green-800 border-green-200' },
        UPCOMING: { label: 'À Venir', classes: 'bg-blue-100 text-blue-800 border-blue-200' },
        COMPLETED: { label: 'Terminée', classes: 'bg-gray-100 text-gray-800 border-gray-200' },
        CANCELLED: { label: 'Annulée', classes: 'bg-red-100 text-red-800 border-red-200' },
    };

    const currentStatus = statusConfig[status] || { label: status, classes: 'bg-gray-100 text-gray-800 border-gray-200' };

    return (
        <div className="bg-white rounded-2xl overflow-hidden shadow-sm border border-gray-100 hover:shadow-md transition-shadow group">
            <div className="flex flex-col sm:flex-row">
                {/* Image Section */}
                <div className="sm:w-48 h-48 sm:h-auto relative">
                    <img
                        src={vehicleImage}
                        alt={vehicleName}
                        className="w-full h-full object-cover transform group-hover:scale-105 transition-transform duration-500"
                    />
                    <div className="absolute top-3 left-3">
                        <span className={`px-3 py-1 rounded-full text-xs font-bold border ${currentStatus.classes}`}>
                            {currentStatus.label}
                        </span>
                    </div>
                </div>

                {/* Content Section */}
                <div className="p-6 flex-1 flex flex-col justify-between">
                    <div>
                        <div className="flex justify-between items-start mb-2">
                            <h3 className="text-xl font-black text-gray-900 italic">{vehicleName}</h3>
                            <p className="text-lg font-bold text-yellow-500">
                                {formatPrice(totalPrice)}
                            </p>
                        </div>

                        <div className="space-y-2 mt-4">
                            <div className="flex items-center text-sm text-gray-500">
                                <Calendar className="w-4 h-4 mr-2 text-gray-400" />
                                <span>{startDate} - {endDate}</span>
                            </div>
                            <div className="flex items-center text-sm text-gray-500">
                                <MapPin className="w-4 h-4 mr-2 text-gray-400" />
                                <span>{pickupLocation}</span>
                            </div>
                        </div>
                    </div>

                    <div className="mt-6 flex gap-3">
                        <button
                            onClick={() => onDetailsClick?.(id)}
                            className="flex-1 bg-black text-white px-4 py-2 rounded-lg text-sm font-bold hover:bg-gray-800 transition-colors"
                        >
                            Détails
                        </button>
                        {status === 'ACTIVE' && (
                            <button
                                onClick={() => onExtendClick?.(id)}
                                className="flex-1 bg-yellow-400 text-black px-4 py-2 rounded-lg text-sm font-bold hover:bg-yellow-300 transition-colors"
                            >
                                Prolonger
                            </button>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default RentalCard;
