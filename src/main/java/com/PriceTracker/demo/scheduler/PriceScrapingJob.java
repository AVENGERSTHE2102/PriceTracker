package com.PriceTracker.demo.scheduler;

import com.PriceTracker.demo.models.ProductInfo;
import com.PriceTracker.demo.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Scheduled job for automatic price scraping.
 * Runs at configured intervals to update product prices.
 */
@Component
public class PriceScrapingJob {

    private static final Logger log = LoggerFactory.getLogger(PriceScrapingJob.class);

    private final ProductService productService;

    public PriceScrapingJob(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Scrape hourly products.
     * Runs every hour at minute 0.
     */
    @Scheduled(cron = "0 0 * * * *") // Every hour at :00
    public void scrapeHourlyProducts() {
        log.info("Starting hourly price scraping job at {}", LocalDateTime.now());

        List<ProductInfo> products = productService.getProductsByFrequency("HOURLY");

        if (products.isEmpty()) {
            log.info("No hourly products to scrape");
            return;
        }

        scrapeProducts(products, "HOURLY");
    }

    /**
     * Scrape daily products.
     * Runs once per day at 6:00 AM.
     */
    @Scheduled(cron = "0 0 6 * * *") // Every day at 6:00 AM
    public void scrapeDailyProducts() {
        log.info("Starting daily price scraping job at {}", LocalDateTime.now());

        List<ProductInfo> products = productService.getProductsByFrequency("DAILY");

        if (products.isEmpty()) {
            log.info("No daily products to scrape");
            return;
        }

        scrapeProducts(products, "DAILY");
    }

    /**
     * Scrape a list of products with error handling.
     */
    private void scrapeProducts(List<ProductInfo> products, String frequency) {
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        log.info("Scraping {} {} products", products.size(), frequency);

        // Process products in parallel using stream
        products.parallelStream().forEach(product -> {
            try {
                productService.updateProductPrice(product.getId());
                successCount.incrementAndGet();
                log.debug("Successfully scraped: {}", product.getName());
            } catch (Exception e) {
                failCount.incrementAndGet();
                log.error("Failed to scrape product {} (ID: {}): {}",
                        product.getName(), product.getId(), e.getMessage());
            }
        });

        log.info("Completed {} scraping job. Success: {}, Failed: {}",
                frequency, successCount.get(), failCount.get());
    }
}
