package com.PriceTracker.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a historical price record for a product.
 * Stores the price captured at a specific point in time.
 */
@Entity
@Table(name = "price_history", indexes = {
        @Index(name = "idx_product_scraped", columnList = "product_id, scrapedAt DESC")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductInfo product;

    // Price at the time of scraping - BigDecimal for monetary precision
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    // Product availability status
    private Boolean available;

    // Currency code (INR, USD, etc.)
    private String currency;

    @CreationTimestamp
    private LocalDateTime scrapedAt;

    // Constructor for convenience
    public PriceHistory(ProductInfo product, BigDecimal price, Boolean available, String currency) {
        this.product = product;
        this.price = price;
        this.available = available;
        this.currency = currency;
    }
}
