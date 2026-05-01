package org.example.book_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.book_service.dto.BaseResponse;
import org.example.book_service.dto.request.BookSearchRequest;
import org.example.book_service.dto.response.BookSummaryDTO;
import org.example.book_service.service.CatalogService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/catalog")
@RequiredArgsConstructor
public class PublicBookController {

    private final CatalogService catalogService;

    @GetMapping
    public ResponseEntity<BaseResponse<Page<BookSummaryDTO>>> getBooks(@RequestBody @Valid BookSearchRequest request) {
        return ResponseEntity.ok(new BaseResponse<>(catalogService.getBooks(request), "Get catalog successfully"));
    }

    @GetMapping("/id")
    public ResponseEntity<BaseResponse<BookSummaryDTO>> getBookById(@PathVariable String id) {
        return ResponseEntity.ok(new BaseResponse<>(catalogService.getBookById(id), "Get book information successfully"));
    }

    @GetMapping("/categories/id")
    public ResponseEntity<BaseResponse<Page<BookSummaryDTO>>> getBooksByCategory(@RequestParam String id) {
        return ResponseEntity.ok(new BaseResponse<>(catalogService.getBooksByCategory(id), "Get all books in this category successfully"));
    }

    @GetMapping("/categories")
    public ResponseEntity<BaseResponse<List<String>>> getAllCategories() {
        return ResponseEntity.ok(new BaseResponse<>(catalogService.getAllCategories(), "Get all categories successfully"));
    }
}
