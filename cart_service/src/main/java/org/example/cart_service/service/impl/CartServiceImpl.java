package org.example.cart_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cart_service.client.BookClient;
import org.example.cart_service.dto.request.AddToCartRequest;
import org.example.cart_service.dto.request.UpdateQuantityRequest;
import org.example.cart_service.dto.response.BookResponseDTO;
import org.example.cart_service.dto.response.CartItemDTO;
import org.example.cart_service.dto.response.CartResponseDTO;
import org.example.cart_service.entity.Cart;
import org.example.cart_service.entity.CartItem;
import org.example.cart_service.exception.ApplicationException;
import org.example.cart_service.mapper.CartItemMapper;
import org.example.cart_service.repository.CartItemRepository;
import org.example.cart_service.repository.CartRepository;
import org.example.cart_service.service.CartService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartResponsitory;
    private final CartItemMapper cartItemMapper;
    private final BookClient bookClient;

    @Override
    public CartResponseDTO getCart(String userId) {

        Cart cart = getOrCreateCart(userId);

        List<CartItem> items = cartItemRepository.findAllByCartId(cart.getId());

        List<CartItemDTO> itemDTOS = items.stream()
                .map(cartItemMapper::toCartItemDTO)
                .collect(Collectors.toList());

        CartResponseDTO cartResponseDTO = new CartResponseDTO();
        cartResponseDTO.setCartId(cart.getId());
        cartResponseDTO.setItems(itemDTOS);

        return cartResponseDTO;
    }

    @Override
    public void addItem(AddToCartRequest request, String userId) {

        if (userId == null || userId.isBlank()) {
            throw new ApplicationException("Invalid user id");
        }

        if (request == null) {
            throw new ApplicationException("Invalid request");
        }

        Cart cart = getOrCreateCart(userId);

        CartItem cartItem = cartItemRepository.findByCartIdAndBookId(cart.getId(), request.getBookId());

        if (cartItem == null) {

            // Gọi sang Book Service
            BookResponseDTO book = bookClient.getBook(request.getBookId());

            cartItem = new CartItem();
            cartItem.setCartId(cart.getId());
            cartItem.setBookId(request.getBookId());
            cartItem.setQuantity(request.getQuantity());

            cartItem.setBookName(book.getBookName());
            cartItem.setPrice(book.getPrice());
            cartItem.setImage(book.getImage());


        } else {
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        }

        cartItemRepository.save(cartItem);

    }

    @Override
    public void updateItemInCart(String userId, String bookId, UpdateQuantityRequest request) {
        if (userId == null || userId.isBlank()) {
            throw new ApplicationException("Invalid user id");
        }

        if (request == null) {
            throw new ApplicationException("Invalid request");
        }

        if (bookId == null || bookId.isBlank()) {
            throw new ApplicationException("Invalid book id");
        }

        Integer quantity = request.getQuantity();

        Cart cart = getOrCreateCart(userId);

        CartItem cartItem = cartItemRepository.findByCartIdAndBookId(cart.getId(), bookId);

        if (cartItem == null) {
            throw new ApplicationException("Item not found with id " + bookId);
        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
    }

    @Override
    public void deleteItemFromCart(String userId, String bookId) {
        if (userId == null || userId.isBlank()) {
            throw new ApplicationException("Invalid user id");
        }

        if (bookId == null || bookId.isBlank()) {
            throw new ApplicationException("Invalid book id");
        }

        Cart cart = getOrCreateCart(userId);

        CartItem cartItem = cartItemRepository.findByCartIdAndBookId(cart.getId(), bookId);

        if (cartItem == null) {
            throw new ApplicationException("Item not found with id " + bookId);
        }else {
            cartItem.setIsDeleted(true);
            cartItemRepository.save(cartItem);
        }
    }

    // Hàm private

    private Cart getOrCreateCart(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ApplicationException("Invalid user id");
        }

        Cart cart = cartResponsitory.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);

                    return cartItemRepository.save(newCart);
                });
        return cart;
    }
}
