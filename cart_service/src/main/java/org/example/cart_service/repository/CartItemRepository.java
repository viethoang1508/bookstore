package org.example.cart_service.repository;

import org.example.cart_service.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, String> {
    List<CartItem> findAllByCartIdAndIsDeletedFalse(String cartId);
    CartItem findByCartIdAndBookIdAndIsDeletedFalse(String cartId, String bookId);
    CartItem findByCartIdAndIdAndIsDeletedFalse(String cartId, String id);
}
