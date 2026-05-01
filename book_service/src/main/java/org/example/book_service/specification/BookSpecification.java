package org.example.book_service.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.example.book_service.entity.Book;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BookSpecification {
    public static Specification<Book> filter(
            String keyword,
            String categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice
    ) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();
            // keyword
            if (keyword != null && !keyword.isBlank()) {
                predicates.add(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")),
                                "%" + keyword.toLowerCase() +"%")
                );
            }

            // CategoryId
            if (categoryId != null && !categoryId.isBlank()) {
                Join<Object, Object> categoryJoin = root.join("categories");
                predicates.add(
                        criteriaBuilder.equal(categoryJoin.get("id"), categoryId)
                );
            }

            // maxPrice
            if (maxPrice != null) {
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice)
                );
            }

            // minPrice
            if (minPrice != null) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice)
                );
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
