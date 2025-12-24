import React, { useEffect, useState } from 'react';
import { getClientDashboard, ClientDashboard as ClientDashboardType } from '../services/dashboardService';
import {
  HomeIcon,
  CalendarIcon,
  HeartIcon,
  CreditCardIcon,
  ChartBarIcon,
  ClockIcon
} from '@heroicons/react/24/outline';

const ClientDashboard: React.FC = () => {
  const [dashboard, setDashboard] = useState<ClientDashboardType | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchDashboard();
  }, []);

  const fetchDashboard = async () => {
    try {
      setLoading(true);
      // TODO: Récupérer le userId depuis le contexte auth
      const userId = 1;
      const data = await getClientDashboard(userId);
      setDashboard(data);
    } catch (err: any) {
      setError(err.message || 'Erreur lors du chargement du dashboard');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-dark-900 to-dark-800 flex items-center justify-center">
        <div className="text-white text-xl">Chargement...</div>
      </div>
    );
  }

  if (error || !dashboard) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-dark-900 to-dark-800 flex items-center justify-center">
        <div className="text-red-500 text-xl">{error || 'Dashboard introuvable'}</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-dark-900 to-dark-800 p-6">
      {/* Header */}
      <div className="bg-dark-800/50 backdrop-blur-xl rounded-2xl p-6 mb-6 border border-primary-500/20">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold text-primary-500">
              Bienvenue, {dashboard.firstName}! 👋
            </h1>
            <p className="text-dark-50 mt-2">
              Membre depuis {new Date(dashboard.memberSince).toLocaleDateString('fr-FR', { month: 'long', year: 'numeric' })}
            </p>
            <div className="mt-3 inline-block bg-primary-500/20 text-primary-500 px-4 py-2 rounded-full">
              {dashboard.membershipBadge}
            </div>
          </div>
          <div className="text-right">
            <div className="text-4xl font-bold text-primary-500">{(dashboard.averageRating || 0).toFixed(1)}</div>
            <div className="text-dark-50">⭐ Note moyenne</div>
          </div>
        </div>
      </div>

      {/* KPIs */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-6">
        <StatCard
          icon={<CalendarIcon className="w-8 h-8" />}
          title="Locations Actives"
          value={dashboard.activeRentals}
          subtitle="En cours"
          color="primary"
        />
        <StatCard
          icon={<ChartBarIcon className="w-8 h-8" />}
          title="Total Locations"
          value={dashboard.totalRentals}
          subtitle="Depuis le début"
          color="blue"
        />
        <StatCard
          icon={<CreditCardIcon className="w-8 h-8" />}
          title="Montant Total"
          value={`${((dashboard.totalSpent || 0) / 1000).toFixed(0)}K`}
          subtitle="FCFA dépensés"
          color="green"
        />
        <StatCard
          icon={<ClockIcon className="w-8 h-8" />}
          title="Durée Moyenne"
          value={`${(dashboard.averageDuration || 0).toFixed(1)}`}
          subtitle="jours par location"
          color="purple"
        />
      </div>

      {/* Réservations actives */}
      {dashboard.currentRentals && dashboard.currentRentals.length > 0 && (
        <div className="bg-dark-800/50 backdrop-blur-xl rounded-2xl p-6 mb-6 border border-primary-500/20">
          <h2 className="text-2xl font-bold text-primary-500 mb-4 flex items-center">
            <HomeIcon className="w-6 h-6 mr-2" />
            Locations en cours
          </h2>
          <div className="space-y-4">
            {dashboard.currentRentals.map((rental) => (
              <RentalCard key={rental.reservationId} rental={rental} />
            ))}
          </div>
        </div>
      )}

      {/* Favoris */}
      {dashboard.favorites && dashboard.favorites.length > 0 && (
        <div className="bg-dark-800/50 backdrop-blur-xl rounded-2xl p-6 mb-6 border border-primary-500/20">
          <h2 className="text-2xl font-bold text-primary-500 mb-4 flex items-center">
            <HeartIcon className="w-6 h-6 mr-2" />
            Mes Véhicules Préférés
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            {dashboard.favorites.map((fav) => (
              <FavoriteCard key={fav.vehicleId} favorite={fav} />
            ))}
          </div>
        </div>
      )}

      {/* Historique */}
      {dashboard.recentHistory && dashboard.recentHistory.length > 0 && (
        <div className="bg-dark-800/50 backdrop-blur-xl rounded-2xl p-6 border border-primary-500/20">
          <h2 className="text-2xl font-bold text-primary-500 mb-4">Historique Récent</h2>
          <div className="space-y-3">
            {dashboard.recentHistory.map((history) => (
              <HistoryCard key={history.reservationId} history={history} />
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

// Components
interface StatCardProps {
  icon: React.ReactNode;
  title: string;
  value: string | number;
  subtitle: string;
  color: string;
}

const StatCard: React.FC<StatCardProps> = ({ icon, title, value, subtitle, color }) => {
  const colors = {
    primary: 'text-primary-500 bg-primary-500/20',
    blue: 'text-blue-500 bg-blue-500/20',
    green: 'text-green-500 bg-green-500/20',
    purple: 'text-purple-500 bg-purple-500/20',
  };

  return (
    <div className="bg-dark-800/50 backdrop-blur-xl rounded-xl p-6 border border-dark-700">
      <div className={`inline-block p-3 rounded-lg mb-4 ${colors[color as keyof typeof colors]}`}>
        {icon}
      </div>
      <div className="text-dark-50 text-sm mb-1">{title}</div>
      <div className="text-3xl font-bold text-dark-50 mb-1">{value}</div>
      <div className="text-dark-400 text-sm">{subtitle}</div>
    </div>
  );
};

interface RentalCardProps {
  rental: ClientDashboardType['currentRentals'][0];
}

const RentalCard: React.FC<RentalCardProps> = ({ rental }) => {
  return (
    <div className="bg-dark-700/50 rounded-xl p-4 border border-primary-500/30 hover:border-primary-500/50 transition-colors">
      <div className="flex justify-between items-start">
        <div>
          <h3 className="text-lg font-bold text-dark-50">{rental.vehicleName}</h3>
          <p className="text-sm text-dark-400">Confirmation: {rental.confirmationNumber}</p>
          <div className="mt-2 space-y-1">
            <p className="text-sm text-dark-300">
              📅 Du {new Date(rental.startDate).toLocaleDateString('fr-FR')} au {new Date(rental.endDate).toLocaleDateString('fr-FR')}
            </p>
            <p className="text-sm text-dark-300">
              📍 {rental.pickupLocation}
            </p>
          </div>
        </div>
        <div className="text-right">
          <div className="text-2xl font-bold text-primary-500">{rental.totalPrice.toLocaleString('fr-FR')} FCFA</div>
          <div className="text-sm text-dark-400 mt-1">
            {rental.daysRemaining > 0 ? `${rental.daysRemaining}j ${rental.hoursRemaining}h restants` : 'À restituer aujourd\'hui'}
          </div>
        </div>
      </div>
    </div>
  );
};

interface FavoriteCardProps {
  favorite: ClientDashboardType['favorites'][0];
}

const FavoriteCard: React.FC<FavoriteCardProps> = ({ favorite }) => {
  return (
    <div className="bg-dark-700/50 rounded-xl p-4 border border-dark-600 hover:border-primary-500/50 transition-colors">
      <div className="aspect-video bg-dark-600 rounded-lg mb-3 flex items-center justify-center text-4xl">
        🚗
      </div>
      <h3 className="font-bold text-dark-50 mb-1">{favorite.name}</h3>
      <div className="flex justify-between items-center">
        <span className="text-primary-500 font-bold">{favorite.dailyPrice.toLocaleString('fr-FR')} FCFA/j</span>
        <span className="text-sm text-dark-400">⭐ {(favorite.averageRating || 0).toFixed(1)}</span>
      </div>
      <button className="mt-3 w-full bg-primary-500 hover:bg-primary-600 text-dark-900 font-bold py-2 rounded-lg transition-colors">
        Louer
      </button>
    </div>
  );
};

interface HistoryCardProps {
  history: ClientDashboardType['recentHistory'][0];
}

const HistoryCard: React.FC<HistoryCardProps> = ({ history }) => {
  return (
    <div className="bg-dark-700/30 rounded-lg p-3 flex justify-between items-center">
      <div>
        <h4 className="font-semibold text-dark-50">{history.vehicleName}</h4>
        <p className="text-sm text-dark-400">
          {new Date(history.startDate).toLocaleDateString('fr-FR')} - {new Date(history.endDate).toLocaleDateString('fr-FR')}
        </p>
        {history.rating > 0 && (
          <p className="text-sm text-primary-500">⭐ {history.rating}/5</p>
        )}
      </div>
      <div className="text-right">
        <div className="font-bold text-dark-50">{history.totalPrice.toLocaleString('fr-FR')} FCFA</div>
        <div className={`text-sm ${history.status === 'COMPLETED' ? 'text-green-500' : 'text-red-500'}`}>
          {history.status === 'COMPLETED' ? '✅ Complété' : '❌ Annulé'}
        </div>
      </div>
    </div>
  );
};

export default ClientDashboard;
