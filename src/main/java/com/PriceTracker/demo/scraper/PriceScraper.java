package com.PriceTracker.demo.scraper;

import com.PriceTracker.demo.dto.ProductPrice;
import com.PriceTracker.demo.exception.ScrapingException;

/**
 * Interface for price scrapers.
 * Implements Strategy Pattern - each site has its own scraper implementation.
 */
public interface PriceScraper {

    /**
     * Scrape product price from the given URL.
     * 
     * @param url The product page URL
     * @return ProductPrice containing scraped data
     * @throws ScrapingException if scraping fails
     */
    ProductPrice scrape(String url) throws ScrapingException;

    /**
     * Check if this scraper supports the given URL.
     * 
     * @param url The URL to check
     * @return true if this scraper can handle the URL
     */
    boolean supports(String url);

    /**
     * Get the name of the site this scraper handles.
     * 
     * @return Site name (e.g., "Amazon", "Flipkart")
     */
    String getSiteName();
}
