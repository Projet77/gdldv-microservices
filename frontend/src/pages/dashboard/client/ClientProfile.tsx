import React from 'react';

const ClientProfile: React.FC = () => {
    return (
        <div className="py-6">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 md:px-8">
                <h1 className="text-2xl font-semibold text-gray-900">Mon Profil</h1>
            </div>
            <div className="max-w-7xl mx-auto px-4 sm:px-6 md:px-8">
                <div className="py-4">
                    <div className="bg-white shadow sm:rounded-lg">
                        <div className="px-4 py-5 sm:p-6">
                            <h3 className="text-lg leading-6 font-medium text-gray-900">Informations personnelles</h3>
                            <div className="mt-2 max-w-xl text-sm text-gray-500">
                                <p>Gérez vos informations de compte et vos préférences.</p>
                            </div>
                            <div className="mt-5">
                                <button
                                    type="button"
                                    className="inline-flex items-center justify-center px-4 py-2 border border-transparent font-medium rounded-md text-white bg-emerald-600 hover:bg-emerald-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-emerald-500 sm:text-sm"
                                >
                                    Modifier le profil
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ClientProfile;
