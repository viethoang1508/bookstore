package org.example.cart_service.client;

import org.example.cart_service.dto.response.BookResponseDTO;

public interface BookClient {
    BookResponseDTO getBook(String bookId);
}
