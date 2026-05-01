package org.example.promotion_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.promotion_service.dto.request.ApplyPromotionRequest;
import org.example.promotion_service.dto.request.PromotionItemRequest;
import org.example.promotion_service.dto.response.ApplyPromotionResponse;
import org.example.promotion_service.dto.response.DiscountDetail;
import org.example.promotion_service.entity.Promotion;
import org.example.promotion_service.entity.PromotionBook;
import org.example.promotion_service.entity.PromotionStatus;
import org.example.promotion_service.entity.PromotionType;
import org.example.promotion_service.exception.ApplicationException;
import org.example.promotion_service.repository.PromotionBookRepository;
import org.example.promotion_service.repository.PromotionRepository;
import org.example.promotion_service.service.InternalPromotionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InternalPromotionServiceImpl implements InternalPromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionBookRepository promotionBookRepository;

    @Override
    public ApplyPromotionResponse applyPromotion(ApplyPromotionRequest request) {
        // Validate request
        if (request == null || request.getCode() == null) {
            throw new RuntimeException("Invalid promotion request");
        }

        // Tìm promotion
        Promotion promotion = promotionRepository.findByCode(request.getCode())
                .orElseThrow(() -> new ApplicationException("Promotion with code " + request.getCode() + " not found"));

        // Validate
        validatePromotion(promotion, request);

        // Chia trường hợp theo scope
        String scope = promotion.getScope();
        if (scope == "ITEM") {
            ApplyPromotionResponse response = applyOrderPromotion(request, promotion);
        } else if (scope == "ORDER") {
            ApplyPromotionResponse response = applyItemsPromotion(request, promotion);
        } else {
            throw new ApplicationException("Invalid scope " + scope);
        }


    }

    // VALIDATE PROMOTION
    private void validatePromotion(ApplyPromotionRequest request, Promotion promotion) {

        // Kiểm tra ACTIVE
        if (promotion.getStatus().equals(PromotionStatus.INACTIVE)) {
            throw new ApplicationException("Promotion with code " + promotion.getCode() + " is not active.");
        }

        // Kiểm tra thười gian
        LocalDateTime now = LocalDateTime.now();

        if (promotion.getStartTime() != null && now.isBefore(promotion.getStartTime())) {
            throw new ApplicationException("Promotion with code " + promotion.getCode() + " not started.");
        }

        if (promotion.getEndTime() != null && now.isAfter(promotion.getEndTime())) {
            throw new ApplicationException("Promotion with code " + promotion.getCode() + " is ended.");
        }

        // min order
        if (promotion.getMinOrderValue() != null &&
                request.getTotalAmount().compareTo(promotion.getMinOrderValue()) < 0) {

            throw new RuntimeException("Order does not meet minimum amount");
        }
    }

    // TÍNH DISCOUNT THEO ORDER
    public ApplyPromotionResponse applyOrderPromotion(ApplyPromotionRequest request, Promotion promotion) {

        BigDecimal maxDiscountAmount = promotion.getMaxDiscount();

        BigDecimal discountAmount = BigDecimal.ZERO;

        if (promotion.getType().equals(PromotionType.PERCENT)) {
            BigDecimal value = promotion.getValue();
            BigDecimal discountAmount = value.divide(100).multiply(request.getTotalAmount());
            if (maxDiscountAmount != null &&
                    maxDiscountAmount.compareTo(discountAmount) < 0) {
                discountAmount = maxDiscountAmount;
            }
        } else if (promotion.getType().equals(PromotionType.FIXED)) {
            BigDecimal value = promotion.getValue();
            BigDecimal discountAmount = value;
            if (maxDiscountAmount != null &&
                    maxDiscountAmount.compareTo(discountAmount) < 0) {
                discountAmount = maxDiscountAmount;
            }
        }

        BigDecimal finalAmount = request.getTotalAmount() - discountAmount;

        ApplyPromotionResponse response = new ApplyPromotionResponse();
        response.setFinalAmount(finalAmount);
        response.setDiscountAmount(discountAmount);
        response.setPromotionCode(promotion.getCode());
        response.setDescription("Get discount by " + promotion.getScope() + " and get discount by " + discountAmount);

        return response;
    }

    // TÍNH DISCOUNT THEO TỪNG ITEMS
    public ApplyPromotionResponse applyItemsPromotion(ApplyPromotionRequest request, Promotion promotion) {

        // Lấy danh sách items trong request
        List<PromotionItemRequest> promoItemsList = request.getItems();

        if (promoItemsList == null || promoItemsList.isEmpty()) {
            throw new RuntimeException("Items cannot be empty");
        }

        // Lấy những sách được giảm theo promotion
        String promotionId = promotion.getId();
        List<PromotionBook> promotionBookList = promotionBookRepository.findByPromotionId(promotionId);

        Set<String> promotedBookIdsSet = promotionBookList.stream()
                .map(PromotionBook::getBookId)
                .collect(Collectors.toSet());

        List<DiscountDetail> discountDetails = new ArrayList<>();

        BigDecimal discountAmount = BigDecimal.ZERO;

        for (PromotionItemRequest item : promoItemsList) {

            String bookId = item.getBookId();

            // Kiểm tra có được apply không
            if(!promotedBookIdsSet.contains(bookId)) {
                continue;
            }

            BigDecimal itemDiscount = BigDecimal.ZERO;

            BigDecimal subTotal = item.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

            switch (promotion.getType()) {
                case PERCENT:
                    itemDiscount = subTotal
                            .multiply(promotion.getValue())
                            .divide(BigDecimal.valueOf(100));
                    break;

                case FIXED:
                    itemDiscount = promotion.getValue()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));
                    break;
            }

            // Chặn discount > subtotal
            if (itemDiscount.compareTo(subTotal) > 0) {
                itemDiscount = subTotal;
            }

            itemDiscount = itemDiscount.setScale(2, RoundingMode.HALF_UP);

            DiscountDetail discountDetail = new DiscountDetail();
            discountDetail.setBookId(bookId);
            discountDetail.setSubDiscountAmount(itemDiscount);

            discountDetails.add(discountDetail);

            discountAmount = discountAmount.add(itemDiscount);

        }

        // Kiểm tra max discount
        BigDecimal maxDiscountAmount = promotion.getMaxDiscount();

        boolean isCapped = false;

        if (maxDiscountAmount != null &&
                discountAmount.compareTo(maxDiscountAmount) > 0) {
            discountAmount = maxDiscountAmount;
            isCapped = true;
            discountDetails = null;
        }

        BigDecimal finalAmount = request.getTotalAmount()
                .subtract(discountAmount);

        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }

        ApplyPromotionResponse response = new ApplyPromotionResponse();
        response.setDiscountAmount(discountAmount);
        response.setPromotionCode(promotion.getCode());
        response.setFinalAmount(finalAmount);

        if (isCapped) {
            response.setDescription("Reach max discount: " + maxDiscountAmount);
        } else if (discountDetails.isEmpty()) {
            response.setDescription("No books is applied for discount");
        } else {
            response.setDescription("Discounted " + discountAmount + " VND");
        }

        response.setDiscountDetails(discountDetails);

        return response;
    }
}
