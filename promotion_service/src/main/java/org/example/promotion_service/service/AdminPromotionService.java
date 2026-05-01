package org.example.promotion_service.service;

import org.example.promotion_service.dto.request.AssignBooksRequest;
import org.example.promotion_service.dto.request.CreatePromotionRequest;
import org.example.promotion_service.dto.request.UpdatePromotionRequest;
import org.example.promotion_service.dto.response.PromotionResponse;

import java.util.List;

public interface AdminPromotionService {
    PromotionResponse create(CreatePromotionRequest createPromotionRequest);
    PromotionResponse update(UpdatePromotionRequest updatePromotionRequest, String id);
    Void delete(String id);
    List<PromotionResponse> getAll();
    Void assignBooks(String promotionId, AssignBooksRequest assignBooksRequest);
    Void changeStatus(String promotionId, String status);
}
