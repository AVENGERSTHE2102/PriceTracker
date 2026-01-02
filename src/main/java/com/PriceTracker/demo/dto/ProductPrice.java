package com.PriceTracker.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for scraped price data.
 * Returned by scraper implementations.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPrice {
    private String productName;
    private BigDecimal price;
    private Boolean available;
    private String currency;
    private LocalDateTime scrapedAt;

    public ProductPrice(String productName, BigDecimal price, Boolean available, String currency) {
        this.productName = productName;
        this.price = price;
        this.available = available;
        this.currency = currency;
        this.scrapedAt = LocalDateTime.now();
    }
}
