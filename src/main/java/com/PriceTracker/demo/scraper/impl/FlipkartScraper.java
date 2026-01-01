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
 * Scraper implementation for Flipkart.com
 */
@Component
public class FlipkartScraper implements PriceScraper {

    private static final Logger log = LoggerFactory.getLogger(FlipkartScraper.class);

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    private static final int TIMEOUT_MS = 15000;

    @Override
    public ProductPrice scrape(String url) throws ScrapingException {
        log.info("Scraping Flipkart URL: {}", url);

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT_MS)
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .get();

            // Extract product title
            String title = extractTitle(doc);

            // Extract price
            BigDecimal price = extractPrice(doc);

            // Check availability
            Boolean available = checkAvailability(doc);

            log.info("Successfully scraped: {} - Price: {} INR", title, price);

            return new ProductPrice(title, price, available, "INR", LocalDateTime.now());

        } catch (Exception e) {
            log.error("Failed to scrape Flipkart URL: {}", url, e);
            throw new ScrapingException("Failed to scrape Flipkart product page", url, e);
        }
    }

    private String extractTitle(Document doc) {
        // Try multiple selectors for product title
        String[] titleSelectors = {
                "span.B_NuCI", // Main product title
                "h1.yhB1nd", // Alternative title
                ".G6XhRU", // Product name container
                "h1._9E25nV", // Another variant
                "span._35KyD6" // Mobile view title
        };

        for (String selector : titleSelectors) {
            Element titleElement = doc.selectFirst(selector);
            if (titleElement != null) {
                String title = titleElement.text().trim();
                if (!title.isEmpty()) {
                    return title;
                }
            }
        }

        // Fallback to title tag
        Element title = doc.selectFirst("title");
        if (title != null) {
            String text = title.text();
            // Remove ": Buy ... - Flipkart.com" suffix
            int colonIndex = text.indexOf(":");
            if (colonIndex > 0) {
                return text.substring(0, colonIndex).trim();
            }
            return text.replace("- Flipkart.com", "").trim();
        }

        return "Unknown Product";
    }

    private BigDecimal extractPrice(Document doc) throws ScrapingException {
        // Try multiple price selectors (Flipkart changes these frequently)
        String[] priceSelectors = {
                "div._30jeq3._16Jk6d", // Main discounted price
                "div._30jeq3", // Alternative price class
                "div._16Jk6d", // Just discount price
                "span._2I-_Kd._30jeq3", // Price span variant
                "div[class*='_30jeq3']", // Partial class match
                "meta[itemprop='price']" // Schema.org price
        };

        for (String selector : priceSelectors) {
            Element priceElement = doc.selectFirst(selector);
            if (priceElement != null) {
                String priceText;
                if (selector.contains("meta")) {
                    priceText = priceElement.attr("content");
                } else {
                    priceText = priceElement.text();
                }

                BigDecimal price = parsePrice(priceText);
                if (price != null && price.compareTo(BigDecimal.ZERO) > 0) {
                    return price;
                }
            }
        }

        throw new ScrapingException("Could not extract price from Flipkart page", doc.baseUri());
    }

    private BigDecimal parsePrice(String priceText) {
        if (priceText == null || priceText.isEmpty()) {
            return null;
        }

        try {
            // Remove rupee symbol, commas, and extra characters
            // Examples: "₹1,299", "₹ 29,999.00"
            String cleaned = priceText
                    .replaceAll("[₹,\\s]", "") // Remove rupee symbol and commas
                    .replaceAll("[^0-9.]", ""); // Keep only digits and decimal

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
        Element notifyButton = doc.selectFirst("button._2KpZ6l._2ObVJD");
        if (notifyButton != null && notifyButton.text().toLowerCase().contains("notify")) {
            return false;
        }

        // Check if "Add to Cart" or "Buy Now" button exists
        Element buyButton = doc.selectFirst("button._2KpZ6l._2U9uOA._3v1-ww");
        Element addToCart = doc.selectFirst("button._2KpZ6l._2U9uOA.ihZ75k._3AWRsL");

        return buyButton != null || addToCart != null;
    }

    @Override
    public boolean supports(String url) {
        if (url == null)
            return false;
        String lowerUrl = url.toLowerCase();
        return lowerUrl.contains("flipkart.com") ||
                lowerUrl.contains("fkrt.it"); // Short URL
    }

    @Override
    public String getSiteName() {
        return "Flipkart";
    }
}
