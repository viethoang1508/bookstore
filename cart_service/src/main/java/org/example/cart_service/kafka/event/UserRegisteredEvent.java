package org.example.cart_service.kafka.event;

import lombok.Data;

@Data
public class UserRegisteredEvent {
    private String userId;
    private String username;
    private String email;
    private String fullName;
    private String phone;
}
