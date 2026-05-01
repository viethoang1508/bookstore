package org.example.order_service.service;

import org.example.order_service.dto.request.PlaceOrderRequest;
import org.example.order_service.dto.response.CheckoutPreviewResponse;
import org.example.order_service.dto.response.OrderResponse;
import org.example.order_service.kafka.event.OrderStockDeductedEvent;
import org.example.order_service.kafka.event.OrderStockFailedEvent;

import java.util.List;

public interface OrderService {
    CheckoutPreviewResponse preview(PlaceOrderRequest request, String userId);
    OrderResponse placeOrder(PlaceOrderRequest request, String userId);
    void handleStockDeductedEvent(OrderStockDeductedEvent event);
    void handleStockFailedEvent(OrderStockFailedEvent event);
    List<OrderResponse> getAllOrders(String userId);
    OrderResponse getOrderById(String orderId, String userId);

}
