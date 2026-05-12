package org.example.user_service.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.user_service.dto.request.CreateUserRequest;
import org.example.user_service.kafka.event.UserRegisteredEvent;
import org.example.user_service.service.UserService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegisteredConsumer {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "user-registered")
    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 2000, multiplier = 2),
            exclude = {NullPointerException.class, IllegalArgumentException.class}
    )

    public void consume(String json) throws JsonProcessingException {
        log.info("Received message: {}", json);

        UserRegisteredEvent event =
                objectMapper.readValue(json, UserRegisteredEvent.class);

        if (event.getUserId() == null || event.getUserId().isBlank()) {
            log.warn("Skip user-registered event because userId is empty");
            return;
        }

        CreateUserRequest request = new CreateUserRequest();

        request.setId(event.getUserId());
        request.setUsername(event.getUsername());
        request.setEmail(event.getEmail());
        request.setFullName(event.getFullName());
        request.setPhone(event.getPhone());

        userService.createProfile(request);

        log.info("Created profile for userId={}", event.getUserId());
    }
}
