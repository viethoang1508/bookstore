package org.example.book_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.book_service.dto.request.BookCreateRequest;
import org.example.book_service.dto.request.BookUpdateRequest;
import org.example.book_service.entity.Book;
import org.example.book_service.exception.ApplicationException;
import org.example.book_service.repository.BookRepository;
import org.example.book_service.service.AdminBookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminBookServiceImpl implements AdminBookService {

    private final BookRepository bookRepository;

    @Override
    public Book createBook(BookCreateRequest request) {
        log.info("Creating book, title={}, isbn={}", request != null ? request.getTitle() : null, request != null ? request.getIsbn() : null);
        validateCreateRequest(request);
        if (request.getIsbn() != null && !request.getIsbn().isBlank() && bookRepository.existsByIsbn(request.getIsbn())) {
            log.warn("Create book rejected because ISBN already exists, isbn={}", request.getIsbn());
            throw new ApplicationException("ISBN already exists");
        }

        Book book = new Book();
        applyCreateFields(book, request);
        Book savedBook = bookRepository.save(book);
        log.info("Book created successfully, id={}", savedBook.getId());
        return savedBook;
    }

    @Override
    public Book updateBook(BookUpdateRequest request, String id) {
        log.info("Updating book, id={}", id);
        Book book = getExistingBook(id);
        applyUpdateFields(book, request);
        Book updatedBook = bookRepository.save(book);
        log.info("Book updated successfully, id={}", id);
        return updatedBook;
    }

    @Override
    public void deleteBook(String id) {
        log.info("Deleting book, id={}", id);
        Book book = getExistingBook(id);
        bookRepository.delete(book);
        log.info("Book deleted successfully, id={}", id);
    }

    @Override
    public Page<Book> getAllBooks() {
        log.info("Fetching all books with default pagination");
        return bookRepository.findAll(
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
    }

    @Override
    public Book getBookById(String id) {
        log.info("Fetching book by id={}", id);
        return getExistingBook(id);
    }

    private Book getExistingBook(String id) {
        if (id == null || id.isBlank()) {
            throw new ApplicationException("Id is empty");
        }
        return bookRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Book not found"));
    }

    private void validateCreateRequest(BookCreateRequest request) {
        if (request == null) {
            throw new ApplicationException("Request is empty");
        }
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new ApplicationException("Title is required");
        }
        if (request.getPrice() == null) {
            throw new ApplicationException("Price is required");
        }
        if (request.getStock() == null) {
            throw new ApplicationException("Stock is required");
        }
    }

    private void applyCreateFields(Book book, BookCreateRequest request) {
        book.setIsbn(request.getIsbn());
        book.setTitle(request.getTitle());
        book.setSlug(resolveSlug(request.getSlug(), request.getTitle()));
        book.setDescription(request.getDescription());
        book.setAuthorName(request.getAuthorName());
        book.setPublisherName(request.getPublisherName());
        book.setPublishYear(request.getPublishYear());
        book.setPrice(request.getPrice());
        book.setStock(request.getStock());
        book.setThumbnailUrl(request.getThumbnailUrl());
    }

    private void applyUpdateFields(Book book, BookUpdateRequest request) {
        if (request == null) {
            throw new ApplicationException("Request is empty");
        }

        if (request.getIsbn() != null) {
            book.setIsbn(request.getIsbn());
        }
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            book.setTitle(request.getTitle());
        }
        if (request.getSlug() != null) {
            book.setSlug(resolveSlug(request.getSlug(), book.getTitle()));
        }
        if (request.getDescription() != null) {
            book.setDescription(request.getDescription());
        }
        if (request.getAuthorName() != null) {
            book.setAuthorName(request.getAuthorName());
        }
        if (request.getPublisherName() != null) {
            book.setPublisherName(request.getPublisherName());
        }
        if (request.getPublishYear() != null) {
            book.setPublishYear(request.getPublishYear());
        }
        if (request.getPrice() != null) {
            book.setPrice(request.getPrice());
        }
        if (request.getStock() != null) {
            book.setStock(request.getStock());
        }
        if (request.getThumbnailUrl() != null) {
            book.setThumbnailUrl(request.getThumbnailUrl());
        }
    }

    private String resolveSlug(String slug, String title) {
        String baseSlug = (slug != null && !slug.isBlank())
                ? slug.trim().toLowerCase()
                : toSlug(title);
        if (baseSlug == null || baseSlug.isBlank()) {
            throw new ApplicationException("Slug is invalid");
        }

        String candidate = baseSlug;
        int suffix = 1;
        while (bookRepository.existsBySlug(candidate)) {
            candidate = baseSlug + "-" + suffix++;
        }
        return candidate;
    }

    private String toSlug(String input) {
        if (input == null) {
            return "";
        }
        return input.trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-{2,}", "-");
    }
}
