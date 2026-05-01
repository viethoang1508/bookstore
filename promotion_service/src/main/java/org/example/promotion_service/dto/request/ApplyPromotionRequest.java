package org.example.promotion_service.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ApplyPromotionRequest {
    @NotBlank(message = "Code must not be blank")
    private String code;

    @NotBlank(message = "User ID must not be blank")
    private String userId;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0", message = "Total amount must be >= 0")
    private BigDecimal totalAmount;

    @NotEmpty(message = "Items must not be empty")
    @Valid
    private List<PromotionItemRequest> items;
}