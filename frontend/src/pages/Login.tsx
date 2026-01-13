import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { ArrowLeft, ArrowRight, Car, Lock, Mail } from 'lucide-react';
import { authService } from '../services/authService';

export default function Login() {
    const navigate = useNavigate();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            const response = await authService.login({ email, password });

            // Redirect based on role
            // response.user contains the user object with roles as objects [{id, name: 'ROLE_SUPER_ADMIN'}]
            // response IS the user object (flat structure from backend)
            const user = response;
            const userRoleName = user.roles && user.roles.length > 0 ? user.roles[0] : 'ROLE_CLIENT';

            const role = userRoleName.replace('ROLE_', '');

            const dashboardRoutes: Record<string, string> = {
                'SUPER_ADMIN': '/dashboard/superadmin',
                'ADMIN': '/dashboard/admin',
                'MANAGER': '/dashboard/manager',
                'AGENT': '/dashboard/agent',
                'CLIENT': '/dashboard/client'
            };

            const targetRoute = dashboardRoutes[role] || '/dashboard/client';
            navigate(targetRoute);
        } catch (err: any) {
            console.error('Login error:', err);
            setError('Email ou mot de passe incorrect');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="h-screen flex bg-white font-sans overflow-hidden">
            {/* LEFT SIDE - FORM */}
            <div className="w-full lg:w-1/2 flex flex-col p-8 lg:p-20 justify-center relative z-10 bg-white">
                <button
                    onClick={() => navigate('/')}
                    className="absolute top-8 left-8 text-gray-400 hover:text-black flex items-center gap-2 text-sm font-medium transition"
                >
                    <ArrowLeft className="w-4 h-4" /> Retour accueil
                </button>

                <div className="max-w-md w-full mx-auto">
                    <div className="flex items-center gap-2 mb-10">
                        <div className="h-10 w-10 bg-yellow-400 rounded-xl flex items-center justify-center text-black font-bold text-xl skew-x-[-10deg]">
                            <Car className="w-6 h-6" />
                        </div>
                        <div className="flex flex-col leading-none">
                            <span className="text-2xl font-black text-black tracking-tighter italic uppercase">
                                ACA <span className="text-yellow-400">LOCATIONS</span>
                            </span>
                            <span className="text-xs font-bold text-gray-500 tracking-widest uppercase">De Voitures</span>
                        </div>
                    </div>

                    <h1 className="text-4xl font-black text-gray-900 mb-3 tracking-tight">Bon retour.</h1>
                    <p className="text-gray-500 mb-10 text-lg">Connectez-vous pour gérer vos locations.</p>

                    <form onSubmit={handleLogin} className="space-y-6">
                        {error && (
                            <div className="bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-xl text-sm font-medium">
                                {error}
                            </div>
                        )}

                        <div>
                            <label className="block text-sm font-bold text-gray-700 mb-2">Email</label>
                            <div className="relative">
                                <Mail className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 w-5 h-5" />
                                <input
                                    type="email"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    required
                                    className="w-full pl-12 pr-4 py-3.5 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-yellow-400 focus:bg-white transition-all font-medium"
                                    placeholder="exemple@email.com"
                                />
                            </div>
                        </div>

                        <div>
                            <div className="flex justify-between items-center mb-2">
                                <label className="block text-sm font-bold text-gray-700">Mot de passe</label>
                                <a href="#" className="text-sm font-bold text-yellow-500 hover:text-yellow-600">Oublié ?</a>
                            </div>
                            <div className="relative">
                                <Lock className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 w-5 h-5" />
                                <input
                                    type="password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    required
                                    className="w-full pl-12 pr-4 py-3.5 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-yellow-400 focus:bg-white transition-all font-medium"
                                    placeholder="••••••••"
                                />
                            </div>
                        </div>

                        <button
                            type="submit"
                            disabled={loading}
                            className="w-full bg-black text-white font-bold py-4 rounded-xl hover:bg-zinc-800 transition-all shadow-lg hover:shadow-xl transform hover:-translate-y-1 flex items-center justify-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
                        >
                            {loading ? 'Connexion en cours...' : 'Se connecter'} {!loading && <ArrowRight className="w-5 h-5" />}
                        </button>
                    </form>

                    <p className="text-center mt-8 text-gray-500 font-medium">
                        Pas encore membre ? <Link to="/register" className="text-black font-bold hover:text-yellow-500 transition">Créer un compte</Link>
                    </p>
                </div>
            </div>

            {/* RIGHT SIDE - IMAGE */}
            <div className="hidden lg:block w-1/2 relative bg-zinc-900">
                <div className="absolute inset-0 bg-[url('/images/suv_car.png')] bg-cover bg-center opacity-60 mix-blend-overlay"></div>
                <div className="absolute inset-0 bg-gradient-to-bl from-black/80 via-black/40 to-transparent"></div>

                <div className="absolute bottom-20 left-12 right-12 text-white">
                    <div className="w-16 h-1 bg-yellow-400 mb-8"></div>
                    <h2 className="text-5xl font-black italic mb-6 leading-tight">
                        "L'excellence n'attend <br />
                        <span className="text-yellow-400">pas</span>."
                    </h2>
                    <p className="text-gray-300 font-medium text-lg max-w-md">
                        Retrouvez votre historique, vos préférences et votre statut membre élite.
                    </p>
                </div>
            </div>
        </div>
    );
}
