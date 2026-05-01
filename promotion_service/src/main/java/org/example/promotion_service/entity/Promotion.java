package org.example.promotion_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "promotions")
public class Promotion extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true)
    private String code;

    private String name;

    @Column(nullable = false)
    private String scope; // ITEM - ORDER

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PromotionType type;

    @Column(nullable = false)
    private BigDecimal value;

    private BigDecimal maxDiscount;

    private BigDecimal minOrderValue;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private PromotionStatus status;
}
