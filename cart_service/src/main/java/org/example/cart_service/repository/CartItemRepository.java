package org.example.cart_service.repository;

import org.example.cart_service.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, String> {
    List<CartItem> findAllByCartId(String cartId);
    CartItem findByCartIdAndBookId(String cartId, String bookId);
}
