package com.PriceTracker.demo.exception;

/**
 * Exception thrown when attempting to scrape an unsupported website.
 */
public class UnsupportedSiteException extends RuntimeException {

    private final String url;

    public UnsupportedSiteException(String url) {
        super("No scraper available for URL: " + url);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
