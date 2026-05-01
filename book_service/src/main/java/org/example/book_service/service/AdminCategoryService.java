package org.example.book_service.service;

import org.example.book_service.dto.request.CategoryRequest;
import org.example.book_service.entity.Category;

public interface AdminCategoryService {
    Category createCategory(CategoryRequest request);
    Category updateCategory(String id, CategoryRequest request);
    void deleteCategory(String id);
}
