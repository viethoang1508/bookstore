package org.example.book_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.book_service.dto.response.BaseResponse;
import org.example.book_service.dto.request.UpdateStockRequest;
import org.example.book_service.dto.response.InternalBookDTO;
import org.example.book_service.service.InternalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("internal")
@RequiredArgsConstructor
public class InternalController {

    private final InternalService internalService;

    @PatchMapping("/books/{id}/stock")
    public ResponseEntity<BaseResponse<Void>> updateStock(
            @PathVariable String id,
            @RequestBody @Valid UpdateStockRequest request
    ) {
        internalService.updateStock(id, request);
        return ResponseEntity.ok(new BaseResponse<>(null, "Update stock successfully"));
    }

    @PostMapping("/books/{bookId}")
    public ResponseEntity<BaseResponse<InternalBookDTO>> getBookForCart(@PathVariable String bookId){
        return ResponseEntity.ok(new BaseResponse<>(internalService.getBookForCart(bookId), "Get book successfully"));
    }

    @GetMapping("/books/existing")
    public ResponseEntity<BaseResponse<Set<String>>> checkIfBooksExist(@RequestBody List<String> bookIds){
        return ResponseEntity.ok(new BaseResponse<>(internalService.checkIfBooksExist(bookIds), "Finish checking book"));
    }
}
