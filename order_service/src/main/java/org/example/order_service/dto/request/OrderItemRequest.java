package org.example.order_service.dto.request;

import lombok.Data;

@Data
public class OrderItemRequest {
    private String bookId;
    private Integer quantity;
}
