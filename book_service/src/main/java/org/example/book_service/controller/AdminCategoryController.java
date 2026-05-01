package org.example.book_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.book_service.dto.BaseResponse;
import org.example.book_service.dto.request.CategoryDto;
import org.example.book_service.entity.Category;
import org.example.book_service.service.AdminCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;

    @PostMapping
    public ResponseEntity<BaseResponse<Category>> createCategory(@RequestBody @Valid CategoryDto request) {
        return ResponseEntity.ok(new BaseResponse<>(adminCategoryService.createCategory(request), "Create category successfully"));
    }

    @PutMapping("{id}")
    public ResponseEntity<BaseResponse<Category>> updateCategory(@PathVariable String id,
                                                             @RequestBody @Valid CategoryDto request)
    {
        return ResponseEntity.ok(new BaseResponse<>(adminCategoryService.updateCategory(id, request), "Update category successfully"));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<BaseResponse<Void>> deleteCategory(@PathVariable String id) {
        adminCategoryService.deleteCategory(id);
        return ResponseEntity.ok(new BaseResponse<>(null, "Delete category successfully"));
    }
}
