package org.example.promotion_service.repository;

import org.example.promotion_service.entity.PromotionBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromotionBookRepository extends JpaRepository<PromotionBook,String> {
    List<PromotionBook> findByPromotionIdAndBookIdIn(String promotionId, List<String> bookIds);
    List<PromotionBook> findByPromotionId(String promotionId);
}
