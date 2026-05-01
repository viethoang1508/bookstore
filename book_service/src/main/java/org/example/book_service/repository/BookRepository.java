package org.example.book_service.repository;

import org.example.book_service.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface BookRepository extends JpaRepository<Book,String>, JpaSpecificationExecutor<Book> {
    boolean existsByIsbn(String isbn);
    boolean existsBySlug(String slug);
    @Query(
            "SELECT b.id FROM Book b " +
                    "WHERE b.id IN :bookIds"
    )
    Set<String> findExistingBookIds(List<String> bookIds);

    List<Book> findAllByIdIn(List<String> bookIds);
}
