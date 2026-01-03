import React from 'react';

const ManagerOverview: React.FC = () => {
  return (
    <div className="py-6">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 md:px-8">
        <h1 className="text-2xl font-semibold text-gray-900">Espace Manager</h1>
      </div>
      <div className="max-w-7xl mx-auto px-4 sm:px-6 md:px-8">
        <div className="py-4">
          <div className="bg-white shadow sm:rounded-lg p-6">
            <p className="text-gray-500">Bienvenue dans l'espace manager.</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ManagerOverview;
