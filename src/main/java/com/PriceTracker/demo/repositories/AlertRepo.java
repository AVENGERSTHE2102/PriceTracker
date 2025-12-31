package com.PriceTracker.demo.repositories;

import com.PriceTracker.demo.models.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Alert entity.
 * Provides methods for querying alert data.
 */
@Repository
public interface AlertRepo extends JpaRepository<Alert, Long> {

    // Find all alerts for a product
    List<Alert> findByProductIdOrderByTriggeredAtDesc(Long productId);

    // Find unnotified alerts
    List<Alert> findByNotifiedFalse();

    // Find alerts by type
    List<Alert> findByAlertType(String alertType);

    // Find alerts by email
    List<Alert> findByEmail(String email);
}
