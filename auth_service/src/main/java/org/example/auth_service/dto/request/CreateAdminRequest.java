package org.example.auth_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateAdminRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email is invalid")
    private String email;

    @Size(max = 255, message = "Full name must be <= 255 characters")
    private String fullName;

    @NotBlank(message = "Username is required")
    @Size(max = 100, message = "Username must be <= 100 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(max = 100, message = "Password must be <= 100 characters")
    private String password;

    @Pattern(
            regexp = "^(0|\\+84)[0-9]{9}$",
            message = "Phone number is invalid (Vietnam format)"
    )
    private String phone;
}