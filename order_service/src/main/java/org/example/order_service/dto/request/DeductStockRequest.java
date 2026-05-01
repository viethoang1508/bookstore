package org.example.order_service.dto.request;

import lombok.Data;

@Data
public class DeductStockRequest {
    private String bookId;

    private String quantity;
}
