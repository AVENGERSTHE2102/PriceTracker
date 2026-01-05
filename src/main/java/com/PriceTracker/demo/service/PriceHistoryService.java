package com.PriceTracker.demo.service;

import com.PriceTracker.demo.dto.PriceAnalytics;
import com.PriceTracker.demo.exception.ProductNotFoundException;
import com.PriceTracker.demo.models.PriceHistory;
import com.PriceTracker.demo.models.ProductInfo;
import com.PriceTracker.demo.repositories.PriceHistoryRepo;
import com.PriceTracker.demo.repositories.ProductRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing price history data.
 * Provides methods for saving prices, retrieving history, and computing
 * analytics.
 */
@Service
@Transactional(readOnly = true)
public class PriceHistoryService {

    private static final Logger log = LoggerFactory.getLogger(PriceHistoryService.class);

    private final PriceHistoryRepo priceHistoryRepo;
    private final ProductRepo productRepo;

    public PriceHistoryService(PriceHistoryRepo priceHistoryRepo, ProductRepo productRepo) {
        this.priceHistoryRepo = priceHistoryRepo;
        this.productRepo = productRepo;
    }

    /**
     * Save a new price record for a product.
     */
    @Transactional
    public PriceHistory savePrice(ProductInfo product, BigDecimal price, Boolean available, String currency) {
        PriceHistory priceHistory = new PriceHistory(product, price, available, currency);
        PriceHistory saved = priceHistoryRepo.save(priceHistory);

        // Update product's current price
        product.setCurrentPrice(price);
        productRepo.save(product);

        log.info("Saved price {} for product {}", price, product.getName());
        return saved;
    }

    /**
     * Get price history for a product within the last N days.
     */
    public List<PriceHistory> getHistory(Long productId, int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        LocalDateTime endDate = LocalDateTime.now();

        return priceHistoryRepo.findByProductIdAndDateRange(productId, startDate, endDate);
    }

    /**
     * Get all price history for a product (ordered by most recent first).
     */
    public List<PriceHistory> getAllHistory(Long productId) {
        return priceHistoryRepo.findByProductIdOrderByScrapedAtDesc(productId);
    }

    /**
     * Get the most recent price for a product.
     */
    public Optional<PriceHistory> getLatestPrice(Long productId) {
        return priceHistoryRepo.findTopByProductIdOrderByScrapedAtDesc(productId);
    }

    /**
     * Compute price analytics for a product.
     */
    public PriceAnalytics getAnalytics(Long productId, int days) {
        ProductInfo product = productRepo.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        List<PriceHistory> history = getHistory(productId, days);

        if (history.isEmpty()) {
            return PriceAnalytics.builder()
                    .productId(productId)
                    .productName(product.getName())
                    .currentPrice(product.getCurrentPrice())
                    .daysAnalyzed(days)
                    .recordCount(0L)
                    .build();
        }

        // Get aggregate values from repository queries
        BigDecimal minPrice = priceHistoryRepo.findMinPriceByProductId(productId);
        BigDecimal maxPrice = priceHistoryRepo.findMaxPriceByProductId(productId);
        BigDecimal avgPrice = priceHistoryRepo.findAvgPriceByProductId(productId);
        long recordCount = priceHistoryRepo.countByProductId(productId);

        // Get current price (most recent)
        BigDecimal currentPrice = history.get(history.size() - 1).getPrice();

        // Get first price for comparison
        BigDecimal firstPrice = history.get(0).getPrice();

        // Calculate price change
        BigDecimal priceChange = currentPrice.subtract(firstPrice);

        // Calculate percentage change
        Double percentageChange = null;
        if (firstPrice.compareTo(BigDecimal.ZERO) > 0) {
            percentageChange = priceChange
                    .divide(firstPrice, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        // Check if at lowest price
        boolean isAtLowestPrice = currentPrice.compareTo(minPrice) <= 0;

        // Calculate savings from max
        BigDecimal savingsFromMax = maxPrice.subtract(currentPrice);

        return PriceAnalytics.builder()
                .productId(productId)
                .productName(product.getName())
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .avgPrice(avgPrice != null ? avgPrice.setScale(2, RoundingMode.HALF_UP) : null)
                .currentPrice(currentPrice)
                .priceChange(priceChange)
                .percentageChange(percentageChange)
                .recordCount(recordCount)
                .daysAnalyzed(days)
                .isAtLowestPrice(isAtLowestPrice)
                .savingsFromMax(savingsFromMax)
                .build();
    }
}
