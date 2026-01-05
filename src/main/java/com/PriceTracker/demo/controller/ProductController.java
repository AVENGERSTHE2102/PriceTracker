package com.PriceTracker.demo.controller;

import com.PriceTracker.demo.dto.PriceAnalytics;
import com.PriceTracker.demo.dto.ProductRequest;
import com.PriceTracker.demo.dto.ProductResponse;
import com.PriceTracker.demo.models.PriceHistory;
import com.PriceTracker.demo.models.ProductInfo;
import com.PriceTracker.demo.scraper.ScraperFactory;
import com.PriceTracker.demo.service.PriceHistoryService;
import com.PriceTracker.demo.service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for product management.
 * Provides endpoints for CRUD operations, price history, and analytics.
 */
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*") // Allow all origins for development
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;
    private final PriceHistoryService priceHistoryService;
    private final ScraperFactory scraperFactory;

    public ProductController(ProductService productService,
            PriceHistoryService priceHistoryService,
            ScraperFactory scraperFactory) {
        this.productService = productService;
        this.priceHistoryService = priceHistoryService;
        this.scraperFactory = scraperFactory;
    }

    /**
     * Add a new product to track.
     * POST /api/products
     */
    @PostMapping
    public ResponseEntity<ProductInfo> addProduct(@Valid @RequestBody ProductRequest request) {
        log.info("Adding new product: {}", request.getUrl());
        ProductInfo product = productService.addProduct(request);

        return ResponseEntity
                .created(URI.create("/api/products/" + product.getId()))
                .body(product);
    }

    /**
     * Get all products.
     * GET /api/products
     */
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProductResponses());
    }

    /**
     * Get all active products.
     * GET /api/products/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<ProductInfo>> getActiveProducts() {
        return ResponseEntity.ok(productService.getActiveProducts());
    }

    /**
     * Get a product by ID.
     * GET /api/products/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductResponse(id));
    }

    /**
     * Get price history for a product.
     * GET /api/products/{id}/prices?days=30
     */
    @GetMapping("/{id}/prices")
    public ResponseEntity<List<PriceHistory>> getPriceHistory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(priceHistoryService.getHistory(id, days));
    }

    /**
     * Get all price history for a product.
     * GET /api/products/{id}/prices/all
     */
    @GetMapping("/{id}/prices/all")
    public ResponseEntity<List<PriceHistory>> getAllPriceHistory(@PathVariable Long id) {
        return ResponseEntity.ok(priceHistoryService.getAllHistory(id));
    }

    /**
     * Get analytics for a product.
     * GET /api/products/{id}/analytics?days=30
     */
    @GetMapping("/{id}/analytics")
    public ResponseEntity<PriceAnalytics> getAnalytics(
            @PathVariable Long id,
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(priceHistoryService.getAnalytics(id, days));
    }

    /**
     * Delete a product.
     * DELETE /api/products/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Update target price for a product.
     * PATCH /api/products/{id}/target-price
     */
    @PatchMapping("/{id}/target-price")
    public ResponseEntity<ProductInfo> updateTargetPrice(
            @PathVariable Long id,
            @RequestBody Map<String, BigDecimal> body) {
        BigDecimal targetPrice = body.get("targetPrice");
        return ResponseEntity.ok(productService.updateTargetPrice(id, targetPrice));
    }

    /**
     * Update active status for a product.
     * PATCH /api/products/{id}/active
     */
    @PatchMapping("/{id}/active")
    public ResponseEntity<ProductInfo> updateActiveStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> body) {
        Boolean active = body.get("active");
        return ResponseEntity.ok(productService.updateActiveStatus(id, active != null && active));
    }

    /**
     * Manually trigger price scrape for a product.
     * POST /api/products/{id}/scrape
     */
    @PostMapping("/{id}/scrape")
    public ResponseEntity<ProductInfo> scrapeProduct(@PathVariable Long id) {
        log.info("Manual scrape triggered for product ID: {}", id);
        ProductInfo product = productService.updateProductPrice(id);
        return ResponseEntity.ok(product);
    }

    /**
     * Get list of supported sites.
     * GET /api/products/supported-sites
     */
    @GetMapping("/supported-sites")
    public ResponseEntity<List<String>> getSupportedSites() {
        return ResponseEntity.ok(scraperFactory.getSupportedSites());
    }

    /**
     * Check if a URL is supported.
     * GET /api/products/check-url?url=...
     */
    @GetMapping("/check-url")
    public ResponseEntity<Map<String, Object>> checkUrl(@RequestParam String url) {
        boolean supported = scraperFactory.isSupported(url);
        String siteName = scraperFactory.getSiteNameForUrl(url);

        return ResponseEntity.ok(Map.of(
                "url", url,
                "supported", supported,
                "site", siteName));
    }
}
