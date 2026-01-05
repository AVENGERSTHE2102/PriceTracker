import React, { useState, useEffect } from 'react';
import {
    BarChart3,
    Plus,
    RefreshCw,
    TrendingDown,
    Activity,
    Package,
    Bell,
    Search,
    LayoutGrid,
    List,
    Filter,
    X,
    Target,
    ArrowRight,
    History
} from 'lucide-react';
import { productService } from './services/api';
import ProductCard from './components/ProductCard';
import AddProductModal from './components/AddProductModal';
import PriceChart from './components/PriceChart';

const App = () => {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [selectedProduct, setSelectedProduct] = useState(null);
    const [isAddModalOpen, setIsAddModalOpen] = useState(false);
    const [searchQuery, setSearchQuery] = useState('');
    const [filterMode, setFilterMode] = useState('ALL'); // ALL, ACTIVE, PRICE_DROP

    useEffect(() => {
        fetchProducts();
    }, []);

    const fetchProducts = async () => {
        try {
            const response = await productService.getAllProducts();
            setProducts(response.data);
            setLoading(false);
        } catch (error) {
            console.error('Error fetching products:', error);
            setLoading(false);
        }
    };

    const handleManualScrape = async (id) => {
        try {
            await productService.scrapeProduct(id);
            fetchProducts();
            // If the selected product is the one being scraped, we should ideally refresh its details too
            if (selectedProduct && selectedProduct.id === id) {
                const updated = await productService.getProduct(id);
                setSelectedProduct(updated.data);
            }
        } catch (error) {
            console.error('Error scraping product:', error);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Are you sure you want to remove this product?')) {
            try {
                await productService.deleteProduct(id);
                fetchProducts();
                if (selectedProduct && selectedProduct.id === id) {
                    setSelectedProduct(null);
                }
            } catch (error) {
                console.error('Error deleting product:', error);
            }
        }
    };

    const filteredProducts = products.filter(p => {
        const matchesSearch = p.name.toLowerCase().includes(searchQuery.toLowerCase());
        if (filterMode === 'ACTIVE') return matchesSearch && p.active;
        if (filterMode === 'PRICE_DROP') return matchesSearch && p.currentPrice < p.avgPrice;
        return matchesSearch;
    });

    const stats = {
        total: products.length,
        active: products.filter(p => p.active).length,
        drops: products.filter(p => p.currentPrice < p.avgPrice).length,
        savings: products.reduce((acc, p) => acc + (p.maxPrice - p.currentPrice > 0 ? p.maxPrice - p.currentPrice : 0), 0)
    };

    return (
        <div className="min-h-screen">
            {/* Header */}
            <header className="border-b border-[rgba(255,255,255,0.05)] bg-[#0f172a]/80 backdrop-blur-md sticky top-0 z-50">
                <div className="container py-4 flex justify-between items-center">
                    <div className="flex items-center gap-3">
                        <div className="w-10 h-10 bg-indigo-500 rounded-xl flex items-center justify-center shadow-lg shadow-indigo-500/20">
                            <Activity className="text-white w-6 h-6" />
                        </div>
                        <div>
                            <h1 className="text-2xl font-bold tracking-tight">PricePulse</h1>
                            <p className="text-[10px] text-indigo-400 font-bold uppercase tracking-[0.2em]">Engine v1.0</p>
                        </div>
                    </div>

                    <div className="hidden md:flex items-center bg-slate-900/50 border border-slate-800 rounded-full px-4 py-2 w-96">
                        <Search size={16} className="text-slate-500 mr-2" />
                        <input
                            type="text"
                            placeholder="Search your inventory..."
                            value={searchQuery}
                            onChange={(e) => setSearchQuery(e.target.value)}
                            className="bg-transparent border-none text-sm text-slate-200 focus:outline-none w-full"
                        />
                    </div>

                    <button
                        onClick={() => setIsAddModalOpen(true)}
                        className="btn btn-primary shadow-indigo-500/20 shadow-lg"
                    >
                        <Plus size={18} />
                        <span className="hidden sm:inline">Track Product</span>
                    </button>
                </div>
            </header>

            <main className="container py-8">
                {/* Stats Row */}
                <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-12">
                    {[
                        { label: 'Inventory', value: stats.total, icon: Package, color: 'indigo' },
                        { label: 'Active Pulse', value: stats.active, icon: Activity, color: 'emerald' },
                        { label: 'Price Drops', value: stats.drops, icon: TrendingDown, color: 'amber' },
                        { label: 'Est. Savings', value: `₹${Math.round(stats.savings).toLocaleString()}`, icon: Bell, color: 'purple' }
                    ].map((stat, i) => (
                        <div key={i} className="glass-card p-5 flex items-start justify-between group">
                            <div>
                                <p className="text-[10px] text-slate-500 font-bold uppercase tracking-wider mb-1">{stat.label}</p>
                                <h3 className="text-2xl font-bold">{stat.value}</h3>
                            </div>
                            <div className={`p-2.5 rounded-lg bg-${stat.color}-500/10 text-${stat.color}-500 group-hover:scale-110 transition-transform`}>
                                <stat.icon size={20} />
                            </div>
                        </div>
                    ))}
                </div>

                {/* Filters & Actions */}
                <div className="mb-8 flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
                    <div className="flex bg-slate-900/50 p-1 rounded-xl border border-slate-800">
                        {['ALL', 'ACTIVE', 'PRICE_DROP'].map((mode) => (
                            <button
                                key={mode}
                                onClick={() => setFilterMode(mode)}
                                className={`px-4 py-2 rounded-lg text-xs font-bold transition-all ${filterMode === mode
                                        ? 'bg-indigo-500 text-white shadow-lg shadow-indigo-500/20'
                                        : 'text-slate-500 hover:text-slate-300'
                                    }`}
                            >
                                {mode.replace('_', ' ')}
                            </button>
                        ))}
                    </div>

                    <div className="flex items-center gap-4 w-full md:w-auto">
                        <div className="flex md:hidden items-center bg-slate-900/50 border border-slate-800 rounded-full px-4 py-2 flex-1">
                            <Search size={16} className="text-slate-500 mr-2" />
                            <input
                                type="text"
                                placeholder="Search..."
                                className="bg-transparent border-none text-sm text-slate-200 focus:outline-none w-full"
                            />
                        </div>
                        <button className="p-3 bg-slate-900/50 border border-slate-800 rounded-xl text-slate-500 hover:text-white transition-colors">
                            <Filter size={18} />
                        </button>
                    </div>
                </div>

                {/* Product Grid */}
                {loading ? (
                    <div className="flex flex-col items-center justify-center py-32 gap-6">
                        <div className="relative">
                            <div className="w-16 h-16 border-4 border-indigo-500/20 border-t-indigo-500 rounded-full animate-spin"></div>
                            <Activity className="absolute inset-0 m-auto text-indigo-500 animate-pulse" size={24} />
                        </div>
                        <p className="text-slate-400 font-bold text-sm uppercase tracking-widest">Synchronizing Pulse Data...</p>
                    </div>
                ) : filteredProducts.length === 0 ? (
                    <div className="glass-card p-20 text-center animate-fade-in max-w-2xl mx-auto">
                        <div className="w-24 h-24 bg-slate-800/30 rounded-full flex items-center justify-center mx-auto mb-8 border border-white/5">
                            <Search size={40} className="text-slate-600" />
                        </div>
                        <h3 className="text-2xl font-bold mb-3">No pulses found</h3>
                        <p className="text-slate-400 mb-10 leading-relaxed font-medium">
                            {searchQuery
                                ? `We couldn't find any products matching "${searchQuery}". Maybe try a different search?`
                                : "Your tracking engine is currently idle. Let's add some products to monitor the e-commerce pulse."}
                        </p>
                        <button
                            onClick={() => setIsAddModalOpen(true)}
                            className="btn btn-primary"
                        >
                            Start Tracking Now
                        </button>
                    </div>
                ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
                        {filteredProducts.map((product) => (
                            <ProductCard
                                key={product.id}
                                product={product}
                                onDelete={handleDelete}
                                onScrape={handleManualScrape}
                                onClick={setSelectedProduct}
                            />
                        ))}
                    </div>
                )}
            </main>

            {/* Product Detail Overlay */}
            {selectedProduct && (
                <div className="fixed inset-0 z-[110] flex items-end md:items-center justify-center md:p-4">
                    <div className="absolute inset-0 bg-[#0f172a]/95 backdrop-blur-xl animate-in fade-in duration-300" onClick={() => setSelectedProduct(null)}></div>

                    <div className="glass-card w-full max-w-4xl h-[90vh] md:h-auto md:max-h-[85vh] overflow-y-auto relative animate-in slide-in-from-bottom md:zoom-in-95 duration-500">
                        {/* Detail Layout */}
                        <div className="p-8">
                            <div className="flex justify-between items-start mb-10">
                                <div className="pr-12">
                                    <div className="flex items-center gap-3 mb-4">
                                        <span className="bg-indigo-500/20 text-indigo-400 text-[10px] font-bold px-3 py-1 rounded-full border border-indigo-500/20 uppercase tracking-widest">
                                            {selectedProduct.sourceSite}
                                        </span>
                                        <span className={`text-[10px] font-bold px-3 py-1 rounded-full border flex items-center gap-1.5 uppercase tracking-widest ${selectedProduct.active ? 'bg-emerald-500/10 text-emerald-500 border-emerald-500/10' : 'bg-slate-500/10 text-slate-500 border-slate-500/10'
                                            }`}>
                                            <div className={`w-1.5 h-1.5 rounded-full ${selectedProduct.active ? 'bg-emerald-500 animate-pulse' : 'bg-slate-500'}`}></div>
                                            {selectedProduct.active ? 'Actively Monitoring' : 'Idle'}
                                        </span>
                                    </div>
                                    <h2 className="text-3xl font-bold leading-tight mb-4">{selectedProduct.name}</h2>
                                    <div className="flex gap-6 items-center flex-wrap">
                                        <div className="flex items-center gap-2 text-slate-400 text-sm">
                                            <History size={16} />
                                            Joined {new Date(selectedProduct.createdAt).toLocaleDateString()}
                                        </div>
                                        <a
                                            href={selectedProduct.productUrl}
                                            target="_blank"
                                            rel="noopener noreferrer"
                                            className="text-indigo-400 text-sm font-bold flex items-center gap-2 hover:underline"
                                        >
                                            <ExternalLink size={16} />
                                            Go to Original Listing
                                        </a>
                                    </div>
                                </div>
                                <button
                                    onClick={() => setSelectedProduct(null)}
                                    className="bg-slate-800/50 hover:bg-slate-800 p-2.5 rounded-full text-slate-400 hover:text-white transition-all shadow-lg"
                                >
                                    <X size={24} />
                                </button>
                            </div>

                            {/* Detail Stats */}
                            <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-12">
                                {[
                                    { label: 'Current Price', value: `₹${selectedProduct.currentPrice?.toLocaleString()}`, sub: 'Last detected today' },
                                    { label: 'Lowest Seen', value: `₹${selectedProduct.minPrice?.toLocaleString() || 'N/A'}`, sub: 'Optimal buy point' },
                                    { label: 'Highest Seen', value: `₹${selectedProduct.maxPrice?.toLocaleString() || 'N/A'}`, sub: 'Historical peak' },
                                    { label: 'Target Set', value: `₹${selectedProduct.targetPrice?.toLocaleString() || '---'}`, sub: 'Trigger point' },
                                ].map((stat, i) => (
                                    <div key={i} className="bg-slate-900/30 border border-white/5 rounded-2xl p-5">
                                        <p className="text-[10px] text-slate-500 font-bold uppercase mb-2 ml-1 tracking-wider">{stat.label}</p>
                                        <h4 className="text-xl font-bold mb-1">{stat.value}</h4>
                                        <p className="text-[10px] text-slate-600 font-medium">{stat.sub}</p>
                                    </div>
                                ))}
                            </div>

                            {/* Pulse Visualization */}
                            <div className="mb-12">
                                <div className="flex items-center justify-between mb-6">
                                    <h3 className="text-lg font-bold flex items-center gap-3">
                                        <BarChart3 className="text-indigo-500" />
                                        Historical Pulse Analysis
                                    </h3>
                                    <div className="flex gap-2 text-[10px] font-bold">
                                        <span className="bg-slate-800 px-3 py-1 rounded-full text-slate-400">30 DAYS</span>
                                        <span className="bg-indigo-500 px-3 py-1 rounded-full text-white">ALL TIME</span>
                                    </div>
                                </div>
                                <div className="bg-slate-900/30 border border-white/5 rounded-3xl p-6 h-[400px]">
                                    {/* In a real app we'd fetch actual history for the selected product here */}
                                    <PriceChart
                                        data={[
                                            { price: selectedProduct.currentPrice * 1.15, scrapedAt: new Date(Date.now() - 86400000 * 10) },
                                            { price: selectedProduct.currentPrice * 1.10, scrapedAt: new Date(Date.now() - 86400000 * 8) },
                                            { price: selectedProduct.currentPrice * 1.25, scrapedAt: new Date(Date.now() - 86400000 * 6) },
                                            { price: selectedProduct.currentPrice * 1.05, scrapedAt: new Date(Date.now() - 86400000 * 4) },
                                            { price: selectedProduct.currentPrice * 1.10, scrapedAt: new Date(Date.now() - 86400000 * 2) },
                                            { price: selectedProduct.currentPrice, scrapedAt: new Date() },
                                        ]}
                                    />
                                </div>
                            </div>

                            {/* Settings/Info */}
                            <div className="flex flex-col md:flex-row gap-8 items-center justify-between pt-8 border-t border-white/5">
                                <div className="flex gap-8">
                                    <div className="flex items-center gap-3">
                                        <div className="w-10 h-10 bg-indigo-500/10 rounded-xl flex items-center justify-center text-indigo-500">
                                            <Clock size={20} />
                                        </div>
                                        <div>
                                            <p className="text-[10px] text-slate-500 font-bold uppercase tracking-wider">Frequency</p>
                                            <p className="text-sm font-bold">{selectedProduct.scrapeFrequency}</p>
                                        </div>
                                    </div>
                                    <div className="flex items-center gap-3">
                                        <div className="w-10 h-10 bg-emerald-500/10 rounded-xl flex items-center justify-center text-emerald-500">
                                            <Mail size={20} />
                                        </div>
                                        <div>
                                            <p className="text-[10px] text-slate-500 font-bold uppercase tracking-wider">Alerts Sent</p>
                                            <p className="text-sm font-bold">{selectedProduct.alertEmail || 'No Email Set'}</p>
                                        </div>
                                    </div>
                                </div>

                                <div className="flex gap-3 w-full md:w-auto">
                                    <button
                                        onClick={() => handleManualScrape(selectedProduct.id)}
                                        className="btn btn-outline flex-1 md:flex-none border-indigo-500/30 text-indigo-400"
                                    >
                                        <RefreshCw size={16} />
                                        Recalibrate Pulse
                                    </button>
                                    <button
                                        onClick={() => handleDelete(selectedProduct.id)}
                                        className="btn btn-outline flex-1 md:flex-none border-red-500/30 text-red-500"
                                    >
                                        <Trash2 size={16} />
                                        Erase Record
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            )}

            {/* Modals */}
            <AddProductModal
                isOpen={isAddModalOpen}
                onClose={() => setIsAddModalOpen(false)}
                onAdded={fetchProducts}
            />

            {/* Footer */}
            <footer className="mt-20 border-t border-slate-800/60 py-12">
                <div className="container flex flex-col md:flex-row justify-between items-center gap-8">
                    <div className="flex items-center gap-3 opacity-50 grayscale hover:grayscale-0 transition-all cursor-pointer">
                        <Activity size={24} />
                        <span className="font-bold tracking-tight text-xl">PricePulse</span>
                    </div>
                    <div className="text-slate-500 text-[10px] font-bold uppercase tracking-[0.2em]">
                        &copy; 2026 Engine Logic By Advanced Agentic Coding
                    </div>
                    <div className="flex gap-6 text-[10px] font-bold uppercase tracking-widest text-slate-500">
                        <a href="#" className="hover:text-indigo-400 transition-colors">Documentation</a>
                        <a href="#" className="hover:text-indigo-400 transition-colors">API Status</a>
                    </div>
                </div>
            </footer>
        </div>
    );
};

export default App;
