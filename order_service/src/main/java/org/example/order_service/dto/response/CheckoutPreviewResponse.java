package org.example.order_service.dto.response;

import lombok.Data;
import org.example.order_service.dto.request.OrderItemRequest;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CheckoutPreviewResponse {
    private List<CheckoutItemDTO> items;
    private String promotionCode;
    private BigDecimal totalPrice;
    private BigDecimal discountAmount;
    private BigDecimal finalPrice;
}
