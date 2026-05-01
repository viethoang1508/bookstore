package org.example.order_service.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderResponse {
    private String orderId;

    private BigDecimal totalAmount;

    private BigDecimal discountAmount;

    private BigDecimal finalAmount;

    private String status;

    private List<OrderItemDTO> items;
}
