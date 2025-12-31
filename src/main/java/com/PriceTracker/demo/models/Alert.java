package com.PriceTracker.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a price alert that was triggered.
 * Stores alert history and notification status.
 */
@Entity
@Table(name = "alerts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductInfo product;

    // Type of alert: PRICE_DROP, TARGET_REACHED, BACK_IN_STOCK
    @Column(nullable = false)
    private String alertType;

    // The price that triggered this alert
    @Column(precision = 10, scale = 2)
    private BigDecimal triggerPrice;

    // Previous price (for comparison)
    @Column(precision = 10, scale = 2)
    private BigDecimal previousPrice;

    // Percentage change (if applicable)
    private Double percentageChange;

    // Email address for notification
    private String email;

    // Whether the notification was sent
    private boolean notified = false;

    @CreationTimestamp
    private LocalDateTime triggeredAt;

    // When notification was sent
    private LocalDateTime notifiedAt;
}
