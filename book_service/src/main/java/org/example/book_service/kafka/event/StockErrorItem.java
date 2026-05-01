package org.example.book_service.kafka.event;

import lombok.Data;

@Data
public class StockErrorItem {
    private String bookId;
    private Integer requestedQty;
    private Integer availableStock;
}
