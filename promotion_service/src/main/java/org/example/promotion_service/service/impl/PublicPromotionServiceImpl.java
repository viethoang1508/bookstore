package org.example.promotion_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.promotion_service.dto.response.PromotionResponse;
import org.example.promotion_service.entity.Promotion;
import org.example.promotion_service.entity.PromotionStatus;
import org.example.promotion_service.exception.ApplicationException;
import org.example.promotion_service.mapper.PromotionMapper;
import org.example.promotion_service.repository.PromotionRepository;
import org.example.promotion_service.service.PublicPromotionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class PublicPromotionServiceImpl implements PublicPromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionMapper promotionMapper;

    @Override
    public List<PromotionResponse> getAll() {
        log.info("Fetching active public promotions");
        LocalDateTime now = LocalDateTime.now();

        return promotionRepository
                .findByStatusAndStartTimeBeforeAndEndTimeAfter(PromotionStatus.ACTIVE, now, now)
                .stream()
                .map(promotionMapper::toPromotionResponse)
                .toList();
    }

    @Override
    public PromotionResponse getByCode(String code) {
        log.info("Fetching promotion by code={}", code);
        if (code == null || code.isBlank()) {
            log.warn("Fetch promotion by code rejected because code is invalid");
            throw new ApplicationException("Invalid code");
        }
        Promotion promotion = promotionRepository.findByCode(code)
                .orElseThrow(() -> new ApplicationException("Promotion not found"));

        return promotionMapper.toPromotionResponse(promotion);
    }
}
