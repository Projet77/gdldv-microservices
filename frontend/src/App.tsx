import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import ClientDashboard from './pages/ClientDashboard';
import './index.css';

const App: React.FC = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/dashboard/client" replace />} />
        <Route path="/dashboard/client" element={<ClientDashboard />} />
        {/* TODO: Ajouter les autres dashboards */}
        {/* <Route path="/dashboard/agent" element={<AgentDashboard />} /> */}
        {/* <Route path="/dashboard/manager" element={<ManagerDashboard />} /> */}
        {/* <Route path="/dashboard/super-admin" element={<SuperAdminDashboard />} /> */}
      </Routes>
    </BrowserRouter>
  );
};

export default App;
