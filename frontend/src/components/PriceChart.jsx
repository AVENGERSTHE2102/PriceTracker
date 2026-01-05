import React from 'react';
import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    ResponsiveContainer,
    AreaChart,
    Area
} from 'recharts';

const PriceChart = ({ data, currency = '₹' }) => {
    if (!data || data.length === 0) {
        return (
            <div className="h-full flex items-center justify-center text-slate-500 bg-slate-900/20 rounded-xl border border-dashed border-slate-800">
                <p className="text-sm">Not enough data to generate pulse</p>
            </div>
        );
    }

    // Format data for chart
    const chartData = data.map(item => ({
        time: new Date(item.scrapedAt).toLocaleDateString([], { month: 'short', day: 'numeric' }),
        price: item.price,
        fullDate: new Date(item.scrapedAt).toLocaleString()
    }));

    const CustomTooltip = ({ active, payload }) => {
        if (active && payload && payload.length) {
            return (
                <div className="bg-[#1e293b] border border-slate-700 p-3 rounded-lg shadow-xl backdrop-blur-md">
                    <p className="text-[10px] text-slate-400 font-bold uppercase mb-1">{payload[0].payload.fullDate}</p>
                    <p className="text-lg font-bold text-indigo-400">
                        {currency}{payload[0].value.toLocaleString()}
                    </p>
                </div>
            );
        }
        return null;
    };

    return (
        <div className="h-[250px] w-full mt-4">
            <ResponsiveContainer width="100%" height="100%">
                <AreaChart data={chartData}>
                    <defs>
                        <linearGradient id="colorPrice" x1="0" y1="0" x2="0" y2="1">
                            <stop offset="5%" stopColor="#6366f1" stopOpacity={0.3} />
                            <stop offset="95%" stopColor="#6366f1" stopOpacity={0} />
                        </linearGradient>
                    </defs>
                    <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="rgba(255,255,255,0.05)" />
                    <XAxis
                        dataKey="time"
                        axisLine={false}
                        tickLine={false}
                        tick={{ fill: '#64748b', fontSize: 10, fontWeight: 600 }}
                        dy={10}
                    />
                    <YAxis
                        hide
                        domain={['auto', 'auto']}
                    />
                    <Tooltip content={<CustomTooltip />} cursor={{ stroke: 'rgba(99, 102, 241, 0.2)', strokeWidth: 2 }} />
                    <Area
                        type="monotone"
                        dataKey="price"
                        stroke="#6366f1"
                        strokeWidth={3}
                        fillOpacity={1}
                        fill="url(#colorPrice)"
                        animationDuration={1500}
                    />
                </AreaChart>
            </ResponsiveContainer>
        </div>
    );
};

export default PriceChart;
