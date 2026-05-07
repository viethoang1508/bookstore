package org.example.promotion_service.controller;

import lombok.RequiredArgsConstructor;
import org.example.promotion_service.dto.response.BaseResponse;
import org.example.promotion_service.dto.response.PromotionResponse;
import org.example.promotion_service.entity.BaseEntity;
import org.example.promotion_service.service.PublicPromotionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/public/promotions")
@RequiredArgsConstructor
public class PublicPromotionController {

    private final PublicPromotionService publicPromotionService;

    // Lấy danh sách promotions
    @GetMapping
    public ResponseEntity<BaseResponse<List<PromotionResponse>>> getAll() {
        return ResponseEntity.ok(new BaseResponse<>(publicPromotionService.getAll(), "Get all promotions successfully"));
    }

    // Lấy promotion theo code
    @GetMapping("/{code}")
    public ResponseEntity<BaseResponse<PromotionResponse>> getByCode(@PathVariable String promotionCode) {
        return ResponseEntity.ok(new BaseResponse<>(publicPromotionService.getByCode(promotionCode), "Get promotion successfully"));
    }
}
