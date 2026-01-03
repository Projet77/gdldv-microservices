import React from 'react';
import { LucideIcon } from 'lucide-react';

interface StatCardProps {
    title: string;
    value: string | number;
    icon: LucideIcon;
    trend?: string;
    trendUp?: boolean;
    color?: string; // e.g., 'yellow', 'blue', 'green'
}

const StatCard: React.FC<StatCardProps> = ({ title, value, icon: Icon, trend, trendUp, color = 'yellow' }) => {
    return (
        <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 hover:shadow-md transition-shadow duration-300">
            <div className="flex items-center justify-between">
                <div>
                    <p className="text-sm font-medium text-gray-500 uppercase tracking-wide">{title}</p>
                    <p className="mt-2 text-3xl font-black text-gray-900">{value}</p>
                </div>
                <div className={`p-3 rounded-xl bg-${color}-50 text-${color}-600`}>
                    <Icon className="w-8 h-8" />
                </div>
            </div>
            {trend && (
                <div className="mt-4 flex items-center text-sm">
                    <span className={`font-bold ${trendUp ? 'text-green-500' : 'text-red-500'}`}>
                        {trendUp ? '↗' : '↘'} {trend}
                    </span>
                    <span className="text-gray-400 ml-2">vs le mois dernier</span>
                </div>
            )}
        </div>
    );
};

export default StatCard;
