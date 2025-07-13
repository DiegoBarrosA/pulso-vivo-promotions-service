package com.pulsovivo.promotions.service;

import com.pulsovivo.promotions.dto.PromotionRequest;
import com.pulsovivo.promotions.dto.PromotionResponse;
import com.pulsovivo.promotions.model.Promotion;
import com.pulsovivo.promotions.repository.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private KafkaAnalyticsService kafkaAnalyticsService;

    public PromotionResponse generatePromotion(PromotionRequest request) {
        // Get analytics data from Kafka consumers
        var salesData = kafkaAnalyticsService.getSalesDataForProduct(request.getProductId());
        var stockData = kafkaAnalyticsService.getStockDataForProduct(request.getProductId());

        // Generate promotion based on sales and stock data
        Promotion promotion = new Promotion();
        promotion.setId(UUID.randomUUID().toString());
        promotion.setProductId(request.getProductId());
        promotion.setPromotionType(determinePromotionType(salesData, stockData));
        promotion.setDiscountPercentage(calculateDiscountPercentage(salesData, stockData));
        promotion.setDescription(generatePromotionDescription(promotion));
        promotion.setStartDate(LocalDateTime.now());
        promotion.setEndDate(LocalDateTime.now().plusDays(7)); // 7 days promotion
        promotion.setActive(true);
        promotion.setCreatedAt(LocalDateTime.now());

        promotion = promotionRepository.save(promotion);

        return convertToResponse(promotion);
    }

    public List<PromotionResponse> getActivePromotions() {
        List<Promotion> promotions = promotionRepository.findByActiveTrue();
        return promotions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<PromotionResponse> getPromotionsByProduct(String productId) {
        List<Promotion> promotions = promotionRepository.findByProductIdAndActiveTrue(productId);
        return promotions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private String determinePromotionType(Object salesData, Object stockData) {
        // Logic to determine promotion type based on sales and stock data
        // This could be: CLEARANCE, VOLUME_DISCOUNT, SEASONAL, etc.
        return "DYNAMIC_DISCOUNT";
    }

    private Double calculateDiscountPercentage(Object salesData, Object stockData) {
        // Logic to calculate discount based on analytics
        // For now, return a simple calculation
        return 15.0; // 15% discount
    }

    private String generatePromotionDescription(Promotion promotion) {
        return String.format("Special %s promotion - %.0f%% off!",
                promotion.getPromotionType(),
                promotion.getDiscountPercentage());
    }

    private PromotionResponse convertToResponse(Promotion promotion) {
        PromotionResponse response = new PromotionResponse();
        response.setId(promotion.getId());
        response.setProductId(promotion.getProductId());
        response.setPromotionType(promotion.getPromotionType());
        response.setDiscountPercentage(promotion.getDiscountPercentage());
        response.setDescription(promotion.getDescription());
        response.setStartDate(promotion.getStartDate());
        response.setEndDate(promotion.getEndDate());
        response.setActive(promotion.getActive());
        return response;
    }
}
