import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';

const Login: React.FC = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        // TODO: Implement actual login logic
        console.log('Login attempt:', { email, password });
        navigate('/dashboard/client');
    };

    return (
        <div className="min-h-screen bg-gray-900 flex">
            {/* Image Side */}
            <div className="hidden lg:block lg:w-1/2 relative overflow-hidden">
                <img
                    className="absolute inset-0 h-full w-full object-cover opacity-60"
                    src="/images/sport.png"
                    alt="Login Background"
                />
                <div className="absolute inset-0 bg-gradient-to-r from-gray-900 via-gray-900/40 to-transparent"></div>
                <div className="absolute bottom-0 left-0 p-12 text-white">
                    <h2 className="text-4xl font-bold mb-4">Bienvenue à Nouveau</h2>
                    <p className="text-lg text-gray-300">Accédez à votre espace personnel et gérez vos locations de luxe.</p>
                </div>
            </div>

            {/* Form Side */}
            <div className="flex-1 flex flex-col justify-center py-12 px-4 sm:px-6 lg:px-20 xl:px-24">
                <div className="mx-auto w-full max-w-sm lg:w-96">
                    <div className="text-center lg:text-left">
                        <Link to="/" className="text-3xl font-extrabold text-gradient tracking-tighter">GDLDV</Link>
                        <h2 className="mt-6 text-3xl font-extrabold text-white">
                            Connexion
                        </h2>
                        <p className="mt-2 text-sm text-gray-400">
                            Ou{' '}
                            <Link to="/register" className="font-medium text-yellow-400 hover:text-yellow-300">
                                créer un compte gratuitement
                            </Link>
                        </p>
                    </div>

                    <div className="mt-8">
                        <div className="card-glass py-8 px-8 border border-white/10">
                            <form className="space-y-6" onSubmit={handleSubmit}>
                                <div>
                                    <label htmlFor="email" className="block text-sm font-medium text-gray-300">
                                        Adresse email
                                    </label>
                                    <div className="mt-1">
                                        <input
                                            id="email"
                                            name="email"
                                            type="email"
                                            autoComplete="email"
                                            required
                                            value={email}
                                            onChange={(e) => setEmail(e.target.value)}
                                            className="appearance-none block w-full px-3 py-2 border border-gray-700 rounded-md shadow-sm placeholder-gray-500 bg-gray-800 text-white focus:outline-none focus:ring-yellow-400 focus:border-yellow-400 sm:text-sm"
                                        />
                                    </div>
                                </div>

                                <div>
                                    <label htmlFor="password" className="block text-sm font-medium text-gray-300">
                                        Mot de passe
                                    </label>
                                    <div className="mt-1">
                                        <input
                                            id="password"
                                            name="password"
                                            type="password"
                                            autoComplete="current-password"
                                            required
                                            value={password}
                                            onChange={(e) => setPassword(e.target.value)}
                                            className="appearance-none block w-full px-3 py-2 border border-gray-700 rounded-md shadow-sm placeholder-gray-500 bg-gray-800 text-white focus:outline-none focus:ring-yellow-400 focus:border-yellow-400 sm:text-sm"
                                        />
                                    </div>
                                </div>

                                <div className="flex items-center justify-between">
                                    <div className="flex items-center">
                                        <input
                                            id="remember-me"
                                            name="remember-me"
                                            type="checkbox"
                                            className="h-4 w-4 text-yellow-400 focus:ring-yellow-400 border-gray-700 rounded bg-gray-800"
                                        />
                                        <label htmlFor="remember-me" className="ml-2 block text-sm text-gray-400">
                                            Se souvenir de moi
                                        </label>
                                    </div>

                                    <div className="text-sm">
                                        <a href="#" className="font-medium text-yellow-400 hover:text-yellow-300">
                                            Mot de passe oublié ?
                                        </a>
                                    </div>
                                </div>

                                <div>
                                    <button
                                        type="submit"
                                        className="w-full flex justify-center py-2 px-4 border border-transparent rounded-full shadow-sm text-sm font-bold text-black bg-yellow-400 hover:bg-yellow-300 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-yellow-400 transition-all duration-200 transform hover:-translate-y-1"
                                    >
                                        Se connecter
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Login;
