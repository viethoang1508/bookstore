package org.example.cart_service.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
public class UpdateQuantityRequest {
    @NotNull(message = "quantity không được để trống")
    @Positive(message = "quantity phải lớn hơn 0")
    private Integer quantity;
}