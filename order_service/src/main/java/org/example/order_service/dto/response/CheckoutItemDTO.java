package org.example.order_service.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CheckoutItemDTO {
    private String bookId;
    private String bookName;
    private String image;

    private BigDecimal price;             // giá gốc
    private BigDecimal discountedPrice;   // giá sau giảm

    private Integer quantity;
    private BigDecimal subTotal;
}
