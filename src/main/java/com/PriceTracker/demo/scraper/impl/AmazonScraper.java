package com.PriceTracker.demo.scraper.impl;

import com.PriceTracker.demo.dto.ProductPrice;
import com.PriceTracker.demo.exception.ScrapingException;
import com.PriceTracker.demo.scraper.PriceScraper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Scraper implementation for Amazon India and Amazon.com
 */
@Component
public class AmazonScraper implements PriceScraper {

    private static final Logger log = LoggerFactory.getLogger(AmazonScraper.class);

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    private static final int TIMEOUT_MS = 15000;

    @Override
    public ProductPrice scrape(String url) throws ScrapingException {
        log.info("Scraping Amazon URL: {}", url);

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT_MS)
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .header("Accept", "text/html,application/xhtml+xml")
                    .get();

            // Extract product title
            String title = extractTitle(doc);

            // Extract price
            BigDecimal price = extractPrice(doc);

            // Check availability
            Boolean available = checkAvailability(doc);

            // Determine currency based on URL
            String currency = url.contains("amazon.in") ? "INR" : "USD";

            log.info("Successfully scraped: {} - Price: {} {}", title, price, currency);

            return new ProductPrice(title, price, available, currency, LocalDateTime.now());

        } catch (Exception e) {
            log.error("Failed to scrape Amazon URL: {}", url, e);
            throw new ScrapingException("Failed to scrape Amazon product page", url, e);
        }
    }

    private String extractTitle(Document doc) {
        // Try multiple selectors for product title
        Element titleElement = doc.selectFirst("#productTitle");
        if (titleElement != null) {
            return titleElement.text().trim();
        }

        // Fallback to title tag
        Element title = doc.selectFirst("title");
        if (title != null) {
            String text = title.text();
            // Remove " - Amazon.in" or similar suffix
            int dashIndex = text.lastIndexOf("-");
            if (dashIndex > 0) {
                return text.substring(0, dashIndex).trim();
            }
            return text.trim();
        }

        return "Unknown Product";
    }

    private BigDecimal extractPrice(Document doc) throws ScrapingException {
        // Try multiple price selectors (Amazon changes these frequently)
        String[] priceSelectors = {
                ".a-price-whole", // Main price
                "#priceblock_ourprice", // Legacy price block
                "#priceblock_dealprice", // Deal price
                ".a-offscreen", // Hidden price
                "span[data-a-color='price'] .a-offscreen", // Price span
                "#corePrice_feature_div .a-offscreen", // Core price
                ".priceToPay .a-offscreen", // Price to pay
                "#apex_offerDisplay_desktop .a-offscreen" // Apex offer
        };

        for (String selector : priceSelectors) {
            Element priceElement = doc.selectFirst(selector);
            if (priceElement != null) {
                String priceText = priceElement.text();
                BigDecimal price = parsePrice(priceText);
                if (price != null && price.compareTo(BigDecimal.ZERO) > 0) {
                    return price;
                }
            }
        }

        throw new ScrapingException("Could not extract price from page", doc.baseUri());
    }

    private BigDecimal parsePrice(String priceText) {
        if (priceText == null || priceText.isEmpty()) {
            return null;
        }

        try {
            // Remove currency symbols, commas, and extra characters
            // Examples: "₹1,299.00", "$29.99", "1,299"
            String cleaned = priceText
                    .replaceAll("[₹$€£¥,\\s]", "") // Remove currency symbols and commas
                    .replaceAll("[^0-9.]", ""); // Keep only digits and decimal

            // Handle cases like "1299." or ".99"
            if (cleaned.endsWith(".")) {
                cleaned = cleaned.substring(0, cleaned.length() - 1);
            }
            if (cleaned.startsWith(".")) {
                cleaned = "0" + cleaned;
            }

            if (cleaned.isEmpty()) {
                return null;
            }

            return new BigDecimal(cleaned);
        } catch (NumberFormatException e) {
            log.warn("Could not parse price: {}", priceText);
            return null;
        }
    }

    private Boolean checkAvailability(Document doc) {
        // Check for out of stock indicators
        Element availability = doc.selectFirst("#availability");
        if (availability != null) {
            String text = availability.text().toLowerCase();
            if (text.contains("in stock")) {
                return true;
            }
            if (text.contains("out of stock") || text.contains("unavailable")) {
                return false;
            }
        }

        // Check for add to cart button as availability indicator
        Element addToCart = doc.selectFirst("#add-to-cart-button");
        return addToCart != null;
    }

    @Override
    public boolean supports(String url) {
        if (url == null)
            return false;
        String lowerUrl = url.toLowerCase();
        return lowerUrl.contains("amazon.in") ||
                lowerUrl.contains("amazon.com") ||
                lowerUrl.contains("amzn.in") ||
                lowerUrl.contains("amzn.com");
    }

    @Override
    public String getSiteName() {
        return "Amazon";
    }
}
