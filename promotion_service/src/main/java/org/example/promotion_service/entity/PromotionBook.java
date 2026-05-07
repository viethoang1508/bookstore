package org.example.promotion_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "promotion_books")
public class PromotionBook {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    private String promotionId;

    private String bookId;
}
