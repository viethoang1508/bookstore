package org.example.book_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.book_service.dto.response.BaseResponse;
import org.example.book_service.dto.request.BookCreateRequest;
import org.example.book_service.dto.request.BookUpdateRequest;
import org.example.book_service.entity.Book;
import org.example.book_service.service.AdminBookService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/books")
@RequiredArgsConstructor
public class AdminBookController {

    private final AdminBookService service;

    @PostMapping
    public ResponseEntity<BaseResponse<Book>> createBook(@RequestBody @Valid BookCreateRequest request){
        return ResponseEntity.ok(new BaseResponse<>(service.createBook(request), "Create book successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<Book>> updateBook(@RequestBody @Valid BookUpdateRequest request,
                                                         @PathVariable String id) {
        return ResponseEntity.ok(new BaseResponse<>(service.updateBook(request, id), "Update book successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteBook(@PathVariable String id) {
        service.deleteBook(id);
        return ResponseEntity.ok(new BaseResponse<>(null, "Delete book successfully"));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<Page<Book>>> getAllBooks() {
        return ResponseEntity.ok(new BaseResponse<>(service.getAllBooks(), ("Get all books successfully")));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<Book>> getBookById(@PathVariable String id) {
        return ResponseEntity.ok(new BaseResponse<>(service.getBookById(id), "Get book successfully"));
    }
}
