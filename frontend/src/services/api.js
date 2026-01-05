import axios from 'axios';

const api = axios.create({
    baseURL: '/api',
    headers: {
        'Content-Type': 'application/json',
    },
});

export const productService = {
    getAllProducts: () => api.get('/products'),
    getActiveProducts: () => api.get('/products/active'),
    getProduct: (id) => api.get(`/products/${id}`),
    addProduct: (data) => api.post('/products', data),
    deleteProduct: (id) => api.delete(`/products/${id}`),
    updateTargetPrice: (id, targetPrice) => api.patch(`/products/${id}/target-price`, { targetPrice }),
    updateActiveStatus: (id, active) => api.patch(`/products/${id}/active`, { active }),
    scrapeProduct: (id) => api.post(`/products/${id}/scrape`),
    getSupportedSites: () => api.get('/products/supported-sites'),
    checkUrl: (url) => api.get(`/products/check-url?url=${encodeURIComponent(url)}`),
};

export const priceHistoryService = {
    getHistory: (id, days = 30) => api.get(`/products/${id}/prices?days=${days}`),
    getAllHistory: (id) => api.get(`/products/${id}/prices/all`),
    getAnalytics: (id, days = 30) => api.get(`/products/${id}/analytics?days=${days}`),
};

export const alertService = {
    getAlertsForProduct: (productId) => api.get(`/alerts/product/${productId}`),
    getPendingAlerts: () => api.get('/alerts/pending'),
};

export default api;
