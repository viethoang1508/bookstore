package org.example.cart_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "cart_items")
public class CartItem extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "cart_id")
    private String cartId;

    @Column(name = "book_id")
    private String bookId;

    // Snapshot Data

    @Column(name = "book_name")
    private String bookName;

    @Column(name = "image")
    private String image;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    private Integer quantity;
}
