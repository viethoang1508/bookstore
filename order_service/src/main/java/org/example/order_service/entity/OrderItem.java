package org.example.order_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "order_items")
public class OrderItem extends BaseEntity{

    @Id
    @GeneratedValue(generator = "uuid")
    @UuidGenerator
    private String id;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "book_id", nullable = false)
    private String bookId;

    @Column(name = "book_name")
    private String bookName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;
}
