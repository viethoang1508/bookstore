package org.example.order_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.order_service.dto.response.BaseResponse;
import org.example.order_service.dto.request.PlaceOrderRequest;
import org.example.order_service.dto.response.CheckoutPreviewResponse;
import org.example.order_service.dto.response.OrderResponse;
import org.example.order_service.entity.BaseEntity;
import org.example.order_service.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/orders")
public class UserOrderController {

    private final OrderService orderService;

    // Preview order
    @PostMapping("/checkout-preview")
    public ResponseEntity<BaseEntity<CheckoutPreviewResponse>> preview (
            @RequestBody @Valid PlaceOrderRequest request,
            JwtAuthenticationToken jwtAuthenticationToken
    ){
        String userId = jwtAuthenticationToken.getName();
        return ResponseEntity.ok(new BaseResponse<>(orderService.preview(request,userId), "Preview order successfully"));
    }

    // Place order
    @PostMapping("/place")
    public ResponseEntity<BaseEntity<OrderResponse>> placeOrder (
            @RequestBody @Valid PlaceOrderRequest request,
            JwtAuthenticationToken jwtAuthenticationToken
    ){
        String userId = jwtAuthenticationToken.getName();
        return ResponseEntity.ok(new BaseResponse<>(orderService.placeOrder(request, userId), "Place order successfully"));
    }

    // Lấy toàn bộ order cá nhân
    @GetMapping
    public ResponseEntity<BaseResponse<List<OrderResponse>>> getAllOrders(JwtAuthenticationToken jwtAuthenticationToken) {
        String userId = jwtAuthenticationToken.getName();
        return ResponseEntity.ok(new BaseResponse<>(orderService.getAllOrders(userId), "Get all orders successfully"));
    }

    // Lấy order theo orderId
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<OrderResponse>> getOrderById (@PathVariable String id,
                                                                     JwtAuthenticationToken jwtAuthenticationToken) {
        String userId = jwtAuthenticationToken.getName();
        return ResponseEntity.ok(new BaseResponse<>(orderService.getOrderById(id, userId), "Get all orders successfully"));
    }
}
