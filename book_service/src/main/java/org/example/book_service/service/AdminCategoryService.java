package org.example.book_service.service;

import org.example.book_service.dto.request.CategoryDto;
import org.example.book_service.entity.Category;

public interface AdminCategoryService {
    Category createCategory(CategoryDto request);
    Category updateCategory(String id, CategoryDto request);
    void deleteCategory(String id);
}
