package org.example.order_service.kafka.event;

import lombok.Data;
import org.example.book_service.dto.request.DeductStockRequest;

import java.util.List;

@Data
public class OrderStockDeductedEvent {
    private String orderId;
    private List<DeductStockRequest> items;
    private String status; // SUCCESS
}
