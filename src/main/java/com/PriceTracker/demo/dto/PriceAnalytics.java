package com.PriceTracker.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for price analytics data.
 * Contains min, max, average prices and price change information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PriceAnalytics {
    private Long productId;
    private String productName;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal avgPrice;
    private BigDecimal currentPrice;
    private BigDecimal priceChange; // Current - First price
    private Double percentageChange; // Percentage change from first price
    private Long recordCount;
    private Integer daysAnalyzed;

    // Best time to buy indicator
    private boolean isAtLowestPrice;
    private BigDecimal savingsFromMax; // How much below max price
}
