package org.example.book_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.book_service.dto.request.BookSearchRequest;
import org.example.book_service.dto.response.BookSummaryDTO;
import org.example.book_service.entity.Book;
import org.example.book_service.entity.Category;
import org.example.book_service.exception.ApplicationException;
import org.example.book_service.repository.BookRepository;
import org.example.book_service.repository.CategoryRepository;
import org.example.book_service.service.CatalogService;
import org.example.book_service.specification.BookSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CatalogServiceImpl implements CatalogService {
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    @Override
    public Page<BookSummaryDTO> getBooks(BookSearchRequest request) {
        log.info("Searching books, keyword={}, categoryId={}", request.getKeyword(), request.getCategoryId());
        Pageable pageable = request.toPageable();

        // Specification
        Specification<Book> spec = BookSpecification.filter(
                request.getKeyword(),
                request.getCategoryId(),
                request.getMinPrice(),
                request.getMaxPrice()
        );

        // Query
        Page<Book> bookPage = bookRepository.findAll(spec, pageable);

        // Map sang DTO
        return bookPage.map(this::toSummaryDTO);
    }

    private BookSummaryDTO toSummaryDTO(Book book) {
        BookSummaryDTO bookSummaryDTO = new BookSummaryDTO();
        bookSummaryDTO.setId(book.getBookId());
        bookSummaryDTO.setTitle(book.getTitle());
        bookSummaryDTO.setSlug(book.getSlug());
        bookSummaryDTO.setPrice(book.getPrice());
        bookSummaryDTO.setThumbnailUrl(book.getThumbnailUrl());
        return bookSummaryDTO;
    }

    @Override
    public BookSummaryDTO getBookById(String id) {
        log.info("Fetching catalog book by id={}", id);
        if (id == null || id.isEmpty()) {
            log.warn("Fetch catalog book rejected because id is empty");
            throw new ApplicationException("Id is empty");
        }

        Book book = bookRepository.findById(id).orElseThrow(() -> new ApplicationException("Book not found"));

        BookSummaryDTO bookSummaryDTO = new BookSummaryDTO();
        bookSummaryDTO.setId(book.getBookId());
        bookSummaryDTO.setTitle(book.getTitle());
        bookSummaryDTO.setSlug(book.getSlug());
        bookSummaryDTO.setPrice(book.getPrice());
        bookSummaryDTO.setThumbnailUrl(book.getThumbnailUrl());

        return bookSummaryDTO;
    }

    @Override
    public Page<BookSummaryDTO> getBooksByCategory(String categoryId) {
        log.info("Fetching books by category, categoryId={}", categoryId);
        if (categoryId == null || categoryId.isBlank()) {
            log.warn("Fetch books by category rejected because categoryId is empty");
            throw new ApplicationException("Category id is empty");
        }
        if (!categoryRepository.existsById(categoryId)) {
            log.warn("Fetch books by category rejected because category not found, categoryId={}", categoryId);
            throw new ApplicationException("Category not found");
        }

        BookSearchRequest request = new BookSearchRequest();
        request.setCategoryId(categoryId);
        return getBooks(request);
    }

    @Override
    public List<String> getAllCategories() {
        log.info("Fetching all categories");
        return categoryRepository.findAll()
        .stream()
        .map(Category::getName)
        .toList();
    }

}
