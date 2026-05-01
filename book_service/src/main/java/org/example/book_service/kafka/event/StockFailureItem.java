package org.example.book_service.kafka.event;

import lombok.Data;

@Data
public class StockFailureItem {
    private String bookId;
    private Integer requestedQty;
    private Integer availableStock;
}
