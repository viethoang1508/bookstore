package org.example.promotion_service.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PromotionItemRequest {
    @NotBlank(message = "Book ID must not be blank")
    private String bookId;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0", message = "Price must be >= 0")
    private BigDecimal price;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be >= 1")
    private Integer quantity;

    @NotNull(message = "Sub-total is required")
    @DecimalMin(value = "0", message = "Sub-total must be >= 0")
    private BigDecimal subTotal;
}