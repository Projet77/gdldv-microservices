import React from 'react';
import { X, Calendar, Users, Fuel, Gauge, MapPin, Shield, Baby } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export interface Vehicle {
  id: string;
  brand: string;
  model: string;
  year: number;
  category: string;
  image: string;
  pricePerDay: number;
  seats: number;
  transmission: string;
  fuel: string;
  hasBabySeat: boolean;
  hasGPS: boolean;
  minAge: number;
  description: string;
}

interface VehicleDetailModalProps {
  vehicle: Vehicle;
  isOpen: boolean;
  onClose: () => void;
}

export default function VehicleDetailModal({ vehicle, isOpen, onClose }: VehicleDetailModalProps) {
  const navigate = useNavigate();

  if (!isOpen || !vehicle) return null;

  const handleReservation = () => {
    const isConnected = !!localStorage.getItem('token');
    if (isConnected) {
      const user = JSON.parse(localStorage.getItem('user') || '{}');
      const role = user.role || 'CLIENT';

      const dashboardRoutes: Record<string, string> = {
        'SUPER_ADMIN': '/dashboard/superadmin',
        'ADMIN': '/dashboard/admin',
        'MANAGER': '/dashboard/manager',
        'AGENT': '/dashboard/agent',
        'CLIENT': '/dashboard/client/reservations'
      };

      const targetRoute = dashboardRoutes[role] || '/dashboard/client/reservations';
      navigate(targetRoute, { state: { selectedVehicle: vehicle } });
    } else {
      navigate('/login', { state: { returnTo: '/dashboard/client/reservations', selectedVehicle: vehicle } });
    }
  };

  return (
    <div className="fixed inset-0 z-[100] flex items-center justify-center p-4 bg-black/80 backdrop-blur-sm animate-fadeIn">
      <div className="relative bg-white rounded-3xl shadow-2xl max-w-4xl w-full max-h-[90vh] overflow-y-auto custom-scrollbar">
        {/* Close Button */}
        <button
          onClick={onClose}
          className="absolute top-4 right-4 z-10 w-10 h-10 flex items-center justify-center rounded-full bg-black/50 hover:bg-black/70 text-white transition-all transform hover:scale-110"
        >
          <X className="w-6 h-6" />
        </button>

        {/* Vehicle Image */}
        <div className="relative h-96 bg-gradient-to-br from-gray-900 to-black rounded-t-3xl overflow-hidden group">
          <img
            src={vehicle.image}
            alt={`${vehicle.brand} ${vehicle.model}`}
            className="w-full h-full object-cover transform group-hover:scale-105 transition-transform duration-700"
          />
          <div className="absolute inset-0 bg-gradient-to-t from-black via-transparent to-transparent opacity-90"></div>

          <div className="absolute bottom-0 left-0 right-0 p-8 transform translate-y-0 transition-transform duration-500">
            <div className="flex items-end justify-between">
              <div>
                <span className="inline-block px-3 py-1 rounded-full bg-yellow-400/20 text-yellow-400 font-bold text-xs uppercase tracking-wider mb-3 border border-yellow-400/30">
                  {vehicle.category}
                </span>
                <h2 className="text-4xl font-black text-white italic mb-1 tracking-tight">
                  {vehicle.brand} <span className="font-normal text-gray-300 not-italic">{vehicle.model}</span>
                </h2>
              </div>
              <div className="text-right">
                <p className="text-yellow-400 font-black text-4xl tracking-tighter shadow-yellow-400/20 drop-shadow-lg">
                  {vehicle.pricePerDay.toLocaleString('fr-FR')}
                  <span className="text-lg text-white ml-2 font-bold">CFA</span>
                </p>
                <p className="text-gray-400 text-sm font-medium">par jour</p>
              </div>
            </div>
          </div>
        </div>

        {/* Vehicle Details */}
        <div className="p-8 lg:p-10">
          <div className="prose prose-lg text-gray-600 mb-10 max-w-none">
            <p className="leading-relaxed text-lg">{vehicle.description}</p>
          </div>

          {/* Specifications Grid */}
          <div className="grid grid-cols-2 lg:grid-cols-3 gap-6 mb-10">
            <SpecItem icon={Calendar} label="Année" value={vehicle.year} />
            <SpecItem icon={Users} label="Capacité" value={`${vehicle.seats} Passagers`} />
            <SpecItem icon={Fuel} label="Carburant" value={vehicle.fuel} />
            <SpecItem icon={Gauge} label="Transmission" value={vehicle.transmission} />
            <SpecItem icon={Shield} label="Age Minimum" value={`${vehicle.minAge} ans`} />
            <SpecItem icon={MapPin} label="GPS" value={vehicle.hasGPS ? 'Inclus' : 'En option'} highlight={vehicle.hasGPS} />
          </div>

          {/* Options & Features */}
          <div className="mb-10">
            <h3 className="text-lg font-bold text-gray-900 mb-6 flex items-center gap-2">
              <span className="w-1 h-6 bg-yellow-400 rounded-full"></span>
              Équipements Inclus
            </h3>
            <div className="flex flex-wrap gap-4">
              <FeatureBadge icon={Shield} label="Assurance Tous Risques" />
              <FeatureBadge icon={Users} label="Assistance 24/7" />
              {vehicle.hasBabySeat && <FeatureBadge icon={Baby} label="Siège Bébé Disponible" />}
              <FeatureBadge icon={MapPin} label="Kilométrage Illimité" />
            </div>
          </div>

          {/* CTA Buttons */}
          <div className="flex flex-col sm:flex-row gap-4 pt-6 border-t border-gray-100">
            <button
              onClick={handleReservation}
              className="flex-1 bg-black text-white px-8 py-4 rounded-xl font-bold text-lg hover:bg-zinc-800 transition-all transform hover:-translate-y-1 shadow-xl flex items-center justify-center gap-2"
            >
              Réserver ce véhicule
            </button>
            <button
              onClick={onClose}
              className="px-8 py-4 border-2 border-gray-100 text-gray-600 rounded-xl font-bold text-lg hover:bg-gray-50 hover:border-gray-200 transition-all"
            >
              Fermer
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

// Helper Components
const SpecItem = ({ icon: Icon, label, value, highlight = false }: any) => (
  <div className="flex items-center gap-4 p-4 rounded-2xl bg-gray-50 border border-gray-100 hover:border-yellow-400/30 transition-colors group">
    <div className={`w-12 h-12 rounded-xl flex items-center justify-center transition-colors ${highlight ? 'bg-yellow-400 text-black' : 'bg-white text-gray-400 group-hover:text-yellow-500 shadow-sm'}`}>
      <Icon className="w-6 h-6" />
    </div>
    <div>
      <p className="text-xs text-gray-500 font-bold uppercase tracking-wider mb-0.5">{label}</p>
      <p className="text-gray-900 font-bold text-base">{value}</p>
    </div>
  </div>
);

const FeatureBadge = ({ icon: Icon, label }: any) => (
  <span className="inline-flex items-center gap-2 px-4 py-2.5 bg-gray-50 text-gray-700 rounded-lg text-sm font-semibold border border-gray-100 hover:bg-yellow-50 hover:text-yellow-800 hover:border-yellow-200 transition-all cursor-default">
    <Icon className="w-4 h-4" /> {label}
  </span>
);
