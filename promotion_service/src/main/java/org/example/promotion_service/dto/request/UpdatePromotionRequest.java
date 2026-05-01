package org.example.promotion_service.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UpdatePromotionRequest {
    private String name;
    private String scope;
    private String type;
    private BigDecimal value;

    private BigDecimal maxDiscount;
    private BigDecimal minOrderValue;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String status; // ACTIVE | INACTIVE
}
