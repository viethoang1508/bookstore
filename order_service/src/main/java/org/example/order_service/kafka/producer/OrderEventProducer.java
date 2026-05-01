package org.example.order_service.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.example.order_service.kafka.event.OrderCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrderCreated(OrderCreatedEvent orderCreatedEvent) {
        kafkaTemplate.send("order-created", orderCreatedEvent);
    }
}
