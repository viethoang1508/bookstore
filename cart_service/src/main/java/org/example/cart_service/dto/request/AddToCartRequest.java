package org.example.cart_service.dto.request;

import lombok.Data;

@Data
public class AddToCartRequest {
    private String bookId;
    private Integer quantity;

//    private String bookName;
//    private String image;
//    private Double price;
}
