import React from 'react';

const AdminUsers: React.FC = () => {
    return (
        <div className="py-6">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 md:px-8">
                <h1 className="text-2xl font-semibold text-gray-900">Gestion des Utilisateurs</h1>
            </div>
            <div className="max-w-7xl mx-auto px-4 sm:px-6 md:px-8">
                <div className="py-4">
                    <div className="bg-white shadow overflow-hidden sm:rounded-md">
                        <div className="px-4 py-5 sm:px-6 flex justify-between items-center">
                            <h3 className="text-lg leading-6 font-medium text-gray-900">Liste des utilisateurs</h3>
                            <button className="bg-emerald-600 text-white px-4 py-2 rounded-md text-sm font-medium hover:bg-emerald-700">
                                Ajouter un utilisateur
                            </button>
                        </div>
                        <div className="border-t border-gray-200">
                            {/* Table placeholder */}
                            <div className="text-center py-10 text-gray-500">
                                Chargement des utilisateurs...
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default AdminUsers;
