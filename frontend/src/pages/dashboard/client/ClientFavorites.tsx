import React from 'react';

const ClientFavorites: React.FC = () => {
    return (
        <div className="py-6">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 md:px-8">
                <h1 className="text-2xl font-semibold text-gray-900">Mes Favoris</h1>
            </div>
            <div className="max-w-7xl mx-auto px-4 sm:px-6 md:px-8">
                <div className="py-4">
                    <div className="text-center text-gray-500 py-12">
                        <p>Vous n'avez pas encore ajouté de véhicules à vos favoris.</p>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ClientFavorites;
