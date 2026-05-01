package org.example.order_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeductStockRequest {
    @NotBlank(message = "bookId must not be blank")
    private String bookId;

    @NotBlank(message = "quantity must not be blank")
    private String quantity;
}