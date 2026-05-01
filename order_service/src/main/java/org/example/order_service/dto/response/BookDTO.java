package org.example.order_service.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookDTO {
    private String bookId;
    private String bookName;
    private String image;
    private BigDecimal price;
    private Integer stock;
}
