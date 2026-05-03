package org.example.user_service.dto.response;

import lombok.Data;

@Data
public class UserResponse {
    private String id;
    private String email;
    private String fullName;
    private String username;
    private String phone;
    private String status;
}
