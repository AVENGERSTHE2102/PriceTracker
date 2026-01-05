import React, { useState } from 'react';
import {
    Plus,
    Trash2,
    ExternalLink,
    RefreshCw,
    TrendingDown,
    ChevronRight,
    Target,
    Clock,
    Globe
} from 'lucide-react';
import PriceChart from './PriceChart';

const ProductCard = ({ product, onDelete, onScrape, onClick }) => {
    const [isScraping, setIsScraping] = useState(false);

    const handleScrape = async (e) => {
        e.stopPropagation();
        setIsScraping(true);
        await onScrape(product.id);
        setIsScraping(false);
    };

    const handleSourceLink = (e) => {
        e.stopPropagation();
    };

    // Mock data for chart if none exists (for demo)
    const mockChartData = [
        { price: product.currentPrice * 1.05, scrapedAt: new Date(Date.now() - 86400000 * 4) },
        { price: product.currentPrice * 1.1, scrapedAt: new Date(Date.now() - 86400000 * 3) },
        { price: product.currentPrice * 1.08, scrapedAt: new Date(Date.now() - 86400000 * 2) },
        { price: product.currentPrice * 0.95, scrapedAt: new Date(Date.now() - 86400000 * 1) },
        { price: product.currentPrice, scrapedAt: new Date() },
    ];

    const hasLowerPrice = product.currentPrice < (product.avgPrice || product.currentPrice);

    return (
        <div
            onClick={() => onClick(product)}
            className="glass-card group overflow-hidden animate-fade-in relative transition-all hover:border-indigo-500/40 cursor-pointer"
        >
            {/* Site Badge */}
            <div className="absolute top-4 right-4 z-10 flex items-center gap-2">
                <span className={`text-[10px] font-bold px-2 py-1 rounded border uppercase tracking-wider ${product.sourceSite === 'Amazon'
                        ? 'bg-amber-500/10 text-amber-500 border-amber-500/20'
                        : 'bg-blue-500/10 text-blue-500 border-blue-500/20'
                    }`}>
                    {product.sourceSite}
                </span>
            </div>

            <div className="p-6 pb-2">
                {/* Title */}
                <h3 className="text-xl font-bold mb-2 line-clamp-1 leading-snug group-hover:text-indigo-400 transition-colors pr-16" title={product.name}>
                    {product.name}
                </h3>

                {/* URL Status */}
                <div className="flex items-center gap-2 mb-6">
                    <Globe size={12} className="text-slate-500" />
                    <span className="text-[10px] text-slate-500 font-bold uppercase tracking-widest truncate max-w-[150px]">
                        {new URL(product.productUrl).hostname}
                    </span>
                </div>

                {/* Price Section */}
                <div className="flex items-end justify-between mb-4">
                    <div>
                        <p className="text-[10px] text-slate-500 font-bold uppercase mb-1 tracking-wider">Current Pulse</p>
                        <div className="flex items-center gap-3">
                            <span className="text-3xl font-bold tracking-tight">
                                ₹{product.currentPrice?.toLocaleString() || 'N/A'}
                            </span>
                            {hasLowerPrice && (
                                <div className="flex items-center gap-1 text-emerald-400 text-[10px] bg-emerald-400/10 px-2 py-1 rounded-full font-bold">
                                    <TrendingDown size={12} />
                                    -12%
                                </div>
                            )}
                        </div>
                    </div>

                    <div className="flex flex-col items-end">
                        <div className="flex items-center gap-1.5 text-slate-500 mb-1">
                            <Target size={12} />
                            <span className="text-[10px] font-bold uppercase">Target</span>
                        </div>
                        <span className="text-sm font-bold text-slate-300">
                            ₹{product.targetPrice?.toLocaleString() || '---'}
                        </span>
                    </div>
                </div>
            </div>

            {/* Mini Chart */}
            <div className="px-2 -mx-2 h-32 opacity-80 group-hover:opacity-100 transition-opacity">
                <PriceChart data={mockChartData} />
            </div>

            <div className="p-4 pt-1 bg-white/[0.02] border-t border-white/5 flex gap-2">
                <button
                    onClick={handleSourceLink}
                    as="a"
                    href={product.productUrl}
                    target="_blank"
                    className="flex-1 btn btn-outline py-2 h-9 text-xs gap-2 group/btn"
                >
                    <span>Store</span>
                    <ExternalLink size={12} />
                </button>
                <button
                    onClick={handleScrape}
                    className={`w-9 h-9 glass-card flex items-center justify-center text-slate-400 hover:text-indigo-400 hover:border-indigo-500/50 transition-all ${isScraping ? 'animate-spin border-indigo-500/50 text-indigo-400' : ''}`}
                    title="Force Refresh"
                >
                    <RefreshCw size={14} />
                </button>
                <button
                    onClick={(e) => { e.stopPropagation(); onDelete(product.id); }}
                    className="w-9 h-9 glass-card flex items-center justify-center text-slate-400 hover:text-red-400 hover:border-red-500/50 transition-all"
                    title="Remove"
                >
                    <Trash2 size={14} />
                </button>
            </div>
        </div>
    );
};

export default ProductCard;
