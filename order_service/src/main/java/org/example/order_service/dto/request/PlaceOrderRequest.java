package org.example.order_service.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class PlaceOrderRequest {
    @NotEmpty(message = "items must not be empty")
    @Valid
    private List<OrderItemRequest> items;

    private String promotionCode;

    @NotBlank(message = "receiverName must not be blank")
    private String receiverName;

    @NotBlank(message = "receiverPhone must not be blank")
    private String receiverPhone;

    @NotBlank(message = "shippingAddress must not be blank")
    private String shippingAddress;

    private String note;
}