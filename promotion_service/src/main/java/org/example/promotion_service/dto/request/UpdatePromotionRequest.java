package org.example.promotion_service.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UpdatePromotionRequest {
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;

    private String scope;

    @Pattern(regexp = "PERCENT|FIXED", message = "Type must be PERCENT or FIXED")
    private String type;

    @DecimalMin(value = "0", inclusive = false, message = "Value must be greater than 0")
    private BigDecimal value;

    @DecimalMin(value = "0", message = "Max discount must be >= 0")
    private BigDecimal maxDiscount;
    @DecimalMin(value = "0", message = "Min order value must be >= 0")
    private BigDecimal minOrderValue;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Pattern(regexp = "ACTIVE|INACTIVE", message = "Status must be ACTIVE or INACTIVE")
    private String status; // ACTIVE | INACTIVE
}