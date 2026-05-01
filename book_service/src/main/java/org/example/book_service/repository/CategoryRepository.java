package org.example.book_service.repository;

import org.example.book_service.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,String> {
    boolean existsBySlug(String slug);
    Optional<Category> findBySlug(String slug);
}
