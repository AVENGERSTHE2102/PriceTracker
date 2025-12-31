package com.PriceTracker.demo.repositories;

import com.PriceTracker.demo.models.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for PriceHistory entity.
 * Provides methods for querying price history data.
 */
@Repository
public interface PriceHistoryRepo extends JpaRepository<PriceHistory, Long> {

    // Find all price history for a product, ordered by most recent first
    List<PriceHistory> findByProductIdOrderByScrapedAtDesc(Long productId);

    // Find price history within a date range
    @Query("SELECT ph FROM PriceHistory ph WHERE ph.product.id = :productId " +
            "AND ph.scrapedAt BETWEEN :startDate AND :endDate ORDER BY ph.scrapedAt ASC")
    List<PriceHistory> findByProductIdAndDateRange(
            @Param("productId") Long productId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Get the most recent price for a product
    Optional<PriceHistory> findTopByProductIdOrderByScrapedAtDesc(Long productId);

    // Get minimum price for a product
    @Query("SELECT MIN(ph.price) FROM PriceHistory ph WHERE ph.product.id = :productId")
    java.math.BigDecimal findMinPriceByProductId(@Param("productId") Long productId);

    // Get maximum price for a product
    @Query("SELECT MAX(ph.price) FROM PriceHistory ph WHERE ph.product.id = :productId")
    java.math.BigDecimal findMaxPriceByProductId(@Param("productId") Long productId);

    // Get average price for a product
    @Query("SELECT AVG(ph.price) FROM PriceHistory ph WHERE ph.product.id = :productId")
    java.math.BigDecimal findAvgPriceByProductId(@Param("productId") Long productId);

    // Count price records for a product
    long countByProductId(Long productId);
}
