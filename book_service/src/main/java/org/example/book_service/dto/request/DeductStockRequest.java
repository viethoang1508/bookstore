package org.example.book_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DeductStockRequest {
    @NotBlank(message = "Book ID is required")
    private String bookId;

    @Positive(message = "Quantity must be greater than 0")
    private Integer quantity;
}
