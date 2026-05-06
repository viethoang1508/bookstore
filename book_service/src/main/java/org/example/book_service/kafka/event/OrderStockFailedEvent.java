package org.example.book_service.kafka.event;

import lombok.Data;

import java.util.List;

@Data
public class OrderStockFailedEvent {
    private String orderId;
    private String reason;
    private List<StockFailureItem> failedItems;
}
