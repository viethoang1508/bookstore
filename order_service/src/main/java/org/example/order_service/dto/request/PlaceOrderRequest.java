package org.example.order_service.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class PlaceOrderRequest {
    private List<OrderItemRequest> items;
    private String promotionCode;

    private String receiverName;
    private String receiverPhone;
    private String shippingAddress;

    private String note;
}
