package com.PriceTracker.demo.service;

import com.PriceTracker.demo.models.ProductInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Service for sending email notifications.
 * Handles price alert emails.
 * Note: JavaMailSender is optional - if not configured, emails are logged only.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    // Optional injection - may be null if mail is not configured
    private final JavaMailSender mailSender;

    @Value("${app.email.from:pricepulse@example.com}")
    private String fromEmail;

    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;

    // Constructor with optional JavaMailSender
    @Autowired(required = false)
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
        if (mailSender == null) {
            log.info("JavaMailSender not configured - emails will be logged only");
        }
    }

    // Default constructor for when mail is not configured
    public EmailService() {
        this.mailSender = null;
        log.info("EmailService initialized without mail sender - emails will be logged only");
    }

    /**
     * Send email when target price is reached.
     */
    public void sendTargetPriceAlert(String to, ProductInfo product, BigDecimal currentPrice) {
        String subject = "🎯 Target Price Reached: " + product.getName();
        String body = buildTargetPriceBody(product, currentPrice);

        if (!emailEnabled || mailSender == null) {
            log.info("Email (simulated) to {}: {} - {}", to, subject, product.getName());
            log.debug("Email body:\n{}", body);
            return;
        }

        sendEmail(to, subject, body);
        log.info("Sent target price alert to {} for product {}", to, product.getName());
    }

    /**
     * Send email for significant price drop.
     */
    public void sendPriceDropAlert(String to, ProductInfo product, BigDecimal newPrice,
            BigDecimal oldPrice, Double percentDrop) {
        String subject = String.format("📉 Price Drop Alert: %s (%.1f%% off!)",
                product.getName(), percentDrop);
        String body = buildPriceDropBody(product, newPrice, oldPrice, percentDrop);

        if (!emailEnabled || mailSender == null) {
            log.info("Email (simulated) to {}: {} - {}", to, subject, product.getName());
            log.debug("Email body:\n{}", body);
            return;
        }

        sendEmail(to, subject, body);
        log.info("Sent price drop alert to {} for product {}", to, product.getName());
    }

    /**
     * Send a simple email.
     */
    private void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            throw e;
        }
    }

    /**
     * Build email body for target price alert.
     */
    private String buildTargetPriceBody(ProductInfo product, BigDecimal currentPrice) {
        return String.format("""
                Great news! 🎉

                The product you're tracking has reached your target price!

                📦 Product: %s
                🏪 Store: %s

                💰 Current Price: ₹%s
                🎯 Your Target: ₹%s

                This might be a good time to buy!

                🔗 Buy Now: %s

                ---
                PricePulse - Your Price Tracking Assistant
                """,
                product.getName(),
                product.getSourceSite(),
                currentPrice,
                product.getTargetPrice(),
                product.getProductUrl());
    }

    /**
     * Build email body for price drop alert.
     */
    private String buildPriceDropBody(ProductInfo product, BigDecimal newPrice,
            BigDecimal oldPrice, Double percentDrop) {
        BigDecimal savings = oldPrice.subtract(newPrice);

        return String.format("""
                Price Drop Alert! 📉

                A product you're tracking just got cheaper!

                📦 Product: %s
                🏪 Store: %s

                💰 New Price: ₹%s
                📊 Previous Price: ₹%s
                💸 You Save: ₹%s (%.1f%% off!)

                Don't miss out on this deal!

                🔗 Buy Now: %s

                ---
                PricePulse - Your Price Tracking Assistant
                """,
                product.getName(),
                product.getSourceSite(),
                newPrice,
                oldPrice,
                savings,
                percentDrop,
                product.getProductUrl());
    }
}
