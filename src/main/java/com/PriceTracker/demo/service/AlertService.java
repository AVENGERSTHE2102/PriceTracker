package com.PriceTracker.demo.service;

import com.PriceTracker.demo.models.Alert;
import com.PriceTracker.demo.models.ProductInfo;
import com.PriceTracker.demo.repositories.AlertRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing price alerts.
 * Checks price changes and triggers notifications.
 */
@Service
@Transactional
public class AlertService {

    private static final Logger log = LoggerFactory.getLogger(AlertService.class);

    // Threshold for significant price drop (percentage)
    private static final BigDecimal SIGNIFICANT_DROP_THRESHOLD = new BigDecimal("5");

    private final AlertRepo alertRepo;
    private final EmailService emailService;

    public AlertService(AlertRepo alertRepo, EmailService emailService) {
        this.alertRepo = alertRepo;
        this.emailService = emailService;
    }

    /**
     * Check price changes and trigger alerts if conditions are met.
     */
    public void checkAndTriggerAlerts(ProductInfo product, BigDecimal newPrice, BigDecimal oldPrice) {
        // Skip if no email configured for this product
        if (product.getAlertEmail() == null || product.getAlertEmail().isEmpty()) {
            return;
        }

        // Check if target price is reached
        if (product.getTargetPrice() != null &&
                newPrice.compareTo(product.getTargetPrice()) <= 0 &&
                (oldPrice == null || oldPrice.compareTo(product.getTargetPrice()) > 0)) {

            triggerTargetPriceAlert(product, newPrice, oldPrice);
        }

        // Check for significant price drop
        if (oldPrice != null && oldPrice.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal dropPercent = calculateDropPercent(oldPrice, newPrice);

            if (dropPercent.compareTo(SIGNIFICANT_DROP_THRESHOLD) >= 0) {
                triggerPriceDropAlert(product, newPrice, oldPrice, dropPercent.doubleValue());
            }
        }
    }

    /**
     * Calculate the percentage drop from old to new price.
     * Returns positive value if price dropped, negative if increased.
     */
    private BigDecimal calculateDropPercent(BigDecimal oldPrice, BigDecimal newPrice) {
        if (oldPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return oldPrice.subtract(newPrice)
                .divide(oldPrice, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * Trigger alert when target price is reached.
     */
    private void triggerTargetPriceAlert(ProductInfo product, BigDecimal newPrice, BigDecimal oldPrice) {
        log.info("Target price reached for {}: {} (target: {})",
                product.getName(), newPrice, product.getTargetPrice());

        Alert alert = new Alert();
        alert.setProduct(product);
        alert.setAlertType("TARGET_REACHED");
        alert.setTriggerPrice(newPrice);
        alert.setPreviousPrice(oldPrice);
        alert.setEmail(product.getAlertEmail());
        alert.setNotified(false);

        alertRepo.save(alert);

        // Send email notification
        try {
            emailService.sendTargetPriceAlert(
                    product.getAlertEmail(),
                    product,
                    newPrice);

            alert.setNotified(true);
            alert.setNotifiedAt(LocalDateTime.now());
            alertRepo.save(alert);

        } catch (Exception e) {
            log.error("Failed to send target price alert email for product {}", product.getName(), e);
        }
    }

    /**
     * Trigger alert for significant price drop.
     */
    private void triggerPriceDropAlert(ProductInfo product, BigDecimal newPrice,
            BigDecimal oldPrice, Double percentDrop) {
        log.info("Significant price drop for {}: {} -> {} ({:.1f}% drop)",
                product.getName(), oldPrice, newPrice, percentDrop);

        Alert alert = new Alert();
        alert.setProduct(product);
        alert.setAlertType("PRICE_DROP");
        alert.setTriggerPrice(newPrice);
        alert.setPreviousPrice(oldPrice);
        alert.setPercentageChange(percentDrop);
        alert.setEmail(product.getAlertEmail());
        alert.setNotified(false);

        alertRepo.save(alert);

        // Send email notification
        try {
            emailService.sendPriceDropAlert(
                    product.getAlertEmail(),
                    product,
                    newPrice,
                    oldPrice,
                    percentDrop);

            alert.setNotified(true);
            alert.setNotifiedAt(LocalDateTime.now());
            alertRepo.save(alert);

        } catch (Exception e) {
            log.error("Failed to send price drop alert email for product {}", product.getName(), e);
        }
    }

    /**
     * Get all alerts for a product.
     */
    @Transactional(readOnly = true)
    public List<Alert> getAlertsForProduct(Long productId) {
        return alertRepo.findByProductIdOrderByTriggeredAtDesc(productId);
    }

    /**
     * Get all unnotified alerts.
     */
    @Transactional(readOnly = true)
    public List<Alert> getUnnotifiedAlerts() {
        return alertRepo.findByNotifiedFalse();
    }
}
