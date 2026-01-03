package com.PriceTracker.demo.exception;

/**
 * Exception thrown when a product is not found in the database.
 */
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long id) {
        super("Product not found with ID: " + id);
    }

    public ProductNotFoundException(String message) {
        super(message);
    }
}
