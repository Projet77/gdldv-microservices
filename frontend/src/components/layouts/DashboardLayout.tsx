import React, { useState } from 'react';
import { Outlet, NavLink, useNavigate, useLocation } from 'react-router-dom';
import {
    LayoutDashboard,
    Car,
    CalendarDays,
    History,
    Users,
    Settings,
    LogOut,
    Menu,
    X,
    Bell,
    Search,
    ChevronDown,
    ShieldCheck,
    FileText,
    AlertTriangle,
    Activity
} from 'lucide-react';
import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

// Utility for merging tailwind classes
function cn(...inputs: ClassValue[]) {
    return twMerge(clsx(inputs));
}

type UserRole = 'CLIENT' | 'ADMIN' | 'AGENT' | 'MANAGER' | 'SUPER_ADMIN';

interface NavItem {
    label: string;
    href: string;
    icon: React.ElementType;
}

const NAV_CONFIG: Record<UserRole, NavItem[]> = {
    CLIENT: [
        { label: 'Tableau de bord', href: '/dashboard/client', icon: LayoutDashboard },
        { label: 'Mes Réservations', href: '/dashboard/client/reservations', icon: CalendarDays },
        { label: 'Historique', href: '/dashboard/client/history', icon: History },
        { label: 'Favoris', href: '/dashboard/client/favorites', icon: Car },
        { label: 'Mon Profil', href: '/dashboard/client/profile', icon: Settings },
    ],
    ADMIN: [
        { label: 'Aperçu', href: '/dashboard/admin', icon: LayoutDashboard },
        { label: 'Flotte', href: '/dashboard/admin/vehicles', icon: Car },
        { label: 'Clients', href: '/dashboard/admin/users', icon: Users },
        { label: 'Maintenance', href: '/dashboard/admin/maintenance', icon: AlertTriangle },
        { label: 'Rapports', href: '/dashboard/admin/reports', icon: FileText },
    ],
    AGENT: [
        { label: 'Accueil Agent', href: '/dashboard/agent', icon: LayoutDashboard },
        { label: 'Départs / Retours', href: '/dashboard/agent/operations', icon: Car },
        { label: 'Réservations', href: '/dashboard/agent/reservations', icon: CalendarDays },
        { label: 'Clients', href: '/dashboard/agent/clients', icon: Users },
    ],
    MANAGER: [
        { label: 'Tableau de bord', href: '/dashboard/manager', icon: LayoutDashboard },
        { label: 'Performance', href: '/dashboard/manager/performance', icon: Activity },
        { label: 'Équipe', href: '/dashboard/manager/team', icon: Users },
        { label: 'Incidents', href: '/dashboard/manager/incidents', icon: AlertTriangle },
        { label: 'Finances', href: '/dashboard/manager/finance', icon: FileText },
    ],
    SUPER_ADMIN: [
        { label: 'Vue Système', href: '/dashboard/super-admin', icon: Activity },
        { label: 'Utilisateurs', href: '/dashboard/super-admin/users', icon: Users },
        { label: 'Sécurité', href: '/dashboard/super-admin/security', icon: ShieldCheck },
        { label: 'Configuration', href: '/dashboard/super-admin/config', icon: Settings },
    ]
};

interface DashboardLayoutProps {
    role?: UserRole;
    userName?: string;
    userImage?: string;
}

export default function DashboardLayout({ role = "CLIENT", userName = "Utilisateur", userImage }: DashboardLayoutProps) {
    const [isSidebarOpen, setIsSidebarOpen] = useState(false);
    const location = useLocation();
    const navigate = useNavigate();

    const navItems = NAV_CONFIG[role];

    const handleLogout = () => {
        // TODO: Implement actual logout logic (clear tokens, etc.)
        console.log("Logging out...");
        navigate('/login');
    };

    return (
        <div className="min-h-screen bg-gray-50 flex font-sans">
            {/* Mobile Sidebar Overlay */}
            {isSidebarOpen && (
                <div
                    className="fixed inset-0 bg-black/50 z-40 lg:hidden"
                    onClick={() => setIsSidebarOpen(false)}
                />
            )}

            {/* Sidebar - NANO BANANA STYLE */}
            <aside
                className={cn(
                    "fixed lg:static inset-y-0 left-0 z-50 w-64 bg-black text-white border-r border-white/10 shadow-xl transform transition-transform duration-200 ease-in-out lg:transform-none",
                    isSidebarOpen ? "translate-x-0" : "-translate-x-full"
                )}
            >
                <div className="h-full flex flex-col">
                    {/* Logo Area */}
                    <div className="h-20 flex items-center px-6 border-b border-white/10">
                        <div
                            className="flex items-center gap-2 cursor-pointer group"
                            onClick={() => navigate('/')}
                        >
                            <div className="h-8 w-8 bg-yellow-400 rounded-lg flex items-center justify-center text-black font-bold text-lg skew-x-[-10deg] group-hover:scale-105 transition-transform duration-300">
                                <Car className="h-5 w-5" />
                            </div>
                            <div className="flex flex-col leading-none">
                                <span className="text-lg font-black text-white tracking-tighter italic uppercase group-hover:text-yellow-400 transition-colors duration-300">
                                    ACA <span className="text-yellow-400 group-hover:text-white transition-colors duration-300">LOCATIONS</span>
                                </span>
                                <span className="text-[9px] font-bold text-gray-500 tracking-widest uppercase">De Voitures</span>
                            </div>
                        </div>
                    </div>

                    {/* Navigation */}
                    <nav className="flex-1 overflow-y-auto py-6 px-3 space-y-1">
                        {navItems.map((item) => {
                            const isActive = location.pathname === item.href || location.pathname.startsWith(`${item.href}/`);
                            return (
                                <NavLink
                                    key={item.href}
                                    to={item.href}
                                    onClick={() => setIsSidebarOpen(false)}
                                    className={({ isActive }) => cn(
                                        "flex items-center gap-3 px-3 py-3 rounded-xl text-sm font-bold transition-all duration-200",
                                        isActive
                                            ? "bg-yellow-400 text-black shadow-[0_0_15px_rgba(250,204,21,0.4)]"
                                            : "text-gray-400 hover:bg-white/10 hover:text-white"
                                    )}
                                >
                                    <item.icon className={cn("h-5 w-5", isActive ? "text-black" : "text-gray-400 Group-hover:text-white")} />
                                    {item.label}
                                </NavLink>
                            );
                        })}
                    </nav>

                    {/* Bottom Actions */}
                    <div className="p-4 border-t border-white/10">
                        <button
                            onClick={handleLogout}
                            className="flex items-center gap-3 w-full px-3 py-2.5 rounded-xl text-sm font-bold text-red-400 hover:bg-red-500/10 hover:text-red-300 transition-colors"
                        >
                            <LogOut className="h-5 w-5" />
                            Déconnexion
                        </button>
                    </div>
                </div>
            </aside>

            {/* Main Content */}
            <div className="flex-1 flex flex-col min-w-0 overflow-hidden bg-gray-50">
                {/* Header */}
                <header className="bg-white border-b border-gray-200 h-20 flex items-center justify-between px-4 sm:px-6 lg:px-8 shadow-sm relative z-20">
                    <button
                        onClick={() => setIsSidebarOpen(true)}
                        className="lg:hidden p-2 rounded-md text-gray-400 hover:text-gray-500 hover:bg-gray-100 focus:outline-none"
                    >
                        <Menu className="h-6 w-6" />
                    </button>

                    <div className="flex items-center gap-4 ml-auto">
                        {/* Search */}
                        <div className="relative hidden md:block group">
                            <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400 group-focus-within:text-yellow-500 transition-colors" />
                            <input
                                type="text"
                                placeholder="Rechercher..."
                                className="pl-9 pr-4 py-2.5 border border-gray-200 rounded-full text-sm focus:outline-none focus:ring-2 focus:ring-yellow-400 focus:border-transparent w-64 transition-all bg-gray-50 focus:bg-white"
                            />
                        </div>

                        {/* Notifications */}
                        <button className="p-2 relative text-gray-400 hover:text-black transition-colors">
                            <Bell className="h-6 w-6" />
                            <span className="absolute top-2 right-2 h-2.5 w-2.5 bg-yellow-400 rounded-full border-2 border-white"></span>
                        </button>

                        {/* User Profile */}
                        <div className="flex items-center gap-3 pl-4 border-l border-gray-200">
                            <div className="text-right hidden sm:block">
                                <p className="text-sm font-bold text-gray-900">{userName}</p>
                                <p className="text-xs text-gray-500 capitalize">{role.toLowerCase().replace('_', ' ')}</p>
                            </div>
                            <div className="h-10 w-10 rounded-full bg-black flex items-center justify-center text-yellow-400 font-bold border-2 border-yellow-400 shadow-md">
                                {userImage ? (
                                    <img src={userImage} alt={userName} className="h-full w-full rounded-full object-cover" />
                                ) : (
                                    userName.charAt(0)
                                )}
                            </div>
                        </div>
                    </div>
                </header>

                {/* Page Content */}
                <main className="flex-1 overflow-y-auto p-4 sm:p-6 lg:p-8 custom-scrollbar">
                    <Outlet />
                </main>
            </div>
        </div>
    );
}
