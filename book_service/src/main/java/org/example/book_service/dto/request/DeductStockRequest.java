package org.example.book_service.dto.request;

import lombok.Data;

@Data
public class DeductStockRequest {
    private String bookId;
    private Integer quantity;
}
