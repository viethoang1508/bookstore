package org.example.book_service.service;

import org.example.book_service.dto.request.BookCreateRequest;
import org.example.book_service.dto.request.BookUpdateRequest;
import org.example.book_service.entity.Book;
import org.springframework.data.domain.Page;

public interface AdminBookService {
    Book createBook(BookCreateRequest request);
    Book updateBook(BookUpdateRequest request, String id);
    void deleteBook(String id);
    Page<Book> getAllBooks();
    Book getBookById(String id);
}
