package com.PriceTracker.demo.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for adding a new product to track.
 * Contains validation constraints for input data.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {

    @NotBlank(message = "Product URL is required")
    @Pattern(regexp = "^https?://.*", message = "URL must start with http:// or https://")
    private String url;

    @Positive(message = "Target price must be positive")
    private BigDecimal targetPrice;

    @Pattern(regexp = "^(HOURLY|DAILY)$", message = "Scrape frequency must be HOURLY or DAILY")
    private String scrapeFrequency = "DAILY";

    @Email(message = "Invalid email format")
    private String alertEmail;
}
