import React from 'react';
import { Shield, AlertTriangle, CheckCircle, Lock, Key } from 'lucide-react';

const SuperAdminSecurity: React.FC = () => {
    return (
        <div className="space-y-6">
            <div>
                <h1 className="text-3xl font-black text-gray-900 tracking-tight">Sécurité</h1>
                <p className="mt-1 text-gray-500">Surveillance et audit de la sécurité du système.</p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div className="bg-green-50 rounded-2xl p-6 border border-green-100 flex items-start gap-4">
                    <CheckCircle className="h-8 w-8 text-green-600 mt-1" />
                    <div>
                        <h3 className="text-lg font-bold text-green-900">Système Sécurisé</h3>
                        <p className="text-green-700 text-sm mt-1">Aucune menace critique détectée.</p>
                    </div>
                </div>
                <div className="bg-white rounded-2xl p-6 border border-gray-100 shadow-sm">
                    <div className="flex items-center gap-3 mb-2">
                        <Lock className="h-5 w-5 text-gray-400" />
                        <h3 className="font-bold text-gray-900">SSL / TLS</h3>
                    </div>
                    <p className="text-sm text-gray-500">Certificat valide (expire dans 240 jours).</p>
                </div>
                <div className="bg-white rounded-2xl p-6 border border-gray-100 shadow-sm">
                    <div className="flex items-center gap-3 mb-2">
                        <Key className="h-5 w-5 text-gray-400" />
                        <h3 className="font-bold text-gray-900">Auth Service</h3>
                    </div>
                    <p className="text-sm text-gray-500">BCrypt strength: 10. Tokens: JWT HS256.</p>
                </div>
            </div>

            <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                <div className="p-6 border-b border-gray-100 flex items-center justify-between">
                    <h2 className="text-lg font-bold text-gray-900">Journal d'Audit</h2>
                    <button className="text-sm font-bold text-yellow-500 hover:text-yellow-600">Exporter CSV</button>
                </div>
                <div className="p-6">
                    <div className="space-y-4">
                        {[1, 2, 3, 4, 5].map((i) => (
                            <div key={i} className="flex items-center justify-between p-4 bg-gray-50 rounded-xl border border-gray-100">
                                <div className="flex items-center gap-4">
                                    <div className="h-10 w-10 rounded-full bg-blue-100 flex items-center justify-center">
                                        <Shield className="h-5 w-5 text-blue-600" />
                                    </div>
                                    <div>
                                        <p className="font-bold text-gray-900 text-sm">Tentative de connexion (Admin)</p>
                                        <p className="text-xs text-gray-500">IP: 192.168.1.{10 + i} • User-Agent: Mozilla/5.0</p>
                                    </div>
                                </div>
                                <span className="text-xs font-mono text-gray-400">2026-01-05 10:0{i}:23</span>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default SuperAdminSecurity;
