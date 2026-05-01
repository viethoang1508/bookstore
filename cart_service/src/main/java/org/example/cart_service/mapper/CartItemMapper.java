package org.example.cart_service.mapper;

import org.example.cart_service.dto.response.CartItemDTO;
import org.example.cart_service.entity.CartItem;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper (componentModel = "spring")
public interface CartItemMapper {
    CartItemDTO toCartItemDTO(CartItem cartItem);
    CartItem toCartItem(CartItemDTO cartItemDTO);
}
