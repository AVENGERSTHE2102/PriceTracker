package com.PriceTracker.demo.service;

import com.PriceTracker.demo.dto.ProductPrice;
import com.PriceTracker.demo.dto.ProductRequest;
import com.PriceTracker.demo.dto.ProductResponse;
import com.PriceTracker.demo.exception.DuplicateProductException;
import com.PriceTracker.demo.exception.ProductNotFoundException;
import com.PriceTracker.demo.models.ProductInfo;
import com.PriceTracker.demo.repositories.PriceHistoryRepo;
import com.PriceTracker.demo.repositories.ProductRepo;
import com.PriceTracker.demo.scraper.PriceScraper;
import com.PriceTracker.demo.scraper.ScraperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing products.
 * Handles product CRUD operations and price scraping.
 */
@Service
@Transactional
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepo productRepo;
    private final PriceHistoryRepo priceHistoryRepo;
    private final ScraperFactory scraperFactory;
    private final PriceHistoryService priceHistoryService;
    private final AlertService alertService;

    public ProductService(ProductRepo productRepo,
            PriceHistoryRepo priceHistoryRepo,
            ScraperFactory scraperFactory,
            PriceHistoryService priceHistoryService,
            AlertService alertService) {
        this.productRepo = productRepo;
        this.priceHistoryRepo = priceHistoryRepo;
        this.scraperFactory = scraperFactory;
        this.priceHistoryService = priceHistoryService;
        this.alertService = alertService;
    }

    /**
     * Add a new product to track.
     */
    public ProductInfo addProduct(ProductRequest request) {
        String url = request.getUrl().trim();

        // Check for duplicate URL
        if (productRepo.existsByProductUrl(url)) {
            throw new DuplicateProductException(url);
        }

        // Get appropriate scraper and scrape initial price
        PriceScraper scraper = scraperFactory.getScraperForUrl(url);
        ProductPrice scrapedData = scraper.scrape(url);

        // Create product entity
        ProductInfo product = new ProductInfo();
        product.setProductUrl(url);
        product.setName(scrapedData.getProductName());
        product.setSourceSite(scraper.getSiteName());
        product.setCurrentPrice(scrapedData.getPrice());
        product.setTargetPrice(request.getTargetPrice());
        product.setScrapeFrequency(request.getScrapeFrequency() != null ? request.getScrapeFrequency() : "DAILY");
        product.setAlertEmail(request.getAlertEmail());
        product.setActive(true);

        // Save product
        ProductInfo saved = productRepo.save(product);
        log.info("Added new product: {} ({})", saved.getName(), saved.getSourceSite());

        // Save initial price to history
        priceHistoryService.savePrice(saved, scrapedData.getPrice(),
                scrapedData.getAvailable(), scrapedData.getCurrency());

        return saved;
    }

    /**
     * Get a product by ID.
     */
    @Transactional(readOnly = true)
    public ProductInfo getProduct(Long id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    /**
     * Get product response with analytics.
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductResponse(Long id) {
        ProductInfo product = getProduct(id);
        return toProductResponse(product);
    }

    /**
     * Get all products.
     */
    @Transactional(readOnly = true)
    public List<ProductInfo> getAllProducts() {
        return productRepo.findAll();
    }

    /**
     * Get all active products.
     */
    @Transactional(readOnly = true)
    public List<ProductInfo> getActiveProducts() {
        return productRepo.findByActiveTrue();
    }

    /**
     * Get all product responses with analytics.
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProductResponses() {
        return productRepo.findAll().stream()
                .map(this::toProductResponse)
                .toList();
    }

    /**
     * Delete a product.
     */
    public void deleteProduct(Long id) {
        ProductInfo product = getProduct(id);
        productRepo.delete(product);
        log.info("Deleted product: {} (ID: {})", product.getName(), id);
    }

    /**
     * Update target price for a product.
     */
    public ProductInfo updateTargetPrice(Long id, java.math.BigDecimal targetPrice) {
        ProductInfo product = getProduct(id);
        product.setTargetPrice(targetPrice);
        return productRepo.save(product);
    }

    /**
     * Update product active status.
     */
    public ProductInfo updateActiveStatus(Long id, boolean active) {
        ProductInfo product = getProduct(id);
        product.setActive(active);
        return productRepo.save(product);
    }

    /**
     * Scrape and update price for a product.
     */
    public ProductInfo updateProductPrice(Long productId) {
        ProductInfo product = getProduct(productId);

        // Get previous price for comparison
        java.math.BigDecimal previousPrice = product.getCurrentPrice();

        // Scrape current price
        PriceScraper scraper = scraperFactory.getScraperForUrl(product.getProductUrl());
        ProductPrice scrapedData = scraper.scrape(product.getProductUrl());

        java.math.BigDecimal newPrice = scrapedData.getPrice();

        // Save to price history
        priceHistoryService.savePrice(product, newPrice,
                scrapedData.getAvailable(), scrapedData.getCurrency());

        // Check for alert conditions
        if (previousPrice != null) {
            alertService.checkAndTriggerAlerts(product, newPrice, previousPrice);
        }

        log.info("Updated price for {}: {} -> {}", product.getName(), previousPrice, newPrice);

        // Refresh and return updated product
        return productRepo.findById(productId).orElse(product);
    }

    /**
     * Get products by scrape frequency.
     */
    @Transactional(readOnly = true)
    public List<ProductInfo> getProductsByFrequency(String frequency) {
        return productRepo.findByActiveTrueAndScrapeFrequency(frequency);
    }

    /**
     * Convert ProductInfo to ProductResponse with analytics.
     */
    private ProductResponse toProductResponse(ProductInfo product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .sourceSite(product.getSourceSite())
                .productUrl(product.getProductUrl())
                .scrapeFrequency(product.getScrapeFrequency())
                .targetPrice(product.getTargetPrice())
                .currentPrice(product.getCurrentPrice())
                .alertEmail(product.getAlertEmail())
                .active(product.isActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .minPrice(priceHistoryRepo.findMinPriceByProductId(product.getId()))
                .maxPrice(priceHistoryRepo.findMaxPriceByProductId(product.getId()))
                .avgPrice(priceHistoryRepo.findAvgPriceByProductId(product.getId()))
                .priceRecordCount(priceHistoryRepo.countByProductId(product.getId()))
                .build();
    }
}
