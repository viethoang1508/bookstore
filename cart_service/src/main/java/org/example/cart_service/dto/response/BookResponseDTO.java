package org.example.cart_service.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookResponseDTO {
    private String bookId;
    private String bookName;
    private String image;
    private BigDecimal price;
    private Integer stock;
}
