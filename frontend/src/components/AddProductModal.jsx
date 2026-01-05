import React, { useState } from 'react';
import {
    X,
    Link,
    Target,
    Clock,
    Mail,
    AlertCircle,
    CheckCircle2,
    Loader2
} from 'lucide-react';
import { productService } from '../services/api';

const AddProductModal = ({ isOpen, onClose, onAdded }) => {
    const [url, setUrl] = useState('');
    const [targetPrice, setTargetPrice] = useState('');
    const [email, setEmail] = useState('');
    const [frequency, setFrequency] = useState('DAILY');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [step, setStep] = useState(1); // 1: Input, 2: Success

    if (!isOpen) return null;

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            await productService.addProduct({
                url,
                targetPrice: targetPrice ? parseFloat(targetPrice) : null,
                alertEmail: email,
                scrapeFrequency: frequency
            });
            setStep(2);
            onAdded();
        } catch (err) {
            console.error('Error adding product:', err);
            setError(err.response?.data?.message || 'Failed to add product. Please check the URL and try again.');
        } finally {
            setLoading(false);
        }
    };

    const handleReset = () => {
        setUrl('');
        setTargetPrice('');
        setEmail('');
        setFrequency('DAILY');
        setError('');
        setStep(1);
        onClose();
    };

    return (
        <div className="fixed inset-0 z-[100] flex items-center justify-center p-4">
            <div
                className="absolute inset-0 bg-[#0f172a]/90 backdrop-blur-md animate-in fade-in duration-300"
                onClick={handleReset}
            ></div>

            <div className="glass-card max-w-lg w-full p-0 relative overflow-hidden animate-in zoom-in-95 duration-300">
                {/* Header Decoration */}
                <div className="h-2 bg-gradient-to-r from-indigo-500 via-purple-500 to-emerald-500"></div>

                <div className="p-8">
                    {step === 1 ? (
                        <>
                            <div className="mb-8">
                                <h2 className="text-3xl font-bold tracking-tight mb-2">Track New Product</h2>
                                <p className="text-slate-400 font-medium">Add an Amazon or Flipkart link to start monitoring.</p>
                            </div>

                            {error && (
                                <div className="mb-6 p-4 bg-red-500/10 border border-red-500/20 rounded-xl flex items-start gap-3 text-red-400 text-sm">
                                    <AlertCircle size={18} className="shrink-0 mt-0.5" />
                                    <p>{error}</p>
                                </div>
                            )}

                            <form onSubmit={handleSubmit} className="space-y-6">
                                <div className="space-y-2">
                                    <label className="text-xs font-bold text-slate-500 uppercase tracking-wider flex items-center gap-2">
                                        <Link size={14} />
                                        Product URL
                                    </label>
                                    <input
                                        type="url"
                                        required
                                        value={url}
                                        onChange={(e) => setUrl(e.target.value)}
                                        className="w-full bg-[#0f172a]/50 border border-slate-700/50 rounded-xl p-4 text-slate-200 focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 transition-all font-medium placeholder:text-slate-600"
                                        placeholder="https://www.amazon.in/dp/..."
                                    />
                                </div>

                                <div className="grid grid-cols-2 gap-4">
                                    <div className="space-y-2">
                                        <label className="text-xs font-bold text-slate-500 uppercase tracking-wider flex items-center gap-2">
                                            <Target size={14} />
                                            Target Price
                                        </label>
                                        <input
                                            type="number"
                                            value={targetPrice}
                                            onChange={(e) => setTargetPrice(e.target.value)}
                                            className="w-full bg-[#0f172a]/50 border border-slate-700/50 rounded-xl p-4 text-slate-200 focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 transition-all font-medium placeholder:text-slate-600"
                                            placeholder="Optional"
                                        />
                                    </div>
                                    <div className="space-y-2">
                                        <label className="text-xs font-bold text-slate-500 uppercase tracking-wider flex items-center gap-2">
                                            <Clock size={14} />
                                            Scrape Frequency
                                        </label>
                                        <div className="relative">
                                            <select
                                                value={frequency}
                                                onChange={(e) => setFrequency(e.target.value)}
                                                className="w-full bg-[#0f172a]/50 border border-slate-700/50 rounded-xl p-4 text-slate-200 focus:outline-none focus:border-indigo-500 transition-all font-medium appearance-none"
                                            >
                                                <option value="DAILY">Daily Scan</option>
                                                <option value="HOURLY">Hourly Pulse</option>
                                            </select>
                                            <div className="absolute right-4 top-1/2 -translate-y-1/2 pointer-events-none text-slate-500">
                                                <ArrowDown size={14} />
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div className="space-y-2">
                                    <label className="text-xs font-bold text-slate-500 uppercase tracking-wider flex items-center gap-2">
                                        <Mail size={14} />
                                        Alert Email
                                    </label>
                                    <input
                                        type="email"
                                        value={email}
                                        onChange={(e) => setEmail(e.target.value)}
                                        className="w-full bg-[#0f172a]/50 border border-slate-700/50 rounded-xl p-4 text-slate-200 focus:outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-500/10 transition-all font-medium placeholder:text-slate-600"
                                        placeholder="Where to send notifications"
                                    />
                                </div>

                                <div className="pt-4">
                                    <button
                                        type="submit"
                                        disabled={loading}
                                        className="w-full btn btn-primary py-4 text-base relative overflow-hidden group disabled:opacity-70 disabled:cursor-not-allowed"
                                    >
                                        {loading ? (
                                            <div className="flex items-center gap-2">
                                                <Loader2 size={20} className="animate-spin" />
                                                <span>Initializing Pulse Engine...</span>
                                            </div>
                                        ) : (
                                            <span>Start Tracking Product</span>
                                        )}
                                    </button>
                                </div>
                            </form>
                        </>
                    ) : (
                        <div className="py-8 text-center animate-in fade-in zoom-in-95 duration-500">
                            <div className="w-20 h-20 bg-emerald-500/10 rounded-full flex items-center justify-center mx-auto mb-6">
                                <CheckCircle2 size={40} className="text-emerald-500" />
                            </div>
                            <h2 className="text-3xl font-bold mb-3">Product Queued!</h2>
                            <p className="text-slate-400 mb-10 max-w-sm mx-auto leading-relaxed">
                                We've successfully added the product to our watch list. You'll receive alerts as soon as the price drops.
                            </p>
                            <button
                                onClick={handleReset}
                                className="btn btn-primary w-full py-4"
                            >
                                Go to Dashboard
                            </button>
                        </div>
                    )}
                </div>

                <button
                    onClick={handleReset}
                    className="absolute top-6 right-6 text-slate-500 hover:text-white p-2 hover:bg-white/5 rounded-full transition-all"
                >
                    <X size={24} />
                </button>
            </div>
        </div>
    );
};

// Helper for select icon
const ArrowDown = ({ size }) => (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="m6 9 6 6 6-6" /></svg>
);

export default AddProductModal;
