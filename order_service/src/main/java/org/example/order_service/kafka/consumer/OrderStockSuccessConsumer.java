package org.example.order_service.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.order_service.kafka.event.OrderStockDeductedEvent;
import org.example.order_service.service.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderStockSuccessConsumer {

    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-stock-deducted")
    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 2000, multiplier = 2),
            exclude = {NullPointerException.class, IllegalArgumentException.class}
    )

    public void consume(String json) throws JsonProcessingException {
        OrderStockDeductedEvent event = objectMapper.readValue(json, OrderStockDeductedEvent.class);

        if (event.getOrderId() == null || event.getItems() == null) {
            log.error("Invalid event received");
            return;
        }

        orderService.handleStockDeductedEvent(event);
    }
}
