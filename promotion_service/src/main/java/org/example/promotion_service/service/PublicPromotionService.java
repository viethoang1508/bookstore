package org.example.promotion_service.service;

import org.example.promotion_service.dto.response.PromotionResponse;

import java.util.List;

public interface PublicPromotionService {
    List<PromotionResponse> getAll();
    PromotionResponse getByCode(String code);

}
