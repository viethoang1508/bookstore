package org.example.promotion_service.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreatePromotionRequest {
    @NotBlank(message = "Code must not be blank")
    @Size(max = 50, message = "Code must be less than 50 characters")
    private String code;

    @NotBlank(message = "Name must not be blank")
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;

    @NotBlank(message = "Scope must not be blank")
    private String scope;

    @NotBlank(message = "Type must not be blank")
    @Pattern(regexp = "PERCENT|FIXED", message = "Type must be PERCENT or FIXED")
    private String type;

    @NotNull(message = "Value is required")
    @DecimalMin(value = "0", inclusive = false, message = "Value must be greater than 0")
    private BigDecimal value;

    @DecimalMin(value = "0", message = "Max discount must be >= 0")
    private BigDecimal maxDiscount;

    @DecimalMin(value = "0", message = "Min order value must be >= 0")
    private BigDecimal minOrderValue;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;
}
