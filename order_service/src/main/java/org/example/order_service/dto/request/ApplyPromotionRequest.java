package org.example.order_service.dto.request;

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
    @NotBlank(message = "code must not be blank")
    private String code;

    @NotBlank(message = "userId must not be blank")
    private String userId;

    @NotNull(message = "totalAmount must not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "totalAmount must be greater than 0")
    private BigDecimal totalAmount;

    @NotEmpty(message = "items must not be empty")
    @Valid
    private List<PromotionItemRequest> items;
}