package com.PriceTracker.demo.controller;

import com.PriceTracker.demo.models.Alert;
import com.PriceTracker.demo.service.AlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for alert management.
 */
@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    /**
     * Get all alerts for a product.
     * GET /api/alerts/product/{productId}
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Alert>> getAlertsForProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(alertService.getAlertsForProduct(productId));
    }

    /**
     * Get all unnotified alerts.
     * GET /api/alerts/pending
     */
    @GetMapping("/pending")
    public ResponseEntity<List<Alert>> getPendingAlerts() {
        return ResponseEntity.ok(alertService.getUnnotifiedAlerts());
    }
}
