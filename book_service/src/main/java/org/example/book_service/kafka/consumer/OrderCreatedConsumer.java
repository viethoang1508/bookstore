package org.example.book_service.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.book_service.kafka.event.OrderCreatedEvent;
import org.example.book_service.service.InternalService;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedConsumer {

    private final ObjectMapper objectMapper;
    private final InternalService internalService;

    @KafkaListener(topics = "order-created")
    @RetryableTopic(
            attempts = "4",
            backOff = @BackOff(delay = 2000, multiplier = 2),
            exclude = {NullPointerException.class, IllegalArgumentException.class}
    )

    public void handleOrderCreatedEvent(String json) throws JsonProcessingException {
        OrderCreatedEvent orderCreatedEvent = objectMapper.readValue(json, OrderCreatedEvent.class);

        if (orderCreatedEvent.getOrderId() == null || orderCreatedEvent.getOrderId().isBlank()
                || orderCreatedEvent.getDeductRequests() == null
                || orderCreatedEvent.getDeductRequests().isEmpty()) {
            throw new IllegalArgumentException("Invalid order-created event: " + json);
        }

        internalService.deductBooks(orderCreatedEvent);
    }
}
