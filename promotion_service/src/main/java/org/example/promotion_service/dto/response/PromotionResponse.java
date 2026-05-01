package org.example.promotion_service.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PromotionResponse {
    private String id;
    private String code;
    private String name;
    private String scope;

    private String type;
    private BigDecimal value;

    private BigDecimal maxDiscount;
    private BigDecimal minOrderValue;

    private String status;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
