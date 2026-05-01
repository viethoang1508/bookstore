package org.example.order_service.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ApplyPromotionResponse {
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String promotionCode;
    private String description;
    private List<DiscountDetail> discountDetais;
}
