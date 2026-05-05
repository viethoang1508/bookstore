package org.example.cart_service.service;

import org.example.cart_service.dto.request.AddToCartRequest;
import org.example.cart_service.dto.request.UpdateQuantityRequest;
import org.example.cart_service.dto.response.CartResponseDTO;
import org.example.cart_service.kafka.event.UserRegisteredEvent;

public interface CartService {
    CartResponseDTO getCart(String userId);

    void addItemToCart(AddToCartRequest request, String userId);

    void updateItemInCart(String userId, String bookId, UpdateQuantityRequest request);

    void deleteItemFromCart(String userId, String bookId);

    void handleUserRegisteredEvent(UserRegisteredEvent event);
}
