package com.PriceTracker.demo.repositories;

import com.PriceTracker.demo.models.ProductInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ProductInfo entity.
 * Provides CRUD operations and custom query methods.
 */
@Repository
public interface ProductRepo extends JpaRepository<ProductInfo, Long> {

    // Find product by its URL
    Optional<ProductInfo> findByProductUrl(String productUrl);

    // Find all active products
    List<ProductInfo> findByActiveTrue();

    // Find products by source site
    List<ProductInfo> findBySourceSite(String sourceSite);

    // Find active products with specific scrape frequency
    List<ProductInfo> findByActiveTrueAndScrapeFrequency(String scrapeFrequency);

    // Check if product URL already exists
    boolean existsByProductUrl(String productUrl);
}
