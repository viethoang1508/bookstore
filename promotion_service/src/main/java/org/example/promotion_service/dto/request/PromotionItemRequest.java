package org.example.promotion_service.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PromotionItemRequest {
    private String bookId;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subTotal;
}
