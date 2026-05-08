package org.example.cart_service.service;

import org.example.cart_service.dto.request.AddToCartRequest;
import org.example.cart_service.dto.request.UpdateQuantityRequest;
import org.example.cart_service.dto.response.CartResponseDTO;
import org.example.cart_service.kafka.event.UserRegisteredEvent;

public interface CartService {
    CartResponseDTO getCart(String userId);

    Void addItemToCart(AddToCartRequest request, String userId);

    Void updateItemInCart(String userId, String bookId, UpdateQuantityRequest request);

    Void deleteItemFromCart(String userId, String bookId);

    Void handleUserRegisteredEvent(UserRegisteredEvent event);
}
