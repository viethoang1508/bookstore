package org.example.promotion_service.controller;

import lombok.RequiredArgsConstructor;
import org.example.promotion_service.dto.BaseResponse;
import org.example.promotion_service.dto.request.ApplyPromotionRequest;
import org.example.promotion_service.dto.response.ApplyPromotionResponse;
import org.example.promotion_service.service.InternalPromotionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("internal/promotions")
public class InternalPromotionController {

    private final InternalPromotionService internalPromotionService;

    @PostMapping("/apply")
    public ResponseEntity<BaseResponse<ApplyPromotionResponse>> applyPromotion(@RequestBody ApplyPromotionRequest applyPromotionRequest) {
        return ResponseEntity.ok(new BaseResponse<>(internalPromotionService.applyPromotion(applyPromotionRequest), "Apply promotion successfully"));
    }
}
