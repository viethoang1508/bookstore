package org.example.order_service.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ApplyPromotionRequest {
    private String code;
    private String userId;
    private BigDecimal totalAmount;
    private List<PromotionItemRequest> items;
}
