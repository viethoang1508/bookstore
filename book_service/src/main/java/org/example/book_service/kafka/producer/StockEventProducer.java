package org.example.book_service.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.example.book_service.kafka.event.OrderStockDeductedEvent;
import org.example.book_service.kafka.event.OrderStockFailedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishStockFailed(OrderStockFailedEvent event) {
        kafkaTemplate.send("order-stock-failed", event);
    }

    public void publishStockDeducted(OrderStockDeductedEvent event) {
        kafkaTemplate.send("order-stock-deducted", event);
    }
}