package org.example.book_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.book_service.dto.request.CategoryRequest;
import org.example.book_service.entity.Category;
import org.example.book_service.exception.ApplicationException;
import org.example.book_service.repository.CategoryRepository;
import org.example.book_service.service.AdminCategoryService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCategoryServiceImpl implements AdminCategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category createCategory(CategoryRequest request) {
        log.info("Creating category, name={}", request != null ? request.getName() : null);
        validateRequest(request);

        Category category = new Category();
        category.setName(request.getName().trim());
        category.setSlug(resolveSlug(request.getSlug(), request.getName()));

        Category newCategory = categoryRepository.save(category);

        log.info("Category created successfully, id={}", newCategory.getId());
        return newCategory;
    }

    @Override
    public Category updateCategory(String id, CategoryRequest request) {
        log.info("Updating category, id={}", id);
        validateId(id);
        validateRequest(request);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Category not found"));

        category.setName(request.getName().trim());
        category.setSlug(resolveSlug(request.getSlug(), request.getName(), category.getId()));

        Category newCategory = categoryRepository.save(category);

        log.info("Category updated successfully, id={}", newCategory.getId());
        return newCategory;
    }

    @Override
    public void deleteCategory(String id) {
        log.info("Deleting category, id={}", id);
        validateId(id);
        if (!categoryRepository.existsById(id)) {
            log.warn("Delete category rejected because category not found, id={}", id);
            throw new ApplicationException("Category not found");
        }
        categoryRepository.deleteById(id);
        log.info("Category deleted successfully, id={}", id);
    }

    private void validateRequest(CategoryRequest request) {
        if (request == null) {
            throw new ApplicationException("Request is empty");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ApplicationException("Category name is required");
        }
    }

    private void validateId(String id) {
        if (id == null || id.isBlank()) {
            throw new ApplicationException("Category id is empty");
        }
    }

    private String resolveSlug(String slug, String name) {
        return resolveSlug(slug, name, null);
    }

    private String resolveSlug(String slug, String name, String currentCategoryId) {
        String baseSlug = (slug != null && !slug.isBlank())
                ? slug.trim().toLowerCase()
                : toSlug(name);
        if (baseSlug.isBlank()) {
            throw new ApplicationException("Slug is invalid");
        }

        String candidate = baseSlug;
        int suffix = 1;
        while (categoryRepository.existsBySlug(candidate)) {
            if (currentCategoryId != null) {
                Category existed = categoryRepository.findBySlug(candidate).orElse(null);
                if (existed != null && currentCategoryId.equals(existed.getId())) {
                    break;
                }
            }
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
