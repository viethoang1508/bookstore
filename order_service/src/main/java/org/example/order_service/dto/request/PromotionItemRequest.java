package org.example.order_service.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PromotionItemRequest {
    @NotBlank(message = "bookId must not be blank")
    private String bookId;

    @NotNull(message = "price must not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "quantity must not be null")
    @Min(value = 1, message = "quantity must be greater than or equal to 1")
    private Integer quantity;

    @NotNull(message = "subTotal must not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "subTotal must be greater than 0")
    private BigDecimal subTotal;
}