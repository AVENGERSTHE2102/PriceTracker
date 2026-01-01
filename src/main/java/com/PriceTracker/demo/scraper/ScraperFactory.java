package com.PriceTracker.demo.scraper;

import com.PriceTracker.demo.exception.UnsupportedSiteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Factory for obtaining the appropriate scraper for a given URL.
 * Uses Spring's automatic injection of all PriceScraper implementations.
 */
@Component
public class ScraperFactory {

    private static final Logger log = LoggerFactory.getLogger(ScraperFactory.class);

    private final List<PriceScraper> scrapers;

    /**
     * Constructor injection of all PriceScraper beans.
     * Spring automatically injects all implementations of PriceScraper.
     */
    public ScraperFactory(List<PriceScraper> scrapers) {
        this.scrapers = scrapers;
        log.info("Initialized ScraperFactory with {} scrapers: {}",
                scrapers.size(),
                scrapers.stream().map(PriceScraper::getSiteName).toList());
    }

    /**
     * Get the appropriate scraper for the given URL.
     * 
     * @param url The product URL
     * @return The scraper that can handle this URL
     * @throws UnsupportedSiteException if no scraper supports the URL
     */
    public PriceScraper getScraperForUrl(String url) {
        return scrapers.stream()
                .filter(scraper -> scraper.supports(url))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("No scraper found for URL: {}", url);
                    return new UnsupportedSiteException(url);
                });
    }

    /**
     * Check if any scraper supports the given URL.
     * 
     * @param url The URL to check
     * @return true if a scraper exists for this URL
     */
    public boolean isSupported(String url) {
        return scrapers.stream().anyMatch(scraper -> scraper.supports(url));
    }

    /**
     * Get the site name for a URL without throwing exception.
     * 
     * @param url The URL
     * @return Site name or "Unknown" if not supported
     */
    public String getSiteNameForUrl(String url) {
        return scrapers.stream()
                .filter(scraper -> scraper.supports(url))
                .findFirst()
                .map(PriceScraper::getSiteName)
                .orElse("Unknown");
    }

    /**
     * Get list of all supported site names.
     * 
     * @return List of site names
     */
    public List<String> getSupportedSites() {
        return scrapers.stream()
                .map(PriceScraper::getSiteName)
                .toList();
    }
}
