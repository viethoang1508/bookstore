package org.example.book_service.service;

import org.example.book_service.dto.request.BookSearchRequest;
import org.example.book_service.dto.response.BookSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CatalogService {
    Page<BookSummaryDTO> getBooks(BookSearchRequest request);
    BookSummaryDTO getBookById(String id);
    Page<BookSummaryDTO> getBooksByCategory(String categoryId);
    List<String> getAllCategories();
}
