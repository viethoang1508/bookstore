package org.example.promotion_service.repository;

import org.example.promotion_service.dto.response.PromotionResponse;
import org.example.promotion_service.entity.Promotion;
import org.example.promotion_service.entity.PromotionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion,String> {
    List<Promotion> findByStatusAndStartTimeBeforeAndEndTimeAfter(PromotionStatus status, LocalDateTime startTimeBefore, LocalDateTime endTimeAfter);

    // Optional -> Wrapper: có thể có promotion hoặc là null
    Optional<Promotion> findByCode(String code);
}
