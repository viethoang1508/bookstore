package org.example.book_service.service;

import org.example.book_service.dto.request.UpdateStockRequest;
import org.example.book_service.dto.response.InternalBookDTO;
import org.example.book_service.kafka.event.OrderCreatedEvent;

import java.util.List;
import java.util.Set;

public interface InternalService {

    void updateStock(String bookId, UpdateStockRequest request);
    InternalBookDTO getBookForCart(String bookId);
    Set<String> checkIfBooksExist(List<String> bookIds);
    void deductBooks(OrderCreatedEvent orderCreatedEvent);
}
