package org.example.order_service.client;

import org.example.order_service.dto.response.BookDTO;

import java.util.List;

public interface BookClient {
    List<BookDTO> getBooksInfo(List<String> bookIds);
}
