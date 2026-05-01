package org.example.order_service.controller;

import lombok.RequiredArgsConstructor;
import org.example.order_service.dto.response.BaseResponse;
import org.example.order_service.dto.response.OrderResponse;
import org.example.order_service.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    // Lấy toàn bộ orders
    @GetMapping
    public ResponseEntity<BaseResponse<List<OrderResponse>>> getAllOrders(){
        return ResponseEntity.ok(
                new BaseResponse<>(orderService.getAllOrdersAdmin())
        );
    }

    // Update status
    @PutMapping("/{orderId}/status")
    public ResponseEntity<BaseResponse<Void>> updateStatus(
            @PathVariable String orderId,
            @RequestParam String status
    ){
        orderService.updateStatus(orderId, status);
        return ResponseEntity.ok(new BaseResponse<>());
    }
}
