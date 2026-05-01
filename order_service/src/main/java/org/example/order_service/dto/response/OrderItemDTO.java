package org.example.order_service.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    private String bookId;

    private String bookName;

    private String image;

    private BigDecimal originalPrice;
    private BigDecimal price;

    private Integer quantity;

    private BigDecimal totalPrice;
}
