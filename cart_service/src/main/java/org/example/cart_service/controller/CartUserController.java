package org.example.cart_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cart_service.dto.BaseResponse;
import org.example.cart_service.dto.request.AddToCartRequest;
import org.example.cart_service.dto.request.UpdateQuantityRequest;
import org.example.cart_service.dto.response.CartResponseDTO;
import org.example.cart_service.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/cart")
@RequiredArgsConstructor
public class CartUserController {

    private final CartService cartService;

    // Lấy giỏ hàng
    @GetMapping
    public ResponseEntity<BaseResponse<CartResponseDTO>> getCart(
            JwtAuthenticationToken authenticationToken
    ){
        String userId = authenticationToken.getToken().getSubject();
        return ResponseEntity.ok(new BaseResponse<>(cartService.getCart(userId)));
    }

    // Thêm items
    @PostMapping("/items")
    public ResponseEntity<BaseResponse<Void>> addItemToCart(
            @RequestBody @Valid AddToCartRequest request,
            JwtAuthenticationToken authenticationToken
    ){
        String userId = authenticationToken.getToken().getSubject();
        return ResponseEntity.ok(new BaseResponse<>(cartService.addItemToCart(request, userId)));
    }

    // Update quantity
    @PutMapping("/items/{bookId}")
    public ResponseEntity<BaseResponse<Void>> updateItemInCart(
            @PathVariable String bookId,
            @RequestBody @Valid UpdateQuantityRequest request,
            JwtAuthenticationToken authenticationToken
    ){
        String userId = authenticationToken.getToken().getSubject();
        return ResponseEntity.ok(new BaseResponse<>(cartService.updateItemInCart(bookId, userId, request)));
    }

    // Xóa items
    @DeleteMapping("/items/{bookId}")
    public ResponseEntity<BaseResponse<Void>> deleteItemFromCart(
            @PathVariable String bookId,
            JwtAuthenticationToken authenticationToken
    ){
        String userId = authenticationToken.getToken().getSubject();
        return ResponseEntity.ok(new BaseResponse<>(cartService.deleteItemFromCart(bookId, userId)));
    }
}
