import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, ArrowRight, Car, Lock, Mail, User } from 'lucide-react';
import { authService } from '../services/authService';

export default function Register() {
    const navigate = useNavigate();
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleRegister = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');

        // Validation
        if (password !== confirmPassword) {
            setError('Les mots de passe ne correspondent pas');
            return;
        }

        if (password.length < 6) {
            setError('Le mot de passe doit contenir au moins 6 caractères');
            return;
        }

        // Parse name into firstName and lastName
        const nameParts = name.trim().split(' ');
        const firstName = nameParts[0] || '';
        const lastName = nameParts.slice(1).join(' ') || nameParts[0];

        setLoading(true);
        try {
            await authService.register({
                firstName,
                lastName,
                email,
                password
            });

            // Navigate to dashboard after successful registration
            navigate('/dashboard/client');
        } catch (err: any) {
            console.error('Registration error:', err);
            setError(err.response?.data?.message || 'Erreur lors de l\'inscription');
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

                    <h1 className="text-4xl font-black text-gray-900 mb-3 tracking-tight">Créer un compte.</h1>
                    <p className="text-gray-500 mb-10 text-lg">Rejoignez l'élite de la location automobile.</p>

                    {error && (
                        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl mb-6">
                            {error}
                        </div>
                    )}

                    <form onSubmit={handleRegister} className="space-y-6">

                        <div>
                            <label className="block text-sm font-bold text-gray-700 mb-2">Nom Complet</label>
                            <div className="relative">
                                <User className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 w-5 h-5" />
                                <input
                                    type="text"
                                    value={name}
                                    onChange={(e) => setName(e.target.value)}
                                    className="w-full pl-12 pr-4 py-3.5 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-yellow-400 focus:bg-white transition-all font-medium"
                                    placeholder="Jean Dupont"
                                />
                            </div>
                        </div>

                        <div>
                            <label className="block text-sm font-bold text-gray-700 mb-2">Email</label>
                            <div className="relative">
                                <Mail className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 w-5 h-5" />
                                <input
                                    type="email"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    className="w-full pl-12 pr-4 py-3.5 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-yellow-400 focus:bg-white transition-all font-medium"
                                    placeholder="exemple@email.com"
                                />
                            </div>
                        </div>

                        <div>
                            <label className="block text-sm font-bold text-gray-700 mb-2">Mot de passe</label>
                            <div className="relative">
                                <Lock className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 w-5 h-5" />
                                <input
                                    type="password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    className="w-full pl-12 pr-4 py-3.5 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-yellow-400 focus:bg-white transition-all font-medium"
                                    placeholder="••••••••"
                                />
                            </div>
                        </div>

                        <div>
                            <label className="block text-sm font-bold text-gray-700 mb-2">Confirmer le mot de passe</label>
                            <div className="relative">
                                <Lock className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 w-5 h-5" />
                                <input
                                    type="password"
                                    value={confirmPassword}
                                    onChange={(e) => setConfirmPassword(e.target.value)}
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
                            {loading ? 'Inscription en cours...' : "S'inscrire"}
                            {!loading && <ArrowRight className="w-5 h-5" />}
                        </button>
                    </form>

                    <p className="text-center mt-8 text-gray-500 font-medium">
                        Déjà membre ? <button onClick={() => navigate('/login')} className="text-black font-bold hover:text-yellow-500 transition">Se connecter</button>
                    </p>
                </div>
            </div>

            {/* RIGHT SIDE - IMAGE */}
            <div className="hidden lg:block w-1/2 relative bg-zinc-900">
                <div className="absolute inset-0 bg-[url('/images/sport_car.png')] bg-cover bg-center opacity-60 mix-blend-overlay"></div>
                <div className="absolute inset-0 bg-gradient-to-bl from-black/80 via-black/40 to-transparent"></div>

                <div className="absolute bottom-20 left-12 right-12 text-white">
                    <div className="w-16 h-1 bg-yellow-400 mb-8"></div>
                    <h2 className="text-5xl font-black italic mb-6 leading-tight">
                        "Rejoignez le club <br />
                        <span className="text-yellow-400">Privé</span>."
                    </h2>
                    <p className="text-gray-300 font-medium text-lg max-w-md">
                        Créez votre compte en quelques secondes et accédez à notre flotte exclusive immédiatement.
                    </p>
                </div>
            </div>
        </div>
    );
}
