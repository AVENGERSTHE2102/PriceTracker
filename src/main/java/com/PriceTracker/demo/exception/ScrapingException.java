package com.PriceTracker.demo.exception;

/**
 * Exception thrown when web scraping fails.
 */
public class ScrapingException extends RuntimeException {

    private final String url;

    public ScrapingException(String message, String url) {
        super(message);
        this.url = url;
    }

    public ScrapingException(String message, String url, Throwable cause) {
        super(message, cause);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
