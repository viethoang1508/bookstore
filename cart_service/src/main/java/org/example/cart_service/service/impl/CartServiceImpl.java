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
import org.example.cart_service.kafka.event.UserRegisteredEvent;
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
    private final CartRepository cartRepository;
    private final CartItemMapper cartItemMapper;
    private final BookClient bookClient;

    @Override
    public CartResponseDTO getCart(String userId) {
        log.info("Fetching cart, userId={}", userId);

        Cart cart = getOrCreateCart(userId);

        List<CartItem> items = cartItemRepository.findAllByCartId(cart.getId());

        List<CartItemDTO> itemDTOS = items.stream()
                .map(cartItemMapper::toCartItemDTO)
                .collect(Collectors.toList());

        CartResponseDTO cartResponseDTO = new CartResponseDTO();
        cartResponseDTO.setCartId(cart.getId());
        cartResponseDTO.setItems(itemDTOS);

        log.info("Fetched cart successfully, userId={}, itemsCount={}", userId, itemDTOS.size());
        return cartResponseDTO;
    }

    @Override
    public void addItemToCart(AddToCartRequest request, String userId) {
        log.info("Adding item to cart, userId={}, bookId={}", userId, request != null ? request.getBookId() : null);

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
        log.info("Item added to cart successfully, userId={}, bookId={}", userId, request.getBookId());

    }

    @Override
    public void updateItemInCart(String userId, String bookId, UpdateQuantityRequest request) {
        log.info("Updating cart item quantity, userId={}, bookId={}", userId, bookId);
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
        log.info("Updated cart item quantity successfully, userId={}, bookId={}, quantity={}", userId, bookId, quantity);
    }

    @Override
    public void deleteItemFromCart(String userId, String bookId) {
        log.info("Deleting item from cart, userId={}, bookId={}", userId, bookId);
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
            log.info("Deleted item from cart successfully, userId={}, bookId={}", userId, bookId);
        }
    }

    @Override
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        if (cartRepository.findByUserId(event.getUserId()).isPresent()) {
            log.info("Cart already exists for userId={}", event.getUserId());
            return;
        }

        Cart cart = getOrCreateCart(event.getUserId());
        log.info("Created cart from event for userId={}", event.getUserId());
    }

    // Hàm private

    private Cart getOrCreateCart(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ApplicationException("Invalid user id");
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);

                    return cartItemRepository.save(newCart);
                });
        return cart;
    }
}
