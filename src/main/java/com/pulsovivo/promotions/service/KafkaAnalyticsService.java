package com.pulsovivo.promotions.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class KafkaAnalyticsService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaAnalyticsService.class);

    // In-memory storage for demo purposes - in production use Redis or Database
    private final Map<String, Object> salesAnalytics = new ConcurrentHashMap<>();
    private final Map<String, Object> stockAnalytics = new ConcurrentHashMap<>();

    @KafkaListener(topics = "ventas", groupId = "promotions-group")
    public void consumeVentasMessage(String message) {
        logger.info("Received ventas message: {}", message);

        // Process sales message and update analytics
        // In a real implementation, you would parse the JSON message
        // and extract product ID, quantity, price, etc.

        // For demo purposes, store the raw message
        String key = "sales_" + System.currentTimeMillis();
        salesAnalytics.put(key, message);

        // Keep only recent data (last 1000 entries)
        if (salesAnalytics.size() > 1000) {
            String oldestKey = salesAnalytics.keySet().iterator().next();
            salesAnalytics.remove(oldestKey);
        }
    }

    @KafkaListener(topics = "stock", groupId = "promotions-group")
    public void consumeStockMessage(String message) {
        logger.info("Received stock message: {}", message);

        // Process stock message and update analytics
        // In a real implementation, you would parse the JSON message
        // and extract product ID, stock level, etc.

        // For demo purposes, store the raw message
        String key = "stock_" + System.currentTimeMillis();
        stockAnalytics.put(key, message);

        // Keep only recent data (last 1000 entries)
        if (stockAnalytics.size() > 1000) {
            String oldestKey = stockAnalytics.keySet().iterator().next();
            stockAnalytics.remove(oldestKey);
        }
    }

    public Object getSalesDataForProduct(String productId) {
        // In a real implementation, filter by product ID
        // For demo, return general sales analytics
        return salesAnalytics;
    }

    public Object getStockDataForProduct(String productId) {
        // In a real implementation, filter by product ID
        // For demo, return general stock analytics
        return stockAnalytics;
    }

    public Map<String, Object> getAllSalesAnalytics() {
        return new ConcurrentHashMap<>(salesAnalytics);
    }

    public Map<String, Object> getAllStockAnalytics() {
        return new ConcurrentHashMap<>(stockAnalytics);
    }
}
