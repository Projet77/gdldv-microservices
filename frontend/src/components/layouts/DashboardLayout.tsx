import React, { useState, useEffect } from 'react';
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
import { authService } from '../../services/authService';

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

import { useTranslation } from 'react-i18next';

// ... other imports

// Note: We keep NAV_CONFIG with keys, but we don't need TRANSLATIONS here anymore.

const NAV_CONFIG: Record<UserRole, NavItem[]> = {
    // ... (NAV_CONFIG remains same, mapping roles to arrays of objects with 'label' being the translation key)
    CLIENT: [
        { label: 'dashboard', href: '/dashboard/client', icon: LayoutDashboard },
        { label: 'my_reservations', href: '/dashboard/client/reservations', icon: CalendarDays },
        { label: 'history', href: '/dashboard/client/history', icon: History },
        { label: 'favorites', href: '/dashboard/client/favorites', icon: Car },
        { label: 'profile', href: '/dashboard/client/profile', icon: Settings },
    ],
    ADMIN: [
        { label: 'overview', href: '/dashboard/admin', icon: LayoutDashboard },
        { label: 'fleet', href: '/dashboard/admin/vehicles', icon: Car },
        { label: 'clients', href: '/dashboard/admin/users', icon: Users },
        { label: 'my_reservations', href: '/dashboard/admin/reservations', icon: CalendarDays },
        { label: 'maintenance', href: '/dashboard/admin/maintenance', icon: AlertTriangle },
        { label: 'reports', href: '/dashboard/admin/reports', icon: FileText },
    ],
    AGENT: [
        { label: 'agent_home', href: '/dashboard/agent', icon: LayoutDashboard },
        { label: 'operations', href: '/dashboard/agent/operations', icon: Car },
        { label: 'my_reservations', href: '/dashboard/agent/reservations', icon: CalendarDays },
        { label: 'clients', href: '/dashboard/agent/clients', icon: Users },
    ],
    MANAGER: [
        { label: 'dashboard', href: '/dashboard/manager', icon: LayoutDashboard },
        { label: 'my_reservations', href: '/dashboard/manager/reservations', icon: CalendarDays },
        { label: 'team', href: '/dashboard/manager/team', icon: Users },
        { label: 'reports', href: '/dashboard/manager/reports', icon: FileText },
    ],
    SUPER_ADMIN: [
        { label: 'system_view', href: '/dashboard/superadmin', icon: Activity },
        { label: 'fleet', href: '/dashboard/admin/vehicles', icon: Car },
        { label: 'my_reservations', href: '/dashboard/admin/reservations', icon: CalendarDays },
        { label: 'users', href: '/dashboard/superadmin/users', icon: Users },
        { label: 'security', href: '/dashboard/superadmin/security', icon: ShieldCheck },
        { label: 'configuration', href: '/dashboard/superadmin/config', icon: Settings },
    ]
};

interface DashboardLayoutProps {
    role?: UserRole;
    userName?: string;
    userImage?: string;
}

export default function DashboardLayout({ role: propRole, userName: propUserName, userImage }: DashboardLayoutProps) {
    const { t } = useTranslation();
    const [isSidebarOpen, setIsSidebarOpen] = useState(false);
    const [isProfileOpen, setIsProfileOpen] = useState(false);
    const [currentUser, setCurrentUser] = useState<any>(null);
    const location = useLocation();
    const navigate = useNavigate();

    useEffect(() => {
        const loadUser = () => {
            const user = authService.getCurrentUser();
            if (user) {
                setCurrentUser(user);
            } else {
                navigate('/login');
            }
        };

        loadUser();

        // Listen for profile updates
        window.addEventListener('user-updated', loadUser);

        return () => {
            window.removeEventListener('user-updated', loadUser);
        };
    }, [navigate]);

    const role = propRole || currentUser?.role || "CLIENT";
    const userName = propUserName || currentUser?.name || currentUser?.email || "Utilisateur";

    // Handle case where role might have ROLE_ prefix or be invalid
    const normalizedRole = role.startsWith('ROLE_') ? role.replace('ROLE_', '') as UserRole : role;
    const navItems = NAV_CONFIG[normalizedRole] || NAV_CONFIG['CLIENT'];

    const handleLogout = () => {
        authService.logout();
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
                                    {t(item.label)}
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
                            {t('logout')}
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
                        <span className="text-gray-700 mr-4 font-medium block">
                            {t('hello')}, {currentUser ? currentUser.first_name || currentUser.firstName || currentUser.name : 'Utilisateur'}
                        </span>
                        {/* Search */}
                        <div className="relative hidden md:block group">
                            <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400 group-focus-within:text-yellow-500 transition-colors" />
                            <input
                                type="text"
                                placeholder={t('search')}
                                className="pl-9 pr-4 py-2.5 border border-gray-200 rounded-full text-sm focus:outline-none focus:ring-2 focus:ring-yellow-400 focus:border-transparent w-64 transition-all bg-gray-50 focus:bg-white"
                            />
                        </div>

                        {/* Notifications */}
                        <button className="p-2 relative text-gray-400 hover:text-black transition-colors">
                            <Bell className="h-6 w-6" />
                            <span className="absolute top-2 right-2 h-2.5 w-2.5 bg-yellow-400 rounded-full border-2 border-white"></span>
                        </button>

                        {/* User Profile */}
                        <div className="relative pl-4 border-l border-gray-200">
                            <button
                                onClick={() => setIsProfileOpen(!isProfileOpen)}
                                className="flex items-center gap-3 hover:bg-gray-50 rounded-full p-1 transition-colors focus:outline-none"
                            >
                                <div className="text-right block">
                                    <p className="text-sm font-bold text-gray-900">{userName}</p>
                                    <p className="text-xs text-gray-500 capitalize">{role.toLowerCase().replace('_', ' ')}</p>
                                </div>
                                <div className="h-10 w-10 rounded-full bg-black flex items-center justify-center text-yellow-400 font-bold border-2 border-yellow-400 shadow-md overflow-hidden">
                                    {userImage || currentUser?.profileImage ? (
                                        <img src={userImage || currentUser?.profileImage} alt={userName} className="h-full w-full object-cover" />
                                    ) : (
                                        userName.charAt(0)
                                    )}
                                </div>
                                <ChevronDown className={`h-4 w-4 text-gray-400 transition-transform duration-200 ${isProfileOpen ? 'rotate-180' : ''}`} />
                            </button>

                            {/* Dropdown Menu */}
                            {isProfileOpen && (
                                <>
                                    <div
                                        className="fixed inset-0 z-30 opacity-0"
                                        onClick={() => setIsProfileOpen(false)}
                                    />
                                    <div className="absolute right-0 top-full mt-2 w-56 bg-white rounded-xl shadow-lg border border-gray-100 py-2 z-40 transform origin-top-right transition-all">
                                        <div className="px-4 py-3 border-b border-gray-100">
                                            <p className="text-xs font-semibold text-gray-400 uppercase tracking-wider">{t('account')}</p>
                                            <p className="text-sm font-medium text-gray-900 truncate">{userName}</p>
                                            <p className="text-xs text-gray-500 truncate">{currentUser?.email}</p>
                                        </div>

                                        <div className="p-2">
                                            <button
                                                onClick={() => {
                                                    setIsProfileOpen(false);
                                                    navigate(`/dashboard/${normalizedRole.toLowerCase()}/profile`);
                                                }}
                                                className="flex items-center gap-3 w-full px-3 py-2 rounded-lg text-sm text-gray-600 hover:bg-yellow-50 hover:text-yellow-600 transition-colors"
                                            >
                                                <Settings className="h-4 w-4" />
                                                {t('view_profile')}
                                            </button>

                                            <button
                                                onClick={() => {
                                                    setIsProfileOpen(false);
                                                    handleLogout();
                                                }}
                                                className="flex items-center gap-3 w-full px-3 py-2 rounded-lg text-sm text-red-500 hover:bg-red-50 transition-colors mt-1"
                                            >
                                                <LogOut className="h-4 w-4" />
                                                {t('logout')}
                                            </button>
                                        </div>
                                    </div>
                                </>
                            )}
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
