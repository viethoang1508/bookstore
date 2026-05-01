package org.example.order_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderItemRequest {
    @NotBlank(message = "bookId must not be blank")
    private String bookId;

    @NotNull(message = "quantity must not be null")
    @Min(value = 1, message = "quantity must be greater than or equal to 1")
    private Integer quantity;
}