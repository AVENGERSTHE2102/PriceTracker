package com.PriceTracker.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for product API responses.
 * Contains product info with latest price data.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String sourceSite;
    private String productUrl;
    private String scrapeFrequency;
    private BigDecimal targetPrice;
    private BigDecimal currentPrice;
    private String alertEmail;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Additional computed fields
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal avgPrice;
    private Long priceRecordCount;
}
