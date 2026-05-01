package org.example.promotion_service.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DiscountDetail {
    private String bookId;
    private BigDecimal subDiscountAmount;
}
