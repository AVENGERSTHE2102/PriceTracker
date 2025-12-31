package com.PriceTracker.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Configuration for scheduled tasks and thread pool.
 * Enables Spring's scheduling infrastructure.
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {

    private static final Logger log = LoggerFactory.getLogger(SchedulerConfig.class);

    @Value("${app.scheduler.pool-size:10}")
    private int poolSize;

    /**
     * Configure the task scheduler with a thread pool.
     * This allows multiple scraping tasks to run concurrently.
     */
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

        // Number of threads for concurrent scraping
        scheduler.setPoolSize(poolSize);

        // Thread name prefix for debugging
        scheduler.setThreadNamePrefix("price-scraper-");

        // Handle errors gracefully
        scheduler.setErrorHandler(
                throwable -> log.error("Error in scheduled task: {}", throwable.getMessage(), throwable));

        // Wait for tasks to complete on shutdown
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(30);

        log.info("Initialized task scheduler with pool size: {}", poolSize);

        return scheduler;
    }
}
