package org.example.cart_service.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cart_service.kafka.event.UserRegisteredEvent;
import org.example.cart_service.service.CartService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegisteredConsumer {

    private final CartService cartService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "user-registered")
    @RetryableTopic (
            attempts = "4",
            backoff = @Backoff(delay = 2000, multiplier = 2),
            exclude = {NullPointerException.class, IllegalArgumentException.class}
    )

    public void consume(String json)  throws JsonProcessingException {
        log.info("Received user-registered event: {}", json);

        UserRegisteredEvent event =
                objectMapper.readValue(json, UserRegisteredEvent.class);

        if (event.getUserId() == null || event.getUserId().isBlank()) {
            log.warn("Skip user-registered event because userId is empty");
            return;
        }

        cartService.handleUserRegisteredEvent(event);

        log.info("Handled user-registered event for userId={}",
                event.getUserId());
    }
}
