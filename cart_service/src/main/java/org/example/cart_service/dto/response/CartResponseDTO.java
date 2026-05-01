package org.example.cart_service.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class CartResponseDTO {
    private String cartId;
    private List<CartItemDTO> items;
}
