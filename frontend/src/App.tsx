import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import DashboardLayout from './components/layouts/DashboardLayout';
import ClientOverview from './pages/dashboard/client/ClientOverview';
import ClientReservations from './pages/dashboard/client/ClientReservations';
import ClientHistory from './pages/dashboard/client/ClientHistory';
import ClientFavorites from './pages/dashboard/client/ClientFavorites';
import ClientProfile from './pages/dashboard/client/ClientProfile';
import AgentOverview from './pages/dashboard/agent/AgentOverview';
import ManagerOverview from './pages/dashboard/manager/ManagerOverview';
import ManagerReservations from './pages/dashboard/manager/ManagerReservations';
import ManagerTeam from './pages/dashboard/manager/ManagerTeam';
import ManagerReports from './pages/dashboard/manager/ManagerReports';
import AdminOverview from './pages/dashboard/admin/AdminOverview';
import AdminUsers from './pages/dashboard/admin/AdminUsers';
import AdminVehicles from './pages/dashboard/admin/AdminVehicles';
import AdminReports from './pages/dashboard/admin/AdminReports';
import AdminMaintenance from './pages/dashboard/admin/AdminMaintenance';
import AdminReservations from './pages/dashboard/admin/AdminReservations';
import SuperAdminOverview from './pages/dashboard/superadmin/SuperAdminOverview';
import SuperAdminUsers from './pages/dashboard/superadmin/SuperAdminUsers';
import SuperAdminSecurity from './pages/dashboard/superadmin/SuperAdminSecurity';
import SuperAdminConfig from './pages/dashboard/superadmin/SuperAdminConfig';
import './index.css';

const App: React.FC = () => {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public routes */}
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        {/* Client Dashboard routes */}
        <Route path="/dashboard/client" element={<DashboardLayout />}>
          <Route index element={<Navigate to="/dashboard/client/overview" replace />} />
          <Route path="overview" element={<ClientOverview />} />
          <Route path="reservations" element={<ClientReservations />} />
          <Route path="reservations/:id" element={<ClientReservations />} />
          <Route path="history" element={<ClientHistory />} />
          <Route path="favorites" element={<ClientFavorites />} />
          <Route path="profile" element={<ClientProfile />} />
        </Route>

        {/* Agent Dashboard routes */}
        <Route path="/dashboard/agent" element={<DashboardLayout />}>
          <Route index element={<Navigate to="/dashboard/agent/overview" replace />} />
          <Route path="overview" element={<AgentOverview />} />
        </Route>

        {/* Manager Dashboard routes */}
        <Route path="/dashboard/manager" element={<DashboardLayout />}>
          <Route index element={<Navigate to="/dashboard/manager/overview" replace />} />
          <Route path="overview" element={<ManagerOverview />} />
          <Route path="reservations" element={<ManagerReservations />} />
          <Route path="team" element={<ManagerTeam />} />
          <Route path="reports" element={<ManagerReports />} />
          <Route path="profile" element={<ClientProfile />} />
        </Route>

        {/* Admin Dashboard routes */}
        <Route path="/dashboard/admin" element={<DashboardLayout />}>
          <Route index element={<Navigate to="/dashboard/admin/overview" replace />} />
          <Route path="overview" element={<AdminOverview />} />
          <Route path="users" element={<AdminUsers />} />
          <Route path="vehicles" element={<AdminVehicles />} />
          <Route path="reports" element={<AdminReports />} />
          <Route path="maintenance" element={<AdminMaintenance />} />
          <Route path="reservations" element={<AdminReservations />} />
          <Route path="profile" element={<ClientProfile />} />
        </Route>

        {/* Super Admin Dashboard routes */}
        <Route path="/dashboard/superadmin" element={<DashboardLayout />}>
          <Route index element={<Navigate to="/dashboard/superadmin/overview" replace />} />
          <Route path="overview" element={<SuperAdminOverview />} />
          <Route path="users" element={<SuperAdminUsers />} />
          <Route path="security" element={<SuperAdminSecurity />} />
          <Route path="config" element={<SuperAdminConfig />} />
          <Route path="profile" element={<ClientProfile />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
};

export default App;
