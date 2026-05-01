package org.example.promotion_service.service;

import org.example.promotion_service.dto.request.ApplyPromotionRequest;
import org.example.promotion_service.dto.response.ApplyPromotionResponse;

public interface InternalPromotionService {
    ApplyPromotionResponse applyPromotion(ApplyPromotionRequest request);
}
