package com.PriceTracker.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a product being tracked for price changes.
 * Each product has a URL, target price, and scraping frequency.
 */
@Entity
@Table(name = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String sourceSite;

    @Column(unique = true, nullable = false)
    private String productUrl;

    // HOURLY or DAILY
    private String scrapeFrequency;

    // Target price for alerts - uses BigDecimal for monetary precision
    @Column(precision = 10, scale = 2)
    private BigDecimal targetPrice;

    // Current/latest price
    @Column(precision = 10, scale = 2)
    private BigDecimal currentPrice;

    // Email for price alerts
    private String alertEmail;

    // Whether this product is actively being tracked
    private boolean active = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
