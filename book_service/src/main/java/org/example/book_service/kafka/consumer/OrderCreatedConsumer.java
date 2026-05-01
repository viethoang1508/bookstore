package org.example.book_service.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.book_service.kafka.event.OrderCreatedEvent;
import org.example.book_service.service.InternalService;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedConsumer {

    private final ObjectMapper objectMapper;
    private final InternalService internalService;

    @KafkaListener(topics = "order-created")
    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 2000, multiplier = 2),
            exclude = {NullPointerException.class, IllegalArgumentException.class}
    )

    public void handleOrderCreatedEvent(String json) throws JsonProcessingException {
        OrderCreatedEvent orderCreatedEvent = objectMapper.readValue(json, OrderCreatedEvent.class);

        if(orderCreatedEvent.getDeductRequests() == null || orderCreatedEvent.getDeductRequests().isEmpty()) {
            log.warn("No items in order created, orderId={}", orderCreatedEvent.getOrderId());
            return;
        }
        internalService.deductBooks(orderCreatedEvent);
    }
}
