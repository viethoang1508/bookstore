package org.example.order_service.client;

import org.example.order_service.dto.request.ApplyPromotionRequest;
import org.example.order_service.dto.response.ApplyPromotionResponse;

public interface PromotionClient {
    ApplyPromotionResponse applyPromotion(ApplyPromotionRequest request);
}
