package org.example.user_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotBlank(message = "User id is required")
    private String id;

    @NotBlank(message = "Email is required")
    @Email(message = "Email is invalid")
    private String email;

    @Size(max = 255, message = "Full name must be <= 255 characters")
    private String fullName;

    @Size(max = 100, message = "Username must be <= 100 characters")
    private String username;

    @Pattern(
            regexp = "^(0|\\+84)[0-9]{9}$",
            message = "Phone number is invalid (Vietnam format)"
    )
    private String phone;
}
