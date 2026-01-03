package com.PriceTracker.demo.exception;

/**
 * Exception thrown when attempting to add a product that already exists.
 */
public class DuplicateProductException extends RuntimeException {

    private final String url;

    public DuplicateProductException(String url) {
        super("Product already exists with URL: " + url);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
