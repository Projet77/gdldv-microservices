import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowRight, Star, Shield, Zap, Car } from 'lucide-react';
import VehicleDetailModal, { Vehicle } from '../components/public/VehicleDetailModal';

// MOCK DATA GENERATOR
const GENERATED_VEHICLES: Vehicle[] = [
  {
    id: '1', brand: 'Lamborghini', model: 'Urus', year: 2024, category: 'SUV Sport',
    image: '/images/suv_car.png', pricePerDay: 250000, seats: 5, transmission: 'Automatique',
    fuel: 'Essence', hasBabySeat: true, hasGPS: true, minAge: 25,
    description: 'Le SUV le plus rapide du monde. Puissance brute et luxe absolu.'
  },
  {
    id: '2', brand: 'Ferrari', model: 'F8 Tributo', year: 2023, category: 'Supercar',
    image: '/images/sport_car.png', pricePerDay: 350000, seats: 2, transmission: 'Automatique',
    fuel: 'Essence', hasBabySeat: false, hasGPS: true, minAge: 30,
    description: 'Une icône de Maranello. V8 biturbo, 720 chevaux.'
  },
  {
    id: '3', brand: 'Mercedes-Benz', model: 'G63 AMG', year: 2024, category: 'SUV Luxe',
    image: '/images/suv_car.png', pricePerDay: 200000, seats: 5, transmission: 'Automatique',
    fuel: 'Essence', hasBabySeat: true, hasGPS: true, minAge: 25,
    description: 'Le roi des 4x4. Imposant, bruyant, luxueux.'
  },
  {
    id: '4', brand: 'Porsche', model: '911 GT3', year: 2023, category: 'Sport',
    image: '/images/sport_car.png', pricePerDay: 280000, seats: 2, transmission: 'Automatique',
    fuel: 'Essence', hasBabySeat: false, hasGPS: true, minAge: 28,
    description: 'La perfection sur circuit et sur route.'
  },
  {
    id: '5', brand: 'Range Rover', model: 'Autobiography', year: 2024, category: 'SUV Luxe',
    image: '/images/suv_car.png', pricePerDay: 180000, seats: 5, transmission: 'Automatique',
    fuel: 'Diesel', hasBabySeat: true, hasGPS: true, minAge: 25,
    description: 'Le summum du confort britannique.'
  },
  {
    id: '6', brand: 'Audi', model: 'RSQ8', year: 2023, category: 'SUV Sport',
    image: '/images/suv_car.png', pricePerDay: 190000, seats: 5, transmission: 'Automatique',
    fuel: 'Essence', hasBabySeat: true, hasGPS: true, minAge: 25,
    description: 'La cousine technique de l\'Urus, plus discrète mais tout aussi redoutable.'
  },
  {
    id: '7', brand: 'Bentley', model: 'Continental GT', year: 2022, category: 'Grand Tourisme',
    image: '/images/hero.png', pricePerDay: 300000, seats: 4, transmission: 'Automatique',
    fuel: 'Essence', hasBabySeat: true, hasGPS: true, minAge: 30,
    description: 'Voyagez en première classe, au volant.'
  },
  {
    id: '8', brand: 'Rolls Royce', model: 'Cullinan', year: 2024, category: 'Ultra Luxe',
    image: '/images/suv_car.png', pricePerDay: 500000, seats: 4, transmission: 'Automatique',
    fuel: 'Essence', hasBabySeat: true, hasGPS: true, minAge: 35,
    description: 'Le SUV le plus luxueux jamais construit.'
  },
  {
    id: '9', brand: 'McLaren', model: '720S', year: 2023, category: 'Supercar',
    image: '/images/sport_car.png', pricePerDay: 320000, seats: 2, transmission: 'Automatique',
    fuel: 'Essence', hasBabySeat: false, hasGPS: true, minAge: 30,
    description: 'Une aérodynamique sculptée par le vent.'
  },
  {
    id: '10', brand: 'Tesla', model: 'Model X Plaid', year: 2024, category: 'Électrique',
    image: '/images/suv_car.png', pricePerDay: 150000, seats: 6, transmission: 'Automatique',
    fuel: 'Électrique', hasBabySeat: true, hasGPS: true, minAge: 25,
    description: '1020 chevaux, électrique, familial. Falcon Wings.'
  },
  {
    id: '11', brand: 'BMW', model: 'M4 Competition', year: 2024, category: 'Sport',
    image: '/images/sport_car.png', pricePerDay: 160000, seats: 4, transmission: 'Automatique',
    fuel: 'Essence', hasBabySeat: true, hasGPS: true, minAge: 25,
    description: 'Le coupé sportif par excellence.'
  },
  {
    id: '12', brand: 'Aston Martin', model: 'DBX 707', year: 2023, category: 'SUV Sport',
    image: '/images/suv_car.png', pricePerDay: 260000, seats: 5, transmission: 'Automatique',
    fuel: 'Essence', hasBabySeat: true, hasGPS: true, minAge: 30,
    description: 'Le SUV de James Bond. 707 chevaux.'
  },
  {
    id: '13', brand: 'Porsche', model: 'Taycan Turbo S', year: 2024, category: 'Électrique',
    image: '/images/sport_car.png', pricePerDay: 220000, seats: 4, transmission: 'Automatique',
    fuel: 'Électrique', hasBabySeat: true, hasGPS: true, minAge: 28,
    description: 'L\'âme de Porsche, le cœur électrique.'
  },
  {
    id: '14', brand: 'Maserati', model: 'MC20', year: 2023, category: 'Supercar',
    image: '/images/sport_car.png', pricePerDay: 290000, seats: 2, transmission: 'Automatique',
    fuel: 'Essence', hasBabySeat: false, hasGPS: true, minAge: 30,
    description: 'Le trident de retour au sommet.'
  },
  {
    id: '15', brand: 'Cadillac', model: 'Escalade V', year: 2024, category: 'SUV XXL',
    image: '/images/suv_car.png', pricePerDay: 210000, seats: 7, transmission: 'Automatique',
    fuel: 'Essence', hasBabySeat: true, hasGPS: true, minAge: 25,
    description: 'L\'Amérique en mode XXL et supercharged.'
  },
  {
    id: '16', brand: 'Bugatti', model: 'Chiron', year: 2022, category: 'Hypercar',
    image: '/images/sport_car.png', pricePerDay: 1500000, seats: 2, transmission: 'Automatique',
    fuel: 'Essence', hasBabySeat: false, hasGPS: true, minAge: 40,
    description: 'L\'ultime machine de vitesse.'
  },
  // VÉHICULES GAMME ACCESSIBLE (Clio, Peugeot, etc.)
  {
    id: '17', brand: 'Peugeot', model: '208', year: 2023, category: 'Citadine',
    image: '/images/city_car.png', pricePerDay: 30000, seats: 5, transmission: 'Manuelle',
    fuel: 'Essence', hasBabySeat: true, hasGPS: true, minAge: 21,
    description: 'La citadine préférée des français. Compacte et stylée.'
  },
  {
    id: '18', brand: 'Renault', model: 'Clio V', year: 2023, category: 'Citadine',
    image: '/images/city_car.png', pricePerDay: 25000, seats: 5, transmission: 'Manuelle',
    fuel: 'Diesel', hasBabySeat: true, hasGPS: true, minAge: 21,
    description: 'Polyvalente et confortable, idéale pour la ville.'
  },
  {
    id: '19', brand: 'Toyota', model: 'Corolla', year: 2022, category: 'Berline',
    image: '/images/sedan_car.png', pricePerDay: 40000, seats: 5, transmission: 'Automatique',
    fuel: 'Hybride', hasBabySeat: true, hasGPS: true, minAge: 23,
    description: 'La fiabilité légendaire avec une consommation minimale.'
  },
  {
    id: '20', brand: 'Dacia', model: 'Duster', year: 2023, category: 'SUV Éco',
    image: '/images/compact_suv.png', pricePerDay: 35000, seats: 5, transmission: 'Manuelle',
    fuel: 'Diesel', hasBabySeat: true, hasGPS: true, minAge: 23,
    description: 'Le SUV robuste et accessible pour l\'aventure.'
  },
  {
    id: '21', brand: 'Hyundai', model: 'Tucson', year: 2023, category: 'SUV Compact',
    image: '/images/compact_suv.png', pricePerDay: 50000, seats: 5, transmission: 'Automatique',
    fuel: 'Essence', hasBabySeat: true, hasGPS: true, minAge: 25,
    description: 'Design futuriste et confort premium pour la famille.'
  },
  {
    id: '22', brand: 'Kia', model: 'Picanto', year: 2023, category: 'Citadine',
    image: '/images/city_car.png', pricePerDay: 20000, seats: 4, transmission: 'Manuelle',
    fuel: 'Essence', hasBabySeat: false, hasGPS: false, minAge: 18,
    description: 'La petite citadine nerveuse pour se faufiler partout.'
  },
  {
    id: '23', brand: 'Citroën', model: 'C3', year: 2023, category: 'Citadine',
    image: '/images/city_car.png', pricePerDay: 28000, seats: 5, transmission: 'Manuelle',
    fuel: 'Essence', hasBabySeat: true, hasGPS: true, minAge: 21,
    description: 'Le confort Citroën dans un format compact.'
  },
  {
    id: '24', brand: 'Volkswagen', model: 'Polo', year: 2022, category: 'Citadine',
    image: '/images/city_car.png', pricePerDay: 32000, seats: 5, transmission: 'Automatique',
    fuel: 'Essence', hasBabySeat: true, hasGPS: true, minAge: 21,
    description: 'La qualité allemande en format poche.'
  }
];

export default function Home() {
  const navigate = useNavigate();
  const [selectedVehicle, setSelectedVehicle] = useState<Vehicle | null>(null);
  const [searchQuery, setSearchQuery] = useState('');

  // Filter vehicles logic
  const filteredVehicles = GENERATED_VEHICLES.filter(vehicle =>
    vehicle.brand.toLowerCase().includes(searchQuery.toLowerCase()) ||
    vehicle.model.toLowerCase().includes(searchQuery.toLowerCase()) ||
    vehicle.category.toLowerCase().includes(searchQuery.toLowerCase())
  );

  // Authentication Check (Simple Mock)
  const isConnected = !!localStorage.getItem('token');

  return (
    <div className="font-sans antialiased text-gray-900 bg-white">
      {/* NAVBAR */}
      <nav className="fixed w-full z-50 bg-black/80 backdrop-blur-md border-b border-white/10">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-20 gap-4">
            {/* LOGO */}
            <div className="flex items-center gap-2 shrink-0">
              <div className="h-10 w-10 bg-yellow-400 rounded-xl flex items-center justify-center text-black font-bold text-xl skew-x-[-10deg] shadow-[0_0_15px_rgba(250,204,21,0.5)]">
                <Car className="w-6 h-6" />
              </div>
              <div className="flex flex-col leading-none">
                <span className="text-xl font-black text-white tracking-tighter italic uppercase">
                  ACA <span className="text-yellow-400">LOCATIONS</span>
                </span>
                <span className="text-[10px] font-bold text-gray-400 tracking-[0.2em] uppercase">De Voitures</span>
              </div>
            </div>

            {/* SEARCH BAR (CENTER) */}
            <div className="hidden md:flex flex-1 max-w-lg mx-8 relative group">
              <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                <svg className="h-5 w-5 text-gray-500 group-focus-within:text-yellow-400 transition-colors" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                  <path fillRule="evenodd" d="M8 4a4 4 0 100 8 4 4 0 000-8zM2 8a6 6 0 1110.89 3.476l4.817 4.817a1 1 0 01-1.414 1.414l-4.816-4.816A6 6 0 012 8z" clipRule="evenodd" />
                </svg>
              </div>
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => {
                  setSearchQuery(e.target.value);
                  if (e.target.value) {
                    document.getElementById('fleet')?.scrollIntoView({ behavior: 'smooth' });
                  }
                }}
                className="block w-full pl-11 pr-4 py-2.5 border border-white/10 rounded-full leading-5 bg-white/5 text-gray-300 placeholder-gray-500 focus:outline-none focus:bg-white/10 focus:ring-2 focus:ring-yellow-400 focus:border-transparent sm:text-sm transition-all"
                placeholder="Rechercher une voiture (ex: Lamborghini Urus)..."
              />
            </div>

            {/* LINKS & CTA */}
            <div className="flex items-center gap-6">
              <div className="hidden lg:flex items-baseline space-x-6">
                <a href="#fleet" className="text-gray-300 hover:text-yellow-400 text-sm font-medium transition-colors">Flotte</a>
                <a href="#about" className="text-gray-300 hover:text-yellow-400 text-sm font-medium transition-colors">A Propos</a>
              </div>
              <button
                onClick={() => isConnected ? navigate('/dashboard/client') : navigate('/login')}
                className="bg-yellow-400 hover:bg-yellow-300 text-black px-5 py-2 rounded-full font-bold text-sm transition-all transform hover:scale-105 shadow-[0_4px_14px_0_rgba(250,204,21,0.39)] flex items-center gap-2 whitespace-nowrap"
              >
                {isConnected ? 'Mon Espace' : 'Connexion'} <ArrowRight className="w-4 h-4" />
              </button>
            </div>
          </div>
        </div>
      </nav>

      {/* HERO SECTION */}
      <div className="relative h-screen flex items-center">
        {/* Background Image */}
        <div className="absolute inset-0 z-0">
          <img
            src="/images/hero.png"
            alt="Luxury Showroom"
            className="w-full h-full object-cover"
          />
          <div className="absolute inset-0 bg-gradient-to-r from-black via-black/50 to-transparent"></div>
        </div>

        <div className="relative z-10 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 w-full mt-16">
          <div className="max-w-2xl">
            <span className="inline-block py-1 px-3 rounded-full bg-yellow-400/20 border border-yellow-400/50 text-yellow-300 text-sm font-bold mb-6 tracking-wide uppercase">
              Le Futur de la Location
            </span>
            <h1 className="text-5xl md:text-7xl font-black text-white leading-tight mb-6 italic">
              DRIVE THE <br />
              <span className="text-transparent bg-clip-text bg-gradient-to-r from-yellow-300 to-yellow-500">EXTRAORDINARY</span>
            </h1>
            <p className="text-gray-300 text-lg md:text-xl mb-8 leading-relaxed max-w-lg">
              Une flotte exclusive de véhicules de prestige, disponible en un clic.
              L'esthétique ACA Locations rencontre la performance pure.
            </p>
            <div className="flex flex-col sm:flex-row gap-4">
              <button onClick={() => document.getElementById('fleet')?.scrollIntoView({ behavior: 'smooth' })} className="bg-white text-black px-8 py-4 rounded-full font-bold text-lg hover:bg-gray-100 transition-colors shadow-lg">
                Réserver maintenant
              </button>
              <button onClick={() => document.getElementById('fleet')?.scrollIntoView({ behavior: 'smooth' })} className="px-8 py-4 rounded-full font-bold text-lg text-white border border-white/30 hover:bg-white/10 transition-colors backdrop-blur-sm">
                Voir le catalogue
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* FEATURED FLEET */}
      <section id="fleet" className="py-24 bg-zinc-900">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-16">
            <h2 className="text-3xl md:text-5xl font-black text-white italic mb-4">NOTRE COLLECTION <span className="text-yellow-400 uppercase">Elite</span></h2>
            <p className="text-gray-400 max-w-2xl mx-auto text-lg">Sélectionnée pour les conducteurs exigeants. Chaque véhicule est une œuvre d'art technologique.</p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8">
            {filteredVehicles.length > 0 ? (
              filteredVehicles.map((vehicle) => (
                <div
                  key={vehicle.id}
                  className="group relative h-[400px] rounded-3xl overflow-hidden cursor-pointer"
                  onClick={() => setSelectedVehicle(vehicle)}
                >
                  <img
                    src={vehicle.image}
                    alt={vehicle.model}
                    className="absolute inset-0 w-full h-full object-cover transition-transform duration-700 group-hover:scale-110"
                  />
                  <div className="absolute inset-0 bg-gradient-to-t from-black/90 via-black/20 to-transparent opacity-80 group-hover:opacity-60 transition-opacity"></div>

                  {/* Floating Badge */}
                  <div className="absolute top-4 right-4 z-10 opacity-0 group-hover:opacity-100 transition-opacity transform translate-y-2 group-hover:translate-y-0">
                    <span className="bg-white text-black text-xs font-bold px-3 py-1.5 rounded-full shadow-lg">
                      Voir Détails ↗
                    </span>
                  </div>

                  <div className="absolute bottom-0 left-0 p-6 w-full">
                    <div className="flex justify-between items-end">
                      <div>
                        <p className="text-yellow-400 font-bold tracking-wider text-xs mb-1 uppercase">{vehicle.category}</p>
                        <h3 className="text-2xl font-black text-white italic">{vehicle.brand}</h3>
                        <p className="text-white text-lg font-bold">{vehicle.model}</p>
                      </div>
                    </div>
                    <div className="mt-4 pt-4 border-t border-white/10 flex justify-between items-center group-hover:border-yellow-400/50 transition-colors">
                      <span className="text-gray-300 text-sm">{vehicle.year}</span>
                      <span className="text-yellow-400 font-bold text-lg">{vehicle.pricePerDay.toLocaleString('fr-FR')} FCFA <span className="text-xs text-gray-400 font-normal">/jour</span></span>
                    </div>
                  </div>
                </div>
              ))
            ) : (
              <div className="col-span-full py-20 text-center">
                <p className="text-2xl text-gray-500 font-bold mb-4">Aucun véhicule trouvé pour "{searchQuery}"</p>
                <button
                  onClick={() => setSearchQuery('')}
                  className="text-yellow-400 border-b border-yellow-400 pb-1 hover:text-white hover:border-white transition-all"
                >
                  Voir toute la collection
                </button>
              </div>
            )}
          </div>
        </div>
      </section>

      {/* WHY US */}
      <section className="py-24 bg-white relative overflow-hidden">
        {/* Decorative BG */}
        <div className="absolute top-0 right-0 w-1/3 h-full bg-gray-50 skew-x-12 translate-x-32 z-0"></div>

        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-12">
            <div className="p-8 bg-zinc-900 rounded-3xl text-white transform hover:-translate-y-2 transition-transform duration-300 shadow-xl">
              <div className="w-14 h-14 bg-yellow-400 rounded-2xl flex items-center justify-center mb-6 text-black">
                <Zap className="w-8 h-8 fill-current" />
              </div>
              <h3 className="text-xl font-bold mb-4">Performance Pure</h3>
              <p className="text-gray-400 leading-relaxed">
                Des véhicules entretenus à la perfection pour garantir une expérience de conduite sans compromis.0 à 100 en moins de 3s.
              </p>
            </div>

            <div className="p-8 bg-white border border-gray-100 rounded-3xl text-gray-900 transform hover:-translate-y-2 transition-transform duration-300 shadow-xl">
              <div className="w-14 h-14 bg-black rounded-2xl flex items-center justify-center mb-6 text-yellow-400">
                <Shield className="w-8 h-8" />
              </div>
              <h3 className="text-xl font-bold mb-4">Service Premium</h3>
              <p className="text-gray-500 leading-relaxed">
                Conciergerie 24/7, livraison à domicile, et confidentialité absolue. Nous prenons soin de chaque détail pour vous.
              </p>
            </div>

            <div className="p-8 bg-gray-50 rounded-3xl text-gray-900 transform hover:-translate-y-2 transition-transform duration-300 shadow-xl">
              <div className="w-14 h-14 bg-yellow-400 rounded-2xl flex items-center justify-center mb-6 text-black">
                <Star className="w-8 h-8 fill-current" />
              </div>
              <h3 className="text-xl font-bold mb-4">Exclusivité</h3>
              <p className="text-gray-500 leading-relaxed">
                Accédez à des modèles uniques, introuvables ailleurs. Éditions limitées et configurations sur-mesure.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* ABOUT US / STORY SECTION */}
      <section id="about" className="py-24 bg-black relative overflow-hidden">
        {/* Background Elements */}
        <div className="absolute top-0 right-0 w-[500px] h-[500px] bg-yellow-400 rounded-full mix-blend-multiply filter blur-3xl opacity-10 animate-pulse"></div>
        <div className="absolute bottom-0 left-0 w-[500px] h-[500px] bg-white rounded-full mix-blend-overlay filter blur-3xl opacity-5"></div>

        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
          <div className="flex flex-col md:flex-row items-center gap-16">
            <div className="w-full md:w-1/2">
              <div className="relative">
                <div className="absolute -top-4 -left-4 w-24 h-24 border-t-4 border-l-4 border-yellow-400"></div>
                <img
                  src="/images/hero.png"
                  alt="Founders Vision"
                  className="rounded-3xl shadow-2xl grayscale hover:grayscale-0 transition-all duration-700 w-full object-cover h-[500px]"
                />
                <div className="absolute -bottom-4 -right-4 w-24 h-24 border-b-4 border-r-4 border-yellow-400"></div>
              </div>
            </div>

            <div className="w-full md:w-1/2 text-white">
              <h2 className="text-4xl font-black italic mb-2">L'HISTOIRE <span className="text-yellow-400">ACA</span></h2>
              <p className="text-yellow-400 font-bold tracking-widest uppercase text-xs mb-8">Fondé en Décembre 2025</p>

              <div className="space-y-6 text-gray-300 text-lg leading-relaxed">
                <p>
                  <strong className="text-white">ACA</strong>, c'est avant tout l'histoire d'une amitié et d'une vision commune née sur les bancs de l'Université Assane Seck de Ziguinchor (UASZ).
                </p>
                <p>
                  Trois jeunes passionnés d'automobile et de technologie — <span className="text-white font-bold border-b border-yellow-400">Abdou Lakhat Bah</span>, <span className="text-white font-bold border-b border-yellow-400">Coumba Dieng</span> et <span className="text-white font-bold border-b border-yellow-400">Alassane Diatta</span> — ont décidé de fusionner leurs compétences pour révolutionner le marché.
                </p>
                <p>
                  Notre mission ? <span className="italic text-white">Digitaliser l'élégance.</span> Nous avons transformé une idée estudiantine en une plateforme de gestion de flotte ultra-moderne, offrant une expérience fluide, du clic au démarrage du moteur.
                </p>
              </div>

              <div className="mt-10 flex gap-4">
                <div className="px-6 py-4 bg-white/5 border border-white/10 rounded-2xl">
                  <p className="text-3xl font-black text-white">100%</p>
                  <p className="text-xs text-gray-400 uppercase tracking-wider">Passion</p>
                </div>
                <div className="px-6 py-4 bg-white/5 border border-white/10 rounded-2xl">
                  <p className="text-3xl font-black text-white">UASZ</p>
                  <p className="text-xs text-gray-400 uppercase tracking-wider">Origine</p>
                </div>
                <div className="px-6 py-4 bg-white/5 border border-white/10 rounded-2xl">
                  <p className="text-3xl font-black text-white">2025</p>
                  <p className="text-xs text-gray-400 uppercase tracking-wider">Création</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* FOOTER */}
      <footer className="bg-black text-white border-t border-white/10 pt-20 pb-10" id="contact">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-12 mb-16">
            <div>
              <div className="flex items-center gap-2 mb-6">
                <div className="h-8 w-8 bg-yellow-400 rounded-lg flex items-center justify-center text-black font-bold text-lg skew-x-[-10deg]">
                  <Car className="h-5 w-5" />
                </div>
                <span className="text-xl font-black text-white tracking-tighter italic">
                  ACA <span className="text-yellow-400">LOCATIONS</span>
                </span>
              </div>
              <p className="text-gray-400 max-w-sm">
                L'excellence automobile à portée de main. Service client disponible 24/7 pour répondre à toutes vos exigences.
              </p>
            </div>
            <div className="flex flex-col gap-4 md:items-end">
              <h4 className="text-yellow-400 font-bold uppercase tracking-widest text-sm mb-2">Service Client</h4>
              <a href="tel:+221777777777" className="text-2xl font-black text-white hover:text-yellow-400 transition-colors flex items-center gap-2">
                +221 77 777 77 77
              </a>
              <a href="tel:+221788008080" className="text-xl font-bold text-gray-300 hover:text-white transition-colors">
                +221 78 800 80 80
              </a>
              <a href="tel:+221333003333" className="text-xl font-bold text-gray-300 hover:text-white transition-colors">
                +221 33 300 33 33
              </a>
            </div>
          </div>

          <div className="text-center text-gray-600 text-sm pt-8 border-t border-white/5">
            © 2024 ACA Locations De Voitures. All rights reserved. Designed for Excellence.
          </div>
        </div>
      </footer>

      {/* MODAL */}
      <VehicleDetailModal
        vehicle={selectedVehicle!}
        isOpen={!!selectedVehicle}
        onClose={() => setSelectedVehicle(null)}
      />
    </div>
  );
}
