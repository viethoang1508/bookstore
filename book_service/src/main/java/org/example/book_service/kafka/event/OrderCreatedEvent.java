package org.example.book_service.kafka.event;

import lombok.Data;
import org.example.book_service.dto.request.DeductStockRequest;

import java.util.List;

@Data
public class OrderCreatedEvent {
    private String orderId;
    private List<DeductStockRequest> deductRequests;
}
